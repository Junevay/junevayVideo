package com.junevay.controller;

import com.junevay.pojo.Users;
import com.junevay.service.UserService;
import com.junevay.utils.JSONResult;
import com.junevay.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: junevay
 * @Date: 2018/12/27 19:07
 */
@RestController
@RequestMapping("/user")
public class RegistLoginController {

    @Autowired
    private UserService userService;

    @RequestMapping("/regist")
    public JSONResult regist( Users users) throws Exception {

        if(StringUtils.isBlank(users.getUsername())||StringUtils.isBlank(users.getPassword())){

             return JSONResult.errorMsg("用户名不能为空！");
        }

        if(userService.findUserNameIsExist(users.getUsername())){
            return JSONResult.errorMsg("用户名已经存在！");
        }else{
        users.setPassword(MD5Utils.getMD5Str(users.getUsername()));
        users.setNickname(users.getUsername());
        users.setFansCounts(0);
        users.setFollowCounts(0);
        users.setReceiveLikeCounts(0);

        try{
            userService.saveUser(users);
            return JSONResult.ok();
        }catch(Exception e){
            e.printStackTrace();
        return JSONResult.errorMsg("用户注册失败！");
        }

    }





    }
}
