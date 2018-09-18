package com.ys.springboot.demo.mapper;


import com.ys.springboot.demo.model.AllFlow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AllFlowMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AllFlow record);

    int insertSelective(AllFlow record);

    AllFlow selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AllFlow record);

    int updateByPrimaryKey(AllFlow record);

    List<AllFlow> findAllFlowByType(@Param("type") Integer type);
}