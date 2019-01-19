package com.junevay.service;

import com.junevay.pojo.Users;
import com.junevay.pojo.UsersReport;

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


    /**
     * 登陆
      * @param username
     * @param password
     * @return
     */
     public Users queryUserForLogin(String username,String password);

    /**
     * 修改用户信息
     * @param users
     * @return
     */
     public void updateUserInfo(Users users );

    /**
     * 根据用户id查询用户信息
     * @param userId
     * @return
     */
     public Users queryUsersById(String userId);

    /**
     * 判断是否关注
     * @param userId
     * @param fanId
     * @return
     */

    public boolean queryIfFollow(String userId,String fanId);

    /**
     * 查询视频和登录者的关系
     * @param userId
     * @param publisherId
     * @return
     */
     public boolean isUserLikeVideo(String userId,String videoId);

    /**
     * 举报用户视频
     * @param usersReport
     */
    public void userReport(UsersReport usersReport);

    /**
     * 保存用户和粉丝的关系
     * @param userId
     * @param fanId
     */
    public void saveUserFanRelation(String userId,String fanId);

    /**
     * 删除用户和粉丝的关系
     * @param userId
     * @param fanId
     */
    public void deleteUserFanRelation(String userId,String fanId);
 }
