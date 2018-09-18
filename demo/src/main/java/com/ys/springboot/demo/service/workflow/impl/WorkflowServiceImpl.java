package com.ys.springboot.demo.service.workflow.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ys.springboot.demo.Constans.GlobalConstants;
import com.ys.springboot.demo.mapper.HisTaskInfoMapper;
import com.ys.springboot.demo.mapper.TaskModelMapper;
import com.ys.springboot.demo.po.HisTaskInfo;
import com.ys.springboot.demo.po.TaskModel;
import com.ys.springboot.demo.service.workflow.WorkflowService;
import com.ys.springboot.demo.utils.*;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkflowServiceImpl implements WorkflowService {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IdentityService identityservice;

    @Autowired
    private RuntimeService runtimeservice;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private TaskModelMapper taskModelMapper;

    @Autowired
    private HisTaskInfoMapper hisTaskInfoMapper;


    @Override
    @Transactional
    public ReturnData startWorkflow(String flowName, String businesskey, Map<String, Object> variables, String userId,String owner,String tenantId) {
        //参数验证
        if(StringUtils.isEmpty(flowName)){
            return ReturnData.returnParmeterError("流程名称有误！");
        }

        if(StringUtils.isEmpty(businesskey)){
            return ReturnData.returnParmeterError("业务ID有误！");
        }

        AssertUtils.notNull(variables,"用户ID有误！");

        if(StringUtils.isEmpty(owner)){
            return ReturnData.returnParmeterError("任务创建人有误！");
        }

        if(StringUtils.isNotBlank(userId)){
            identityservice.setAuthenticatedUserId(userId); //用户授权
        }

        logger.info("请求参数打印，流程名称：{},业务ID：{},创建人：{},创建公司：{}",flowName,businesskey,variables,userId);

        ExecutionEntity instance = (ExecutionEntity) runtimeservice.startProcessInstanceByKey(flowName,businesskey,variables);

        if(instance == null){
            return ReturnData.returnServiceError();
        }

        Map<String,String> map = Maps.newHashMap();
        //设置拥有者
        List<TaskEntity> list = instance.getTasks();
        for (TaskEntity task:list) {
            task.setFormKey(tenantId);
            task.setOwner(owner);
            taskService.saveTask(task);
            map.put("taskId",task.getId());
        }

        logger.info("返回参数打印：{}",ReturnData.returnSuccess(instance.getId()));

        map.put("instanceId",instance.getId());

        return ReturnData.returnSuccess(map);
    }

    @Override
    @Transactional
    public ReturnData promoteFlow(String approve,String taskId,String userId) {

        //参数验证
        if(StringUtils.isEmpty(approve)){
            return  ReturnData.returnParmeterError("状态返回有误！");
        }

        if(StringUtils.isEmpty(taskId)){
            return ReturnData.returnParmeterError("流程任务ID有误！");
        }

        if(StringUtils.isEmpty(userId)){
            return  ReturnData.returnParmeterError("用户ID有误！");
        }

        logger.info("请求参数打印，流程任务ID：{},用户ID：{}",taskId,userId);

        Task oldTask = taskService.createTaskQuery().taskId(taskId).singleResult();

        AssertUtils.notNull(oldTask,"流程任务ID有误！");

        Map<String,Object> variables=new HashMap<String,Object>();
        variables.put("deptleaderapprove", approve);

        //是否是并行任务的开始
        long count = taskService.createTaskQuery().processInstanceId(oldTask.getProcessInstanceId()).count();

        oldTask.setDescription("审核通过");
        taskService.saveTask(oldTask);

        taskService.claim(taskId, userId);
        taskService.complete(taskId, variables);



        List<Task> list = taskService.createTaskQuery().processInstanceId(oldTask.getProcessInstanceId()).list();

        if(CollectionUtils.isEmpty(list)){

            logger.info("该流程节点结束！");

            return ReturnData.returnSuccess(null);
        }

        JSONObject jsonObject = new JSONObject();

        //任务所有者
        for (Task newTask:list) {

            if(count >1){

                if(oldTask.getExecutionId().equals(newTask.getExecutionId())){
                    newTask.setFormKey(oldTask.getFormKey());
                    newTask.setOwner(oldTask.getOwner());
                    taskService.saveTask(newTask);
                    logger.info("审核通过流程执行完成");
                    jsonObject.put(newTask.getName(),newTask.getId());
                }

            }else{
                taskService.setOwner(newTask.getId(),oldTask.getOwner());
                logger.info("审核通过流程执行完成");
                jsonObject.put(newTask.getName(),newTask.getId());
            }


        }
        return ReturnData.returnSuccess(jsonObject);
/*
        //获取最新任务ID
        Task newTask = taskService.createTaskQuery().executionId(oldTask.getExecutionId()).singleResult();

        if(newTask == null){

            logger.info("该流程节点结束！");

            return ReturnData.returnSuccess(null);
        }

        //任务所有者
        taskService.setOwner(newTask.getId(),oldTask.getOwner());

        logger.info("审核通过流程执行完成");

         *//* //获取最新任务ID
        List<Task> list = taskService.createTaskQuery().processInstanceId(oldTask.getExecutionId()).list();

        if(CollectionUtils.isEmpty(list)){

            logger.info("该流程节点结束！");

            return ReturnData.returnSuccess(null);
        }

        JSONObject jsonObject = new JSONObject();

        //任务所有者
        for (Task newTask:list) {
            taskService.setOwner(newTask.getId(),oldTask.getOwner());

            logger.info("审核通过流程执行完成");
            jsonObject.put(newTask.getName(),newTask.getId());
        }*//*

        return ReturnData.returnSuccess(newTask.getId());*/
    }



    /**
     * 中止流程(特权人直接审批通过等)
     *
     * @param taskId
     */
    public ReturnData endProcess(String taskId)  {

        logger.info("终止流程参数打印，流程任务ID：{}",taskId);

        try {
            ActivityImpl endActivity = findActivitiImpl(taskId, "end");
            commitProcess(taskId, null, endActivity.getId());
        } catch (Exception e) {
            logger.error("终止流程有误！{}",e.getMessage());
            return ReturnData.returnServiceError();
        }

        logger.info("终止流程执行完成！");

        return ReturnData.returnSuccess(null);
    }

    /**
     * 驳回到某个节点
     * @param proInstId 流程实例id
     * @param destTaskKey 目标任务key
     * **/
    @Override
    public ReturnData backProcess(String proInstId, String destTaskKey)  {
        //参数验证
        if(StringUtils.isEmpty(proInstId)){
            return  ReturnData.returnParmeterError("流程实例ID有误！");
        }

        if(StringUtils.isEmpty(destTaskKey)){
            return  ReturnData.returnParmeterError("流程任务name有误！");
        }

        logger.info("驳回流程参数打印，流程实例ID：{},流程名称{}",proInstId,destTaskKey);

        Task newTask = new TaskEntity();
        //先获取当前任务
        Task taskEntity =  taskService.createTaskQuery().processInstanceId(proInstId).singleResult();

        HisTaskInfo hisTaskInfo = hisTaskInfoMapper.findByTaskDefKey(destTaskKey,proInstId);

        logger.info("历史该任务查询：{}",hisTaskInfo);

        //当前任务的key
        String taskDefKey = taskEntity.getTaskDefinitionKey();
        //获得当前流程的定义模型
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl)repositoryService)
                .getDeployedProcessDefinition(taskEntity.getProcessDefinitionId());
        String processDefinitionId = processDefinition.getId();
        GlobalConstants.lockWorkFlowMap.put(processDefinitionId,Boolean.TRUE);//静态变量 加锁
        //获得当前流程定义模型的所有任务节点
        List<ActivityImpl> activitilist = processDefinition.getActivities();
        //找到当前活动节点和驳回的目标节点
        ActivityImpl currActiviti = null;//当前活动节点
        ActivityImpl destActiviti = null;//驳回目标节点
        for(ActivityImpl activityImpl : activitilist){
            //确定当前活动activiti节点
            if(taskDefKey.equals(activityImpl.getId())){
                currActiviti = activityImpl;
            }else if(destTaskKey.equals(activityImpl.getId())){
                destActiviti = activityImpl;
            }
        }
        if(currActiviti!=null&&destActiviti!=null){
            logger.info("======当前任务节点:{} ", currActiviti.getId());
            logger.info("======目标任务节点:{} ", destActiviti.getId());
            //保存当前活动节点的流出方向参数
            List<PvmTransition> hisPvmTransitionList = Lists.newArrayList();
            for(PvmTransition pvmTransition:currActiviti.getOutgoingTransitions()){
                hisPvmTransitionList.add(pvmTransition);
            }
            //清空当前活动节点的所有流出项
            currActiviti.getOutgoingTransitions().clear();
            logger.info("=====当前任务节点流出方向:{}", currActiviti.getOutgoingTransitions().size());
            //为当前节点动态创建新的流出项
            TransitionImpl newTransitionImpl = currActiviti.createOutgoingTransition();
            //为当前活动节点新的流出目标指定流程目标
            newTransitionImpl.setDestination(destActiviti);
            //保存驳回意见
            taskEntity.setDescription("强制驳回");//设置驳回意见
            taskService.saveTask(taskEntity);
            //执行当前任务驳回到目标任务draft
            taskService.complete(taskEntity.getId());

            //清除目标节点的新流入项
            destActiviti.getIncomingTransitions().remove(newTransitionImpl);
            //清除原活动节点的临时流出项
            currActiviti.getOutgoingTransitions().clear();
            //还原原活动节点流出项
            currActiviti.getOutgoingTransitions().addAll(hisPvmTransitionList);

            //设置新的
            newTask =  taskService.createTaskQuery().processInstanceId(proInstId).singleResult();

            newTask.setFormKey(taskEntity.getFormKey());
            newTask.setOwner(taskEntity.getOwner());
            taskService.saveTask(newTask);

            //taskService.setOwner(newTask.getId(),taskEntity.getOwner()); //设置创建人

            if(hisTaskInfo !=null && StringUtils.isNotBlank(hisTaskInfo.getAssignee())){
                taskService.setAssignee(newTask.getId(),hisTaskInfo.getAssignee()); //设置受理人
                logger.info("当前受理人打印：{}",hisTaskInfo.getAssignee());
            }

        }
        GlobalConstants.lockWorkFlowMap.put(processDefinitionId,Boolean.FALSE);//静态变量 解锁

        logger.info("驳回流程执行完成！");

        return ReturnData.returnSuccess(newTask.getId());
    }

    @Override
    public HistoricProcessInstance findHistoryProcessInstance(String processInstanceId) {

        if(StringUtils.isNotBlank(processInstanceId)){
            HistoricProcessInstance hpi = historyService
                    .createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult();
            logger.info("=========查询历史流程ID:{}",hpi.getId());
            logger.info("=========历史流程开始时间:{}",CommonHelper.date2Str(hpi.getStartTime(), CommonHelper.DF_DATE_TIME));
            return hpi;
        }
        return null;

    }

    @Override
    public List<Task> findTasksByUser(String userid) {

        List<Task> list = taskService.createTaskQuery() // 创建任务查询对象
                .taskAssignee(userid) // 指定个人任务查询，指定办理人
                // .taskCandidateGroup("")//组任务的办理人查询
                // .processDefinitionId("")//使用流程定义ID查询
                // .processInstanceId("")//使用流程实例ID查询
                // .executionId(executionId)//使用执行对象ID查询
                .orderByTaskCreateTime().asc() // 使用优先级的升序排列
                .list();
        List<Task> groupTask = taskService.createTaskQuery().taskCandidateUser(userid).list();
        if(CollectionUtils.isNotEmpty(groupTask)){
            list.addAll(groupTask);
        }
        ActivitiUtils.printTaskList(list);
        return list;

    }

    @Override
    public List<Task> findTasksByUserAndProcess(String userid, String processId) {

        List<Task> list = taskService.createTaskQuery() // 创建任务查询对象
                .taskAssignee(userid) // 指定个人任务查询，指定办理人
                .processInstanceId(processId)//使用流程实例ID查询
                // .taskCandidateGroup("")//组任务的办理人查询
                // .processDefinitionId("")//使用流程定义ID查询
                // .executionId(executionId)//使用执行对象ID查询
                .orderByTaskPriority().asc() // 使用优先级的升序排列
                .list();
        ActivitiUtils.printTaskList(list);
        return list;

    }




    @Override
    public ReturnData transferAssignee(String taskId, String userId) {

        if(StringUtils.isEmpty(userId)){
           return ReturnData.returnParmeterError("用户ID有误！");
        }

        if(StringUtils.isEmpty(taskId)){
            return ReturnData.returnParmeterError("任务ID有误！");
        }

        logger.info("转让流程参数打印，任务ID：{},用户ID{}",taskId,userId);

        try {
            //更新任务记录的委办人
            Task curTask = taskService.createTaskQuery().taskId(taskId).singleResult();
            if(StringUtils.isNotBlank(curTask.getAssignee())){
                logger.info("=========任务{}已被{}认领过了",taskId,curTask.getAssignee());
                logger.info("=========任务{}被转让给{}",taskId,userId);

                curTask.setDescription("转让");
                taskService.saveTask(curTask);
                taskService.setAssignee(taskId, userId);
            }else{

                curTask.setDescription("转让");
                taskService.saveTask(curTask);

                taskService.claim(taskId, userId);
                logger.info("=========用户"+userId+"认领任务:任务ID:{}",taskId);
            }

            logger.info("转让流程已执行完成！");
            return ReturnData.returnSuccess(null);
        } catch (ActivitiTaskAlreadyClaimedException  e) {
            //taskService.setAssignee(taskId, userId);
            logger.error("装让流程执行错误{}",e.getMessage());
            return ReturnData.returnSystemError();
        }


    }

    @Override
    @Transactional
    public ReturnData assigneeTask(String taskId, String userId) {
        //推动任务
        if(StringUtils.isEmpty(userId)){
            return ReturnData.returnParmeterError("用户ID有误！");
        }

        if(StringUtils.isEmpty(taskId)){
            return  ReturnData.returnParmeterError("流程任务ID有误！");
        }

        logger.info("领取任务流程参数打印，任务ID：{},用户ID{}",taskId,userId);

        Map<String,Object> variables=new HashMap<String,Object>();

        variables.put("deptleaderapprove", "true");

        Task oldTask = taskService.createTaskQuery().taskId(taskId).singleResult();

        AssertUtils.notNull(oldTask,"流程任务ID有误！");

        oldTask.setDescription("领取任务");
        taskService.saveTask(oldTask);

       /* taskService.claim(taskId, userId);

        taskService.complete(taskId, variables);*/

        //获取最新任务ID
        //Task newTask = taskService.createTaskQuery().processInstanceId(oldTask.getExecutionId()).singleResult();

        //领取任务
        taskService.setAssignee(oldTask.getId(), userId);
       /* //任务所有者
        taskService.setOwner(newTask.getId(),oldTask.getOwner());*/
        logger.info("领取任务流程执行完成！");
        //认领任务
        return  ReturnData.returnSuccess(null);
    }

    @Override
    public ReturnData findByNameAndOwner(Page page, String name, String owners) {

        if(StringUtils.isEmpty(name)){
            return ReturnData.returnParmeterError("权限名称有误！");
        }

        if(StringUtils.isEmpty(owners)){
            return ReturnData.returnParmeterError("创建人参数有误！");
        }

        JSONArray jsonArray = JSONArray.parseArray(owners);


        logger.info("待审大厅流程参数打印，权限名称：{},创建人{}",name,owners);

        PageHelper.startPage(page.getFirstResult(),page.getMaxResults());

        Map<String,Object> map = Maps.newHashMap();

        map.put("taskDefKey",name);

        map.put("owners",jsonArray);

        if(CollectionUtils.isEmpty(jsonArray)){
            map.put("owners",null);
        }

        List<TaskModel> list =taskModelMapper.findByNameAndOwner(map);

        PageInfo pageInfo = new PageInfo<TaskModel>(list);

        logger.info("待审大厅查询成功返回:{}",JSONObject.toJSONString(ReturnData.returnSuccess(new PageContent(pageInfo))));

        return ReturnData.returnSuccess(new PageContent(pageInfo));
    }

    @Override
    public ReturnData findByAssigeneeAndName(Page page, String assignee, String name) {

        if(StringUtils.isEmpty(name)){
            return  ReturnData.returnParmeterError("权限名称有误！");
        }

        if(StringUtils.isEmpty(assignee)){
            return  ReturnData.returnParmeterError("受理人参数有误！");
        }

        logger.info("领取任务流程参数打印，权限名称：{},受理人{}",name,assignee);

        PageHelper.startPage(page.getFirstResult(),page.getMaxResults());

        List<TaskModel> list =taskModelMapper.findByAssigeneeAndName(assignee,name);

        PageInfo pageInfo = new PageInfo<TaskModel>(list);

        logger.info("领取任务查询成功返回:{}",JSONObject.toJSONString(new PageContent(pageInfo)));

        return ReturnData.returnSuccess(new PageContent(pageInfo));
    }


    /**
     * @param taskId
     *            当前任务ID
     * @param variables
     *            流程变量
     * @param activityId
     *            流程转向执行任务节点ID<br>
     *            此参数为空，默认为提交操作
     * @throws Exception
     */
    private void commitProcess(String taskId, Map<String, Object> variables,
                               String activityId) throws Exception {
        if (variables == null) {
            variables = new HashMap<String, Object>();
        }
        // 跳转节点为空，默认提交操作
        if (StringUtils.isEmpty(activityId)) {
            TaskEntity curTask = new TaskEntity();
            curTask.setId(taskId);
            curTask.setDescription("废弃");
            taskService.saveTask(curTask);
            taskService.complete(taskId, variables);
        } else {// 流程转向操作
            turnTransition(taskId, activityId, variables);
        }
    }

    /**
     * 清空指定活动节点流向
     *
     * @param activityImpl
     *            活动节点
     * @return 节点流向集合
     */
    private List<PvmTransition> clearTransition(ActivityImpl activityImpl) {
        // 存储当前节点所有流向临时变量
        List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();
        // 获取当前节点所有流向，存储到临时变量，然后清空
        List<PvmTransition> pvmTransitionList = activityImpl
                .getOutgoingTransitions();
        for (PvmTransition pvmTransition : pvmTransitionList) {
            oriPvmTransitionList.add(pvmTransition);
        }
        pvmTransitionList.clear();

        return oriPvmTransitionList;
    }

    /**
     * 还原指定活动节点流向
     *
     * @param activityImpl
     *            活动节点
     * @param oriPvmTransitionList
     *            原有节点流向集合
     */
    private void restoreTransition(ActivityImpl activityImpl,
                                   List<PvmTransition> oriPvmTransitionList) {
        // 清空现有流向
        List<PvmTransition> pvmTransitionList = activityImpl
                .getOutgoingTransitions();
        pvmTransitionList.clear();
        // 还原以前流向
        for (PvmTransition pvmTransition : oriPvmTransitionList) {
            pvmTransitionList.add(pvmTransition);
        }
    }

    /**
     * 流程转向操作
     *
     * @param taskId
     *            当前任务ID
     * @param activityId
     *            目标节点任务ID
     * @param variables
     *            流程变量
     * @throws Exception
     */
    private void turnTransition(String taskId, String activityId,
                                Map<String, Object> variables) throws Exception {
        // 当前节点
        ActivityImpl currActivity = findActivitiImpl(taskId, null);
        // 清空当前流向
        List<PvmTransition> oriPvmTransitionList = clearTransition(currActivity);

        // 创建新流向
        TransitionImpl newTransition = currActivity.createOutgoingTransition();
        // 目标节点
        ActivityImpl pointActivity = findActivitiImpl(taskId, activityId);
        // 设置新流向的目标节点
        newTransition.setDestination(pointActivity);

        // 执行转向任务
        taskService.complete(taskId, variables);
        // 删除目标节点新流入
        pointActivity.getIncomingTransitions().remove(newTransition);

        // 还原以前流向
        restoreTransition(currActivity, oriPvmTransitionList);
    }

    /**
     * ***************************************************************************************************************************************************<br>
     * ************************************************以上为流程转向操作核心逻辑******************************************************************************<br>
     * ***************************************************************************************************************************************************<br>
     */

    /**
     * ***************************************************************************************************************************************************<br>
     * ************************************************以下为查询流程驳回节点核心逻辑***************************************************************************<br>
     * ***************************************************************************************************************************************************<br>
     */

    /**
     * 迭代循环流程树结构，查询当前节点可驳回的任务节点
     *
     * @param taskId
     *            当前任务ID
     * @param currActivity
     *            当前活动节点
     * @param rtnList
     *            存储回退节点集合
     * @param tempList
     *            临时存储节点集合（存储一次迭代过程中的同级userTask节点）
     * @return 回退节点集合
     */
    private List<ActivityImpl> iteratorBackActivity(String taskId,
                                                    ActivityImpl currActivity, List<ActivityImpl> rtnList,
                                                    List<ActivityImpl> tempList) throws Exception {
        // 查询流程定义，生成流程树结构
        ProcessInstance processInstance = findProcessInstanceByTaskId(taskId);

        // 当前节点的流入来源
        List<PvmTransition> incomingTransitions = currActivity
                .getIncomingTransitions();
        // 条件分支节点集合，userTask节点遍历完毕，迭代遍历此集合，查询条件分支对应的userTask节点
        List<ActivityImpl> exclusiveGateways = new ArrayList<ActivityImpl>();
        // 并行节点集合，userTask节点遍历完毕，迭代遍历此集合，查询并行节点对应的userTask节点
        List<ActivityImpl> parallelGateways = new ArrayList<ActivityImpl>();
        // 遍历当前节点所有流入路径
        for (PvmTransition pvmTransition : incomingTransitions) {
            TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
            ActivityImpl activityImpl = transitionImpl.getSource();
            String type = (String) activityImpl.getProperty("type");
            /**
             * 并行节点配置要求：<br>
             * 必须成对出现，且要求分别配置节点ID为:XXX_start(开始)，XXX_end(结束)
             */
            if ("parallelGateway".equals(type)) {// 并行路线
                String gatewayId = activityImpl.getId();
                String gatewayType = gatewayId.substring(gatewayId
                        .lastIndexOf("_") + 1);
                if ("START".equals(gatewayType.toUpperCase())) {// 并行起点，停止递归
                    return rtnList;
                } else {// 并行终点，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
                    parallelGateways.add(activityImpl);
                }
            } else if ("startEvent".equals(type)) {// 开始节点，停止递归
                return rtnList;
            } else if ("userTask".equals(type)) {// 用户任务
                tempList.add(activityImpl);
            } else if ("exclusiveGateway".equals(type)) {// 分支路线，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
                currActivity = transitionImpl.getSource();
                exclusiveGateways.add(currActivity);
            }
        }

        /**
         * 迭代条件分支集合，查询对应的userTask节点
         */
        for (ActivityImpl activityImpl : exclusiveGateways) {
            iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
        }

        /**
         * 迭代并行集合，查询对应的userTask节点
         */
        for (ActivityImpl activityImpl : parallelGateways) {
            iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
        }

        /**
         * 根据同级userTask集合，过滤最近发生的节点
         */
        currActivity = filterNewestActivity(processInstance, tempList);
        if (currActivity != null) {
            // 查询当前节点的流向是否为并行终点，并获取并行起点ID
            String id = findParallelGatewayId(currActivity);
            if (StringUtils.isEmpty(id)) {// 并行起点ID为空，此节点流向不是并行终点，符合驳回条件，存储此节点
                rtnList.add(currActivity);
            } else {// 根据并行起点ID查询当前节点，然后迭代查询其对应的userTask任务节点
                currActivity = findActivitiImpl(taskId, id);
            }

            // 清空本次迭代临时集合
            tempList.clear();
            // 执行下次迭代
            iteratorBackActivity(taskId, currActivity, rtnList, tempList);
        }
        return rtnList;
    }

    /**
     * 反向排序list集合，便于驳回节点按顺序显示
     *
     * @param list
     * @return
     */
    private List<ActivityImpl> reverList(List<ActivityImpl> list) {
        List<ActivityImpl> rtnList = new ArrayList<ActivityImpl>();
        // 由于迭代出现重复数据，排除重复
        for (int i = list.size(); i > 0; i--) {
            if (!rtnList.contains(list.get(i - 1)))
                rtnList.add(list.get(i - 1));
        }
        return rtnList;
    }

    /**
     * 根据当前节点，查询输出流向是否为并行终点，如果为并行终点，则拼装对应的并行起点ID
     *
     * @param activityImpl
     *            当前节点
     * @return
     */
    private String findParallelGatewayId(ActivityImpl activityImpl) {
        List<PvmTransition> incomingTransitions = activityImpl
                .getOutgoingTransitions();
        for (PvmTransition pvmTransition : incomingTransitions) {
            TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
            activityImpl = transitionImpl.getDestination();
            String type = (String) activityImpl.getProperty("type");
            if ("parallelGateway".equals(type)) {// 并行路线
                String gatewayId = activityImpl.getId();
                String gatewayType = gatewayId.substring(gatewayId
                        .lastIndexOf("_") + 1);
                if ("END".equals(gatewayType.toUpperCase())) {
                    return gatewayId.substring(0, gatewayId.lastIndexOf("_"))
                            + "_start";
                }
            }
        }
        return null;
    }

    /**
     * 根据流入任务集合，查询最近一次的流入任务节点
     *
     * @param processInstance
     *            流程实例
     * @param tempList
     *            流入任务集合
     * @return
     */
    private ActivityImpl filterNewestActivity(ProcessInstance processInstance,
                                              List<ActivityImpl> tempList) {
        while (tempList.size() > 0) {
            ActivityImpl activity_1 = tempList.get(0);
            HistoricActivityInstance activityInstance_1 = findHistoricUserTask(
                    processInstance, activity_1.getId());
            if (activityInstance_1 == null) {
                tempList.remove(activity_1);
                continue;
            }

            if (tempList.size() > 1) {
                ActivityImpl activity_2 = tempList.get(1);
                HistoricActivityInstance activityInstance_2 = findHistoricUserTask(
                        processInstance, activity_2.getId());
                if (activityInstance_2 == null) {
                    tempList.remove(activity_2);
                    continue;
                }

                if (activityInstance_1.getEndTime().before(
                        activityInstance_2.getEndTime())) {
                    tempList.remove(activity_1);
                } else {
                    tempList.remove(activity_2);
                }
            } else {
                break;
            }
        }
        if (tempList.size() > 0) {
            return tempList.get(0);
        }
        return null;
    }

    /**
     * 查询指定任务节点的最新记录
     *
     * @param processInstance
     *            流程实例
     * @param activityId
     * @return
     */
    private HistoricActivityInstance findHistoricUserTask(
            ProcessInstance processInstance, String activityId) {
        HistoricActivityInstance rtnVal = null;
        // 查询当前流程实例审批结束的历史节点
        List<HistoricActivityInstance> historicActivityInstances = historyService
                .createHistoricActivityInstanceQuery().activityType("userTask")
                .processInstanceId(processInstance.getId()).activityId(
                        activityId).finished()
                .orderByHistoricActivityInstanceEndTime().desc().list();
        if (historicActivityInstances.size() > 0) {
            rtnVal = historicActivityInstances.get(0);
        }

        return rtnVal;
    }

    /**
     * *******************************************************************************************************<br>
     * ********************************以上为查询流程驳回节点核心逻辑***********************************************<br>
     * ********************************************************************************************************<br>
     */

    /**
     * ********************************************************************************<br>
     * **********************以下为activiti 核心service
     * set方法***************************<br>
     * *********************************************************************************<br>
     */

    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public void setRuntimeService(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * ********************************************************************************<br>
     * **********************以上为activiti 核心service
     * set方法***************************<br>
     * *********************************************************************************<br>
     */

    /**
     * ********************************************************************************<br>
     * **********************以下为根据 任务节点ID 获取流程各对象查询方法**********************<br>
     * *********************************************************************************<br>
     */

    /**
     * 根据任务ID获得任务实例
     *
     * @param taskId
     *            任务ID
     * @return
     * @throws Exception
     */
    private TaskEntity findTaskById(String taskId) throws Exception {
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(
                taskId).singleResult();
        if (task == null) {
            throw new Exception("任务实例未找到!");
        }
        return task;
    }

    /**
     * 根据流程实例ID和任务key值查询所有同级任务集合
     *
     * @param processInstanceId
     * @param key
     * @return
     */
    private List<Task> findTaskListByKey(String processInstanceId, String key) {
        return taskService.createTaskQuery().processInstanceId(
                processInstanceId).taskDefinitionKey(key).list();
    }

    /**
     * 根据任务ID获取对应的流程实例
     *
     * @param taskId
     *            任务ID
     * @return
     * @throws Exception
     */
    private ProcessInstance findProcessInstanceByTaskId(String taskId)
            throws Exception {
        // 找到流程实例
        ProcessInstance processInstance = runtimeService
                .createProcessInstanceQuery().processInstanceId(
                        findTaskById(taskId).getProcessInstanceId())
                .singleResult();
        if (processInstance == null) {
            throw new Exception("流程实例未找到!");
        }
        return processInstance;
    }

    /**
     * 根据任务ID获取流程定义
     *
     * @param taskId
     *            任务ID
     * @return
     * @throws Exception
     */
    private ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(
            String taskId) throws Exception {
        // 取得流程定义
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(findTaskById(taskId)
                        .getProcessDefinitionId());

        if (processDefinition == null) {
            throw new Exception("流程定义未找到!");
        }

        return processDefinition;
    }

    /**
     * 根据任务ID和节点ID获取活动节点 <br>
     *
     * @param taskId
     *            任务ID
     * @param activityId
     *            活动节点ID <br>
     *            如果为null或""，则默认查询当前活动节点 <br>
     *            如果为"end"，则查询结束节点 <br>
     *
     * @return
     * @throws Exception
     */
    private ActivityImpl findActivitiImpl(String taskId, String activityId)
            throws Exception {
        // 取得流程定义
        ProcessDefinitionEntity processDefinition = findProcessDefinitionEntityByTaskId(taskId);

        // 获取当前活动节点ID
        if (StringUtils.isEmpty(activityId)) {
            activityId = findTaskById(taskId).getTaskDefinitionKey();
        }

        // 根据流程定义，获取该流程实例的结束节点
        if (activityId.toUpperCase().equals("END")) {
            for (ActivityImpl activityImpl : processDefinition.getActivities()) {
                List<PvmTransition> pvmTransitionList = activityImpl
                        .getOutgoingTransitions();
                if (pvmTransitionList.isEmpty()) {
                    return activityImpl;
                }
            }
        }

        // 根据节点ID，获取对应的活动节点
        ActivityImpl activityImpl = ((ProcessDefinitionImpl) processDefinition)
                .findActivity(activityId);

        return activityImpl;
    }

    /**
     * ********************************************************************************<br>
     * **********************以上为根据 任务节点ID 获取流程各对象查询方法**********************<br>
     * *********************************************************************************<br>
     */
}
