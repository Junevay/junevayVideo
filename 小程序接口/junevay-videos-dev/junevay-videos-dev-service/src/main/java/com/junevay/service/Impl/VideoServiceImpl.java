package com.junevay.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.junevay.mapper.*;
import com.junevay.pojo.Comments;
import com.junevay.pojo.SearchRecords;
import com.junevay.pojo.UsersLikeVideos;
import com.junevay.pojo.Videos;
import com.junevay.pojo.vo.CommentsVO;
import com.junevay.pojo.vo.VideosVO;
import com.junevay.service.VideoService;
import com.junevay.utils.PagedResult;
import com.junevay.utils.TimeAgoUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: junevay
 * @Date: 2019/1/6 15:51
 */
@Service
public class VideoServiceImpl  implements VideoService{

    @Autowired
    private VideosMapper videosMapper;
    @Autowired
    private Sid sid;
    @Autowired
    private CommentsMapper commentsMapper;
    @Autowired
    private SearchRecordsMapper searchRecordsMapper;
    @Autowired
    private VideosMapperCustom videosMapperCustom;
    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;
    @Autowired
    private UsersMapper usersMapper;


    @Override
    public String save(Videos videos) {

        String s = sid.nextShort();
        videos.setId(s);
         videosMapper.insertSelective(videos);
        return s;
    }

    @Override
    public void updateVideo(String videoId, String coverPath) {

        Videos videos = new Videos();
        videos.setId(videoId);
        videos.setCoverPath(coverPath);

        videosMapper.updateByPrimaryKeySelective(videos);

    }

    @Override
    public PagedResult queryAllVideos(Videos videos,Integer isSaveRecord,Integer page,Integer pageSize) {


        String desc =videos.getVideoDesc();
        String userId=videos.getUserId();
        if(isSaveRecord==1){
            SearchRecords searchRecords = new SearchRecords();
            String searchId = sid.nextShort();
            searchRecords.setId(searchId);
            searchRecords.setContent(desc);
        }

        PageHelper.startPage(page,pageSize);

        List<VideosVO> videosList= videosMapperCustom.queryAllVideos(desc,userId);

        PageInfo<VideosVO> videosPageInfo = new PageInfo<>(videosList);
        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setRows(videosList);
        pagedResult.setTotal(videosPageInfo.getPages());
        pagedResult.setRecords(videosPageInfo.getTotal());
        return pagedResult;
    }

    @Override
    public PagedResult getVideoComments(String videoId, Integer page, Integer pageSize) {

        PageHelper.startPage(page,pageSize);


        List<CommentsVO> comments = commentsMapper.queryComments(videoId);

        comments.forEach(c->{
            String format = TimeAgoUtils.format(c.getCreateTime());
            c.setTimeAgoStr(format);
        });

        PageInfo<CommentsVO> commentsPageInfo = new PageInfo<>(comments);

        PagedResult pagedResult=new PagedResult();

        pagedResult.setRecords(commentsPageInfo.getTotal());
        pagedResult.setPage(page);
        pagedResult.setTotal(commentsPageInfo.getPages());
        pagedResult.setRows(commentsPageInfo.getList());

        return pagedResult;
    }

    @Override
    public List<String> getHotWorld() {
        List<String> hotwords = searchRecordsMapper.getHotwords();
        return hotwords;
    }

    @Override
    public void userLike(String userId, String videoId, String videoCreaterId) {



        UsersLikeVideos usersLikeVideos = new UsersLikeVideos();
        String s = sid.nextShort();
        usersLikeVideos.setId(s);
        usersLikeVideos.setUserId(userId);
        usersLikeVideos.setVideoId(videoId);
        usersLikeVideosMapper.insert(usersLikeVideos);

        videosMapperCustom.addVideoLikeCount(videoId);

        usersMapper.addReceiveLikeCount(videoCreaterId);
    }

    @Override
    public void userUnLike(String userId, String videoId, String videoCreaterId) {

        Example example=new Example(UsersLikeVideos.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",videoCreaterId);
        criteria.andEqualTo("videoId",videoId);

        usersLikeVideosMapper.deleteByExample(example);


        usersMapper.reduceReceiveLikeCount(userId);


        videosMapperCustom.reduceVideoLikeCount(videoId);


    }

    @Override
    public PagedResult quaryMyLike(Integer page, Integer pageSize,String userId) {

        PageHelper.startPage(page,pageSize);

        List<VideosVO> videosVOS = videosMapperCustom.queryMyLikeVideos(userId);

        PageInfo<VideosVO> videosVOPageInfo = new PageInfo<>(videosVOS);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(videosVOPageInfo.getPages());
        pagedResult.setRecords(videosVOPageInfo.getTotal());
        pagedResult.setPage(page);
        pagedResult.setRows(videosVOS);
        return pagedResult;
    }

    @Override
    public PagedResult queryMyFolloe(Integer page, Integer pageSize, String userId) {
        PageHelper.startPage(page,pageSize);

        List<VideosVO> videosVOS = videosMapperCustom.queryMyFollowVideos(userId);

        PageInfo<VideosVO> videosVOPageInfo = new PageInfo<>(videosVOS);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(videosVOPageInfo.getPages());
        pagedResult.setRecords(videosVOPageInfo.getTotal());
        pagedResult.setPage(page);
        pagedResult.setRows(videosVOS);
        return pagedResult;
    }


}
