package com.junevay.mapper;

import com.junevay.pojo.Comments;
import com.junevay.pojo.vo.CommentsVO;
import com.junevay.utils.MyMapper;

import java.util.List;

public interface CommentsMapper extends MyMapper<Comments> {

    public List<CommentsVO> queryComments(String videoId);
}