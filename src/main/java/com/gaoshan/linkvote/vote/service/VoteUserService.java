package com.gaoshan.linkvote.vote.service;

import com.gaoshan.linkvote.vote.entity.VoteUser;
public interface VoteUserService{


    int deleteByPrimaryKey(Long id);

    int insert(VoteUser record);

    int insertSelective(VoteUser record);

    VoteUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(VoteUser record);

    int updateByPrimaryKey(VoteUser record);

}
