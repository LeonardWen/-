package com.ys.springboot.demo.mapper;

import com.ys.springboot.demo.po.User;

import java.util.List;


public interface UserMapper {
	List<User> getusers();
	User getUserByid(int id);
	void deleteuser(int uid);
	void deleteuserrole(int uid);
	void adduser(User user);
	void updateuser(User user);
	int getUidByusername(String username);
}
