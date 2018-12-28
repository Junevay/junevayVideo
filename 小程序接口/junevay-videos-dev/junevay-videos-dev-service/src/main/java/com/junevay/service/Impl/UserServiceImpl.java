package com.junevay.service.Impl;

import com.junevay.mapper.UsersMapper;
import com.junevay.pojo.Users;
import com.junevay.service.UserService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @Author: junevay
 * @Date: 2018/12/27 19:23
 */
@Service
public class UserServiceImpl implements UserService{


    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    @Override
    public void saveUser(Users users) {
            users.setId(sid.nextShort());
            usersMapper.insert(users);
    }

    @Override
    public boolean findUserNameIsExist(String username) {

        Users users = new Users();
        users.setUsername(username);
        Users  result= usersMapper.selectOne(users);

        return result!=null ? true : false;
    }
}
