package com.ys.springboot.demo.pagemodel;


import com.ys.springboot.demo.po.LeaveApply;
import com.ys.springboot.demo.po.PurchaseApply;

public class HistoryProcess {
	String processDefinitionId;
	String businessKey;
	LeaveApply leaveapply;
	PurchaseApply purchaseapply;
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}
	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}
	public String getBusinessKey() {
		return businessKey;
	}
	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}
	public LeaveApply getLeaveapply() {
		return leaveapply;
	}
	public void setLeaveapply(LeaveApply leaveapply) {
		this.leaveapply = leaveapply;
	}
	public PurchaseApply getPurchaseapply() {
		return purchaseapply;
	}
	public void setPurchaseapply(PurchaseApply purchaseapply) {
		this.purchaseapply = purchaseapply;
	}
	
	
}