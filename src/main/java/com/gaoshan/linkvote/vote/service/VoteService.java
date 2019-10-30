package com.gaoshan.linkvote.vote.service;

import com.gaoshan.linkvote.base.R;
import com.gaoshan.linkvote.vote.entity.Vote;
import com.gaoshan.linkvote.vote.entity.VoteQuery;
import com.github.pagehelper.PageInfo;

public interface VoteService {

    int deleteByPrimaryKey(Long id);

    R insert(Vote record, String optionJson, String address);

    int insertSelective(Vote record);

    Vote selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Vote record);

    int updateByPrimaryKey(Vote record);

    PageInfo queryByPage(VoteQuery query);

    R getVoteDetail(Long voteId, String name);

    R getVoteOptionDetail(Long optionId);

    R updateVoteHash(String address, Long voteId, String hash);

    R doVoteApp(Long voteId, String options, String address);

    R updateAppVoteHash(String address, Long voteId, String hash);
}
