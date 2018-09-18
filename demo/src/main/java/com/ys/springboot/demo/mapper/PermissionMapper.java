package com.ys.springboot.demo.mapper;

import com.ys.springboot.demo.po.Permission;

import java.util.List;


public interface PermissionMapper {
	List<Permission> getPermissions();
	Permission getPermissionByname(String permissionname);
	void addpermission(String permissionname);
	void deletepermission(int pid);
	void deleteRole_permission(int permissionid);
}
