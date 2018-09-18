package com.ys.springboot.demo.service;

import com.ys.springboot.demo.model.AllFlow;
import com.ys.springboot.demo.utils.ReturnData;

import java.util.List;

/**
 * @Auther: Trying
 * @Date: 2018/9/7 17:41
 * @Description:
 */
public interface AllFlowService {

    ReturnData findAllFlowByType(Integer type);

}
