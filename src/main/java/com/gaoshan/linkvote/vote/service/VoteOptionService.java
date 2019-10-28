package com.gaoshan.linkvote.vote.service;

import com.gaoshan.linkvote.vote.entity.VoteOption;
public interface VoteOptionService{


    int deleteByPrimaryKey(Long id);

    int insert(VoteOption record);

    int insertSelective(VoteOption record);

    VoteOption selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(VoteOption record);

    int updateByPrimaryKey(VoteOption record);

}
