package com.ys.springboot.demo.mapper;

import com.ys.springboot.demo.po.HisTaskInfo;
import org.apache.ibatis.annotations.Param;

public interface HisTaskInfoMapper {

    HisTaskInfo findByTaskDefKey(@Param("taskDefKey") String taskDefKey,@Param("procInstId") String procInstId);

}
