package com.mystudy.mybatis.dao;

import com.mystudy.mybatis.domain.User;

import java.util.List;

public interface IUserDao {
    /**
     * 查询所有用户
     * @return
     */
    List<User> findAll();
}
