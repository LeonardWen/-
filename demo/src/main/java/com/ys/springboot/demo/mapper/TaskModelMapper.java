package com.ys.springboot.demo.mapper;

import com.ys.springboot.demo.po.TaskModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TaskModelMapper {


    public List<TaskModel>  findByNameAndOwner(Map<String, Object> maps);

    /**
     * 根据领取任务查询，分页 个人待审大厅
     * @param assignee
     * @return
     */
    public List<TaskModel>  findByAssigeneeAndName(@Param("assignee") String assignee,@Param("taskDefKey")String name);

}
