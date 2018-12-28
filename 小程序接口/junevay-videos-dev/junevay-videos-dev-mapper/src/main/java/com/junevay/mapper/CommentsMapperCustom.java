package com.junevay.mapper;

import java.util.List;

import com.junevay.pojo.Comments;
import com.junevay.pojo.vo.CommentsVO;
import com.junevay.utils.MyMapper;

public interface CommentsMapperCustom extends MyMapper<Comments> {
	
	public List<CommentsVO> queryComments(String videoId);
}