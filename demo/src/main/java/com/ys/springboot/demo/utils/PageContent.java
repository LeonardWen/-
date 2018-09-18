package com.ys.springboot.demo.utils;

import com.github.pagehelper.PageInfo;

import java.io.Serializable;
import java.util.List;

public class PageContent implements Serializable {

    private Integer pageNum;

    private  Integer pageSize;

    private List list;

    private Long size;

    public PageContent(Integer pageNum, Integer pageSize, List list, Long size) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.list = list;
        this.size = size;
    }

    public PageContent(PageInfo pageInfo){
        this.pageNum = pageInfo.getPageNum();
        this.pageSize = pageInfo.getPageSize();
        this.list = pageInfo.getList();
        this.size = pageInfo.getTotal();
    }

    public PageContent(){}



    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

}
