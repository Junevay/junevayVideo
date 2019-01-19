package com.junevay.service;

import com.junevay.pojo.Bgm;

import java.util.List;

/**
 * @Author: junevay
 * @Date: 2019/1/5 14:42
 */
public interface BgmService {

    public List<Bgm> queryAllBgm();


    public Bgm queryBgmById(String bgmId);
}
