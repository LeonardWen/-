package com.ys.springboot.demo.Controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.ys.springboot.demo.service.AllFlowService;
import com.ys.springboot.demo.service.workflow.WorkflowService;
import com.ys.springboot.demo.utils.ReturnData;
import org.activiti.bpmn.model.Activity;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping(value = "/workflow")
@RestController
public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private AllFlowService allFlowService;

    /**
     * 开始工作流
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/startWorkflow")
    public ReturnData startWorkflowInterface(HttpServletRequest request, HttpServletResponse response){
        String flowName = request.getParameter("flowName");
        String businesskey = request.getParameter("businesskey");
        String userId = request.getParameter("userId");
        String owner = request.getParameter("owner");
        String tenantId = request.getParameter("tenantId"); //创建人用户

        Map<String,Object>  variables = new HashMap<>();
        variables.put("userId",userId);
        return workflowService.startWorkflow(flowName,businesskey,variables,userId,owner,tenantId);
    }


    /**
     * 推动流程-推动任务
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/promoteFlow")
    public ReturnData promoteFlow(HttpServletRequest request,HttpServletResponse response){

        String taskId = request.getParameter("taskId");

        String userId  = request.getParameter("userId");

        return workflowService.promoteFlow("true",taskId,userId);
    }

    /**
     * 中途结束流程
     * @param request
     * @param response
     * @return
     */
    @PostMapping(value = "/endFlow")
    public ReturnData endFlow(HttpServletRequest request,HttpServletResponse response){

        String taskId = request.getParameter("taskId");
        return workflowService.endProcess(taskId);
    }


    /**
     * 驳回流程
     * @param request
     * @return
     */
    @PostMapping(value = "/backProcess")
    public ReturnData backProcess(HttpServletRequest request){

        String proInstId = request.getParameter("proInstId");

        String activityId = request.getParameter("activityId");

        return workflowService.backProcess(proInstId,activityId);
    }

    /**
     * 转让方法-领取任务方法
     * @param request
     * @return
     */
    @PostMapping(value = "/transferAssignee")
    public ReturnData transferAssignee(HttpServletRequest request){
        String taskId = request.getParameter("taskId");

        String userId = request.getParameter("userId");

        return workflowService.transferAssignee(taskId,userId);
    }

    /**
     * 推进任务并领取任务
     * @param request
     * @return
     */
    @PostMapping(value = "/assigneeTask")
    public ReturnData assigneeTask(HttpServletRequest request){
        String taskId = request.getParameter("taskId");

        String userId = request.getParameter("userId");

        return workflowService.assigneeTask(taskId,userId);
    }


    /**
     * 待审大厅任务列表
     * @param request
     * @return
     */
    @PostMapping(value = "/findByNameAndOwner")
    public ReturnData findByNameAndOwner(HttpServletRequest request){

        String name = request.getParameter("name");

        String owners = request.getParameter("owners");

        int pageNum = Integer.parseInt(request.getParameter("pageNum"));

        int pageSize = Integer.parseInt(request.getParameter("pageSize"));

        Page page = new Page(pageNum,pageSize);

        return workflowService.findByNameAndOwner(page,name,owners);
    }

    /**
     * 个人待审任务查询
     * @param request
     * @return
     */
    @PostMapping(value = "/findByAssigeneeAndName")
    public ReturnData findByAssigeneeAndName(HttpServletRequest request){

        String name = request.getParameter("name");

        String assignee = request.getParameter("assignee");

        int pageNum = Integer.parseInt(request.getParameter("pageNum"));

        int pageSize = Integer.parseInt(request.getParameter("pageSize"));

        Page page = new Page(pageNum,pageSize);

        return workflowService.findByAssigeneeAndName(page,assignee,name);
    }


    @GetMapping(value = "/test")
    public String test(HttpServletRequest request){
        List<String> list = Lists.newArrayList();
        list.add("123");
        list.add("456");
        return JSONObject.toJSONString(list);
    }


    @PostMapping(value = "/findFlowNameByType")
    public ReturnData findFlowNameByType(HttpServletRequest request){
        int type = Integer.parseInt(request.getParameter("type"));
        return allFlowService.findAllFlowByType(type);
    }

}
