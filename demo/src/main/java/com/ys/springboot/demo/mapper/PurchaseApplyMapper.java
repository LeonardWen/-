package com.ys.springboot.demo.mapper;


import com.ys.springboot.demo.po.PurchaseApply;

public interface PurchaseApplyMapper {
	void save(PurchaseApply apply);
	PurchaseApply get(int id);
	void update(PurchaseApply apply);
}
