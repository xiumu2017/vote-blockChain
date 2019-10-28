package com.gaoshan.linkvote.vote.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.gaoshan.linkvote.vote.entity.VoteOption;
import com.gaoshan.linkvote.vote.mapper.VoteOptionMapper;
import com.gaoshan.linkvote.vote.service.VoteOptionService;
@Service
public class VoteOptionServiceImpl implements VoteOptionService{

    @Resource
    private VoteOptionMapper voteOptionMapper;

    @Override
    public int deleteByPrimaryKey(Long id) {
        return voteOptionMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(VoteOption record) {
        return voteOptionMapper.insert(record);
    }

    @Override
    public int insertSelective(VoteOption record) {
        return voteOptionMapper.insertSelective(record);
    }

    @Override
    public VoteOption selectByPrimaryKey(Long id) {
        return voteOptionMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(VoteOption record) {
        return voteOptionMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(VoteOption record) {
        return voteOptionMapper.updateByPrimaryKey(record);
    }

}
