package com.gaoshan.linkvote.vote.service.impl;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import com.gaoshan.linkvote.vote.entity.VoteUser;
import com.gaoshan.linkvote.vote.mapper.VoteUserMapper;
import com.gaoshan.linkvote.vote.service.VoteUserService;

@Service
public class VoteUserServiceImpl implements VoteUserService {

    @Resource
    private VoteUserMapper voteUserMapper;

    @Override
    public int deleteByPrimaryKey(Long id) {
        return voteUserMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(VoteUser record) {
        return voteUserMapper.insert(record);
    }

    @Override
    public VoteUser selectByPrimaryKey(Long id) {
        return voteUserMapper.selectByPrimaryKey(id);
    }

}
