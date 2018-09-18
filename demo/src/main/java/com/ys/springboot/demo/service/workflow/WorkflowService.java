package com.ys.springboot.demo.service.workflow;

import com.github.pagehelper.PageInfo;
import com.ys.springboot.demo.po.TaskModel;
import com.ys.springboot.demo.utils.ReturnData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.Page;
import org.activiti.engine.task.Task;

import java.util.List;
import java.util.Map;

public interface WorkflowService {

    /**
     * 开始一个事务
     * @param flowName 事务名称
     * @param businesskey  业务逻辑ID
     * @param variables  不知道干什么的
     * @param userId 验证用户ID
     * @return
     */
    ReturnData startWorkflow(String flowName, String businesskey, Map<String, Object> variables, String userId,String owner,String tenantId);


    /**
     * 任务流程推动方法
     * @param approve
     * @param taskId
     * @param userId
     * @return
     */
    ReturnData promoteFlow(String approve,String taskId,String userId);

    /**
     * 结束流程
     * @param taskId
     */
    ReturnData endProcess(String taskId);


    /**
     * 驳回到某个节点
     * @param proInstId 流程实例id
     * @param destTaskKey 目标任务key
     * **/
    ReturnData backProcess(String proInstId, String destTaskKey);



    /**
     * 查询历史流程实例
     * @param processInstanceId 流程实例id
     * */
    public HistoricProcessInstance findHistoryProcessInstance(String processInstanceId);

    /**
     * 获取用户个人任务列表
     * @param userid 用户id
     * */
    public List<Task> findTasksByUser(String userid);



    /**
     * 获取用户个人的某个流程下的任务列表
     * @param userid 用户id
     * @param processid 流程 id
     * @see
     * 任务ID、名称、办理人、创建时间可以从act_ru_task表中查到
     * 任务ID在数据库表act_ru_task中对应“ID_”列
     * */
    public List<Task> findTasksByUserAndProcess(String userid,String processId);


    /**
     * 转办流程
     * @param taskId
     * @param userId
     */
    public ReturnData  transferAssignee(String taskId, String userId);


    /**
     * 认领任务
     * @param taskId 任务id
     * @param userId 用户id
     * @return boolean 认领成功或失败
     * */
    public ReturnData assigneeTask(String taskId, String userId);


    /**
     * 待审大厅--领取任务
     * @param page
     * @param name
     * @param owners
     * @return
     */
    ReturnData findByNameAndOwner(Page page,String name,String owners);


    /**
     * 个人待审任务大厅
     * @param page
     * @param assignee
     * @param name
     * @return
     */
    ReturnData findByAssigeneeAndName(Page page, String assignee, String name);


}
