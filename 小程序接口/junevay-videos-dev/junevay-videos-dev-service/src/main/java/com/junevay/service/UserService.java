package com.junevay.service;

import com.junevay.pojo.Users;

/**
 * @Author: junevay
 * @Date: 2018/12/27 19:22
 */
 public interface UserService {

    /**
     * 保存用户名
     * @param users
     */
     public void saveUser(Users users);

    /**
     * 判断用户名是否存在
     * @param username
     * @return
     */
     public boolean findUserNameIsExist(String username);

 }
