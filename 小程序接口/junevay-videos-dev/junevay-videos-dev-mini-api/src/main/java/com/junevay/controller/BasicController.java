package com.junevay.controller;

import com.junevay.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: junevay
 * @Date: 2018/12/31 13:19
 */
@RestController
public class BasicController {

    @Autowired
    public RedisOperator redis;

    public final String USER_REDIS_SESSION="USER_REDIS_SESSION";
    //PPMPEG软件安装路径
    public final String FFMPEG_EXE ="F:\\ffmpeg\\bin\\ffmpeg.exe";
    //文件根路径
    public final String FILE_SPACE ="D:/junevay";

    public final Integer PAGE_SIZE=5;
}