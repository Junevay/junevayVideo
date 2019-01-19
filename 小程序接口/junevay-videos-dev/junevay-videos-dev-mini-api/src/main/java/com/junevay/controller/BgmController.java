package com.junevay.controller;

import com.junevay.service.BgmService;
import com.junevay.utils.JSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: junevay
 * @Date: 2019/1/5 13:39
 */
@RestController
@RequestMapping("/bgm")
public class BgmController extends BasicController {


    @Autowired
    private BgmService bgmService;

    @RequestMapping("/list")
    public JSONResult list(){


        return JSONResult.ok(bgmService.queryAllBgm());
    }


}
