package com.ys.springboot.demo.mapper;


import com.ys.springboot.demo.po.LeaveApply;

public interface LeaveApplyMapper {
	void save(LeaveApply apply);
	LeaveApply get(int id);
	void update(LeaveApply app);
}
