package com.junevay.controller;

import com.junevay.enums.VideoStatusEnum;
import com.junevay.pojo.Bgm;
import com.junevay.pojo.Comments;
import com.junevay.pojo.Videos;
import com.junevay.service.BgmService;
import com.junevay.service.CommentService;
import com.junevay.service.VideoService;
import com.junevay.utils.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author: junevay
 * @Date: 2019/1/5 15:18
 */
@RestController
@RequestMapping("/video")
public class VideoController extends BasicController {

    @Autowired
    private BgmService bgmService;
    @Autowired
    private VideoService videoService;
    @Autowired
    private CommentService commentService;


    @RequestMapping(value="/upload",headers="content-type=multipart/form-data")
    public JSONResult upload(
            String userId,
            String bgmId, double videoSeconds,
            int videoWidth, int videoHeight,
            String desc,
            MultipartFile file) throws IOException {

        if(StringUtils.isBlank(userId)){
            return JSONResult.errorMsg("用户id不能为空！");
        }
        //上传视频的相对路径
        String uploadVideoDB="/"+userId+"/video/";
        ///上传封面的相对路径
        String uploadCoverPathDB="/"+userId+"/video/";

        String finalUploadPath="";


        InputStream inputStream=null;
        FileOutputStream fileOutputStream=null;

        try {
            if(file!=null){

                String fileName = file.getOriginalFilename();

                if(StringUtils.isNoneBlank(fileName)){

                    uploadVideoDB+=fileName;
                    finalUploadPath=FILE_SPACE+uploadVideoDB;

                    String fileNamePrefix [] = fileName.split("\\.");
                    uploadCoverPathDB+=fileNamePrefix[0]+".jpg";

                    inputStream=file.getInputStream();
                    File outFIle = new File(finalUploadPath);
                    if(outFIle!=null&&!outFIle.getParentFile().isDirectory()){
                        outFIle.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFIle);
                    IOUtils.copy(inputStream,fileOutputStream);
                    FetchVideoCover fetchVideoCover = new FetchVideoCover(FFMPEG_EXE);
                    fetchVideoCover.getCover(finalUploadPath,FILE_SPACE+uploadCoverPathDB);
                }else{
                    return JSONResult.ok("上传出错！");
                }


            }else{
                return JSONResult.errorMsg("上传出错！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JSONResult.errorMsg("上传出错！");
        }finally {
            if(fileOutputStream!=null){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            if(inputStream!=null){
                inputStream.close();
            }
        }


        if(StringUtils.isNoneBlank(bgmId)){

            Bgm bgm = bgmService.queryBgmById(bgmId);
            uploadVideoDB="/"+userId+"/video/";
            String bgmInputPath=FILE_SPACE+bgm.getPath();
            String videoInputPath=finalUploadPath;
            String videoOutPutName= UUID.randomUUID().toString()+".mp4";
            String clearAudioPath=FILE_SPACE+uploadVideoDB+UUID.randomUUID().toString()+"clear.mp4";
            uploadVideoDB+=videoOutPutName;
            finalUploadPath=FILE_SPACE+uploadVideoDB;


            System.out.println(finalUploadPath);
            MergeVideoMp3 tool = new MergeVideoMp3(FFMPEG_EXE);
            ClearMp4Audio clearMp4Audio=new ClearMp4Audio(FFMPEG_EXE);
            try {
                clearMp4Audio.convertor(videoInputPath,clearAudioPath);
                new File(videoInputPath).delete();
                tool.convertor(clearAudioPath,bgmInputPath,videoSeconds,finalUploadPath);
               new File(clearAudioPath).delete();
            } catch (Exception e) {
                e.printStackTrace();
                return JSONResult.errorMsg("视频上传失败！");
            }


        }

        Videos videos = new Videos();
        videos.setAudioId(bgmId);
        videos.setUserId(userId);
        videos.setCreateTime(new Date());
        videos.setVideoHeight(videoHeight);
        videos.setVideoWidth(videoWidth);
        videos.setVideoPath(uploadVideoDB);
        videos.setVideoSeconds((float) videoSeconds);
        videos.setVideoDesc(desc);
        videos.setStatus(VideoStatusEnum.SUCCESS.value);
        videos.setCoverPath(uploadCoverPathDB);
        String videoId = videoService.save(videos);
        return JSONResult.ok(videoId);
    }


    @PostMapping(value="/uploadCover",headers="content-type=multipart/form-data")
    public JSONResult uploadCover(String userId,String videoId,MultipartFile file) throws IOException {

        if(StringUtils.isBlank(userId)||StringUtils.isBlank(videoId)){
            return JSONResult.errorMsg("用户名ID和视频ID不能为空！");
        }

        if(file!=null){
            String filename = file.getOriginalFilename();

            InputStream inputStream=null;
            FileOutputStream fileOutputStream=null;
            if(StringUtils.isNoneBlank(filename)) try {

                String uploadPathDB = "/" + userId + "/video/" + filename;
                String finalUploadPath = FILE_SPACE + uploadPathDB;

                File outFile = new File(finalUploadPath);

                if (outFile.getParentFile() != null && !outFile.getParentFile().isDirectory()) {
                    outFile.getParentFile().mkdirs();
                }


                inputStream = file.getInputStream();
                fileOutputStream = new FileOutputStream(outFile);
                IOUtils.copy(inputStream, fileOutputStream);

                videoService.updateVideo(videoId, uploadPathDB);
                return JSONResult.ok();

            } catch (IOException e) {
                e.printStackTrace();
                return JSONResult.errorMsg("封面上传失败！");
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            }
            else{
                return JSONResult.errorMsg("封面上传失败！");
            }
        }else{
            return JSONResult.errorMsg("封面上传失败！") ;
        }



    }



    @RequestMapping("/showAll")
    public JSONResult showAll(@RequestBody Videos videos,Integer isSaveRecord,Integer page,Integer pageSize){

        if(page==null){
            page=1;
        }

        if(pageSize==null){
            pageSize=PAGE_SIZE;
        }

        PagedResult pagedResult = videoService.queryAllVideos(videos,isSaveRecord,page, pageSize);

        return JSONResult.ok(pagedResult);

    }

    @RequestMapping("/getVideoComments")
    public JSONResult getVideoComments(String videoId,Integer page,Integer pageSize){
        if(page==null){
            page=1;
        }

        if(pageSize==null){
            pageSize=PAGE_SIZE;
        }


        PagedResult videoComments = videoService.getVideoComments(videoId, page, pageSize);
        return JSONResult.ok(videoComments);
    }


    @RequestMapping("/saveComment")
    public JSONResult saveComment(@RequestBody Comments comments){

        try {
            commentService.saveComment(comments);
            return JSONResult.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return JSONResult.errorMsg("评论失败！");
        }


    }


    @RequestMapping("/hot")
    public JSONResult hot(){
        List<String> hotWorld = videoService.getHotWorld();

        return JSONResult.ok(hotWorld);
    }

    @RequestMapping("/userLike")
    public JSONResult userLike(String userId,String videoId,String videoCreaterId){

        try {
            videoService.userLike(userId,videoId,videoCreaterId);
        } catch (Exception e) {
            e.printStackTrace();
            return JSONResult.errorMsg("操作失败！");
        }

        return JSONResult.ok();
    }


    @RequestMapping("/userUnLike")
    public JSONResult userUnLike(String userId,String videoId,String videoCreaterId){

        try{
        videoService.userUnLike(userId,videoId,videoCreaterId);
    } catch (Exception e) {
        e.printStackTrace();
        return JSONResult.errorMsg("操作失败！");
    }
        return JSONResult.ok();
    }


    @RequestMapping("/showMyLike")
    public JSONResult showMyLike(Integer page,Integer pageSize,String userId){

        if(page==null){
            page=1;
        }

        if(pageSize==null){
            pageSize=PAGE_SIZE;
        }

        PagedResult pagedResult = videoService.quaryMyLike(page, pageSize, userId);

        return JSONResult.ok(pagedResult);

    }

    @RequestMapping("/showMyFollow")
    public JSONResult showFollow(Integer page,Integer pageSize,String userId){

        if(page==null){
            page=1;
        }

        if(pageSize==null){
            pageSize=PAGE_SIZE;
        }

        PagedResult pagedResult = videoService.queryMyFolloe(page, pageSize, userId);

        return JSONResult.ok(pagedResult);

    }
}
