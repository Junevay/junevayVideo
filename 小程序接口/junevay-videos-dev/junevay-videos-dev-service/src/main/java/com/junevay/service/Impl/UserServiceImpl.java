package com.junevay.service.Impl;

import com.junevay.mapper.UsersFansMapper;
import com.junevay.mapper.UsersLikeVideosMapper;
import com.junevay.mapper.UsersMapper;
import com.junevay.mapper.UsersReportMapper;
import com.junevay.pojo.Users;
import com.junevay.pojo.UsersFans;
import com.junevay.pojo.UsersLikeVideos;
import com.junevay.pojo.UsersReport;
import com.junevay.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.Date;
import java.util.List;


/**
 * @Author: junevay
 * @Date: 2018/12/27 19:23
 */
@Service
public class UserServiceImpl implements UserService{


    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;
    @Autowired
    private UsersReportMapper usersReportMapper;

    @Autowired
    private Sid sid;
    @Autowired
    private UsersFansMapper usersFansMapper;

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

    @Override
    public Users queryUserForLogin(String username, String password) {
        Example example = new Example(Users.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username",username);
        criteria.andEqualTo("password",password);
        Users users = usersMapper.selectOneByExample(example);
        return users;
    }

    @Override
    public void updateUserInfo(Users users) {
        Example example = new Example(Users.class);
       Criteria criteria = example.createCriteria();
       criteria.andEqualTo("id",users.getId());
        usersMapper.updateByExampleSelective(users,example);

    }

    @Override
    public Users queryUsersById(String userId) {
        Example example = new Example(Users.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",userId);
        Users users = usersMapper.selectOneByExample(example);
        return users;
    }

    @Override
    public boolean queryIfFollow(String userId, String fanId) {

        Example example = new Example(UsersFans.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("fanId",fanId);

        List<UsersFans> usersFans = usersFansMapper.selectByExample(example);

        if(usersFans!=null&&usersFans.size()>0){
            return true;
        }

        return false;
    }

    @Override
    public boolean isUserLikeVideo(String userId, String videoId) {
        if(StringUtils.isBlank(userId)||StringUtils.isBlank(videoId)){
            return false;
        }
        Example example = new Example(UsersLikeVideos.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("videoId",videoId);
        criteria.andEqualTo("userId",userId);

        List<UsersLikeVideos> usersLikeVideos = usersLikeVideosMapper.selectByExample(example);

        if(usersLikeVideos!=null&&usersLikeVideos.size()>0){
            return true;
        }


        return false;
    }

    @Override
    public void userReport(UsersReport usersReport) {

        String s = sid.nextShort();
        usersReport.setId(s);
        usersReport.setCreateDate(new Date());

        usersReportMapper.insert(usersReport);

    }

    @Override
    public void saveUserFanRelation(String userId, String fanId) {

        String s = sid.nextShort();
        UsersFans usersFans = new UsersFans();
        usersFans.setUserId(userId);
        usersFans.setId(s);
        usersFans.setFanId(fanId);

        usersFansMapper.insert(usersFans);
        usersMapper.addFansCount(userId);
        usersMapper.addFollersCount(userId);
    }

    @Override
    public void deleteUserFanRelation(String userId, String fanId) {

        Example example = new Example(UsersFans.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("fanId",fanId);
        usersFansMapper.deleteByExample(example);

        usersMapper.reduceFollersCount(userId);
        usersMapper.reduceFansCount(userId);
    }



}
