package com.junevay.service.Impl;

import com.junevay.mapper.CommentsMapper;
import com.junevay.pojo.Comments;
import com.junevay.service.CommentService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author: junevay
 * @Date: 2019/1/13 14:48
 */
@Service
public class CommentServiceImpl implements CommentService{

    @Autowired
    private CommentsMapper commentsMapper;
    @Autowired
    private Sid sid;

    @Override
    public void saveComment(Comments comments) {

        String id = sid.nextShort();
        comments.setId(id);
        comments.setCreateTime(new Date());
        commentsMapper.insert(comments);
    }
}
