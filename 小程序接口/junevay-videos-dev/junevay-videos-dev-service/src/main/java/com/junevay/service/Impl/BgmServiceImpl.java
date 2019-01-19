package com.junevay.service.Impl;

import com.junevay.mapper.BgmMapper;
import com.junevay.pojo.Bgm;
import com.junevay.service.BgmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Author: junevay
 * @Date: 2019/1/5 14:43
 */
@Service
public class BgmServiceImpl implements BgmService {

    @Autowired
    private BgmMapper bgmMapper;

    @Override
    public List<Bgm> queryAllBgm() {

        List<Bgm> bgms = bgmMapper.selectAll();
        return bgms;
    }


    public Bgm queryBgmById(String bgmId){

        Example example=new Example(Bgm.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",bgmId);
        Bgm bgm = bgmMapper.selectOneByExample(example);
        return bgm;


    }
}
