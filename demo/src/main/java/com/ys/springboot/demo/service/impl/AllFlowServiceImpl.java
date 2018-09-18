package com.ys.springboot.demo.service.impl;

import com.ys.springboot.demo.mapper.AllFlowMapper;
import com.ys.springboot.demo.model.AllFlow;
import com.ys.springboot.demo.service.AllFlowService;
import com.ys.springboot.demo.utils.ReturnData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: Trying
 * @Date: 2018/9/7 17:42
 * @Description:
 */
@Service
public class AllFlowServiceImpl implements AllFlowService {

    @Autowired
    private AllFlowMapper allFlowMapper;



    @Override
    public ReturnData findAllFlowByType(Integer type) {
        List<AllFlow> list = allFlowMapper.findAllFlowByType(type);
        return ReturnData.returnSuccess(list);
    }


}
