package com.junevay.controller;

import com.junevay.pojo.Users;
import com.junevay.pojo.UsersReport;
import com.junevay.pojo.vo.PublisherVideo;
import com.junevay.pojo.vo.UsersVO;
import com.junevay.service.UserService;
import com.junevay.utils.JSONResult;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @Author: junevay
 * @Date: 2019/1/2 16:34
 */
@RestController
@RequestMapping("/user")
public class UserController extends BasicController{

    @Autowired
    private UserService userService;

    /**
     * 上传用户头像
     * @param files
     * @param userId
     * @return
     * @throws Exception
     */
    @RequestMapping("/uploadFace")
    public JSONResult uploadUserFace(@RequestParam("file") MultipartFile files[], String userId) throws Exception{

        if (StringUtils.isBlank(userId)){
            JSONResult.errorMsg("用户ID不能为空！");
        }


        //数据库存储路径
        String uploadPathDB="/"+userId+"/face";

        FileOutputStream fileOutputStream=null;
        InputStream inputStream=null;
        try {

            if (files != null && files.length > 0) {
                String fileName = files[0].getOriginalFilename();
                if (StringUtils.isNoneBlank(fileName)) {
                    //最终上传路径
                    String finalUploadPath =  FILE_SPACE+uploadPathDB + "/" + fileName;
                    uploadPathDB += ("/" + fileName);
                    File outFile = new File(finalUploadPath);
                    inputStream = files[0].getInputStream();

                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        outFile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = files[0].getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }

            }else{
                return JSONResult.errorMsg("请选择上传的图片！");
            }
        }catch (Exception e){
            e.printStackTrace();
            return JSONResult.errorMsg("头像上传失败！");
        }finally {
            if(fileOutputStream!=null){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            if(inputStream!=null){
                inputStream.close();
            }

        }



        try {

            Users users = userService.queryUsersById(userId);
            String oldImage = users.getFaceImage();
            oldImage=FILE_SPACE+oldImage;
            new File(oldImage).delete();
            users.setFaceImage(uploadPathDB);

            userService.updateUserInfo(users);


        } catch (Exception e) {
            e.printStackTrace();
           return JSONResult.errorMsg("上传图片失败！");
        }
        return JSONResult.ok(uploadPathDB);
    }

    /**
     *
     * 获取用户信息
     * @param userId
     * @return
     */
    @RequestMapping("/getUserInfo")
    public JSONResult getUserInfo(String userId,String fanId){
        Users users = userService.queryUsersById(userId);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(users,usersVO);
        boolean ifFollow = userService.queryIfFollow(userId, fanId);
        usersVO.setFollow(ifFollow);
        return JSONResult.ok(usersVO);

    }


    /**
     * 查询视频与登录者的关系和视频发布者信息
     * @param loginUserId
     * @param videoId
     * @param publishId
     * @return
     */
    @RequestMapping("/queryPublisher")
    public JSONResult queryPublisher(String loginUserId,String videoId,String publishUserId){
        if(StringUtils.isBlank(publishUserId)){
            return JSONResult.errorMsg("");
        }

        //查询发布者的信息
        Users userInfo = userService.queryUsersById(publishUserId);
        UsersVO publisher = new UsersVO();
        BeanUtils.copyProperties(userInfo,publisher);

        //查询登录跟视频发布者的关系
        boolean userLikeVideo = userService.isUserLikeVideo(loginUserId, videoId);
        PublisherVideo publisherVideo=new PublisherVideo();
        publisherVideo.setPublisher(publisher);
        publisherVideo.setUserLikeVideo(userLikeVideo);

        return JSONResult.ok(publisherVideo);
    }


    /**
     * 举报视频
     * @return
     */
    @PostMapping("/reportUser")
    public JSONResult reportUser(@RequestBody UsersReport usersReport){

        try {
            userService.userReport(usersReport);
        } catch (Exception e) {
            e.printStackTrace();
            return JSONResult.errorMsg("举报失败！");
        }
        return JSONResult.ok();
    }


    /**
     * 成为你的粉丝
     * @param userId
     * @param fanId
     * @return
     */
    @RequestMapping("/beyourfans")
    public JSONResult beYourFans(String userId,String fanId){

        userService.saveUserFanRelation(userId,fanId);
        return  JSONResult.ok();
    }

    /**
     * 不成为你的粉丝
     * @param userId
     * @param fanId
     * @return
     */
    public JSONResult dontBeYourFans(String userId,String fanId){
        userService.deleteUserFanRelation(userId,fanId);
        return JSONResult.ok();
    }

}
