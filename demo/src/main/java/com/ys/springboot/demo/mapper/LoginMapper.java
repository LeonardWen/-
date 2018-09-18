package com.ys.springboot.demo.mapper;


import com.ys.springboot.demo.po.User;

public interface LoginMapper {
	User getpwdbyname(String name);
}
