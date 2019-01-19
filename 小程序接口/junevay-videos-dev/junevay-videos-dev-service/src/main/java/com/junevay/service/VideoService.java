package com.junevay.service;

import com.junevay.pojo.Videos;
import com.junevay.utils.JSONResult;
import com.junevay.utils.PagedResult;

import java.util.List;

/**
 * @Author: junevay
 * @Date: 2019/1/6 15:51
 */
public interface VideoService {

    public String save(Videos videos);

    public void updateVideo(String videoId,String coverPath);

    public PagedResult queryAllVideos(Videos videos,Integer isRrcordSave,Integer page,Integer pageSize);

    public PagedResult getVideoComments(String videoId,Integer page,Integer pageSize);

    public List<String> getHotWorld();

    public void userLike(String userId,String videoId,String videoCreaterId);

    public void userUnLike(String userId,String videoId,String videoCreaterId);

    public PagedResult quaryMyLike(Integer page,Integer pageSize,String userId);

    public PagedResult queryMyFolloe(Integer page,Integer pageSize,String userId);

}
