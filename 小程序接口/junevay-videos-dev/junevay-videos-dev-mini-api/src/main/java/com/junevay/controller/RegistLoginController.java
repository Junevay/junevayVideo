package com.junevay.controller;

import com.junevay.pojo.Users;
import com.junevay.pojo.vo.UsersVO;
import com.junevay.service.UserService;
import com.junevay.utils.JSONResult;
import com.junevay.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @Author: junevay
 * @Date: 2018/12/27 19:07
 */
@RestController
@RequestMapping("/user")
public class RegistLoginController extends BasicController {

    @Autowired
    private UserService userService;

    @RequestMapping("/regist")
    public JSONResult regist(@RequestBody  Users users) throws Exception {

        if(StringUtils.isBlank(users.getUsername())||StringUtils.isBlank(users.getPassword())){

             return JSONResult.errorMsg("用户名或者密码不能为空！");
        }

        if(userService.findUserNameIsExist(users.getUsername())){
            return JSONResult.errorMsg("用户名已经存在！");
        }else{
        users.setPassword(MD5Utils.getMD5Str(users.getPassword()));
        users.setNickname(users.getUsername());
        users.setFansCounts(0);
        users.setFollowCounts(0);
        users.setReceiveLikeCounts(0);

        try{
            userService.saveUser(users);
            UsersVO usersVO = setUserRedisSessionToken(users);

            return JSONResult.ok(usersVO);
        }catch(Exception e){
            e.printStackTrace();
        return JSONResult.errorMsg("用户注册失败！");
        }

    }

    }
    @RequestMapping("/login")
    public JSONResult login(@RequestBody Users users) {

        if (StringUtils.isBlank(users.getUsername()) || StringUtils.isBlank(users.getPassword())) {
            return JSONResult.errorMsg("用户名或者密码不能为空！");
        }

        try {
            Users isUsers = userService.queryUserForLogin(users.getUsername(), MD5Utils.getMD5Str(users.getPassword()));
            if (isUsers != null) {

                UsersVO usersVO = setUserRedisSessionToken(isUsers);
                return JSONResult.ok(usersVO);

            } else {
                return JSONResult.errorMsg("用户名或者密码错误！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JSONResult.errorMsg("登录失败！");
        }
    }

    @RequestMapping("/logout")
    public JSONResult logout(String userId){
        redis.del(USER_REDIS_SESSION+":"+userId);
        return JSONResult.ok("用户注销成功！");
    }



        private UsersVO setUserRedisSessionToken(Users userModel){


            String uniqueToken= UUID.randomUUID().toString();
            redis.set(USER_REDIS_SESSION+":"+userModel.getId(),uniqueToken,1000*60*30);
            UsersVO usersVO = new UsersVO();
            BeanUtils.copyProperties(userModel,usersVO);
            usersVO.setUserToken(uniqueToken);
            return usersVO;
        }


}
