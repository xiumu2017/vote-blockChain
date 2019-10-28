package com.gaoshan.linkvote.vote.service;

import com.gaoshan.linkvote.base.R;
import com.gaoshan.linkvote.vote.entity.Vote;
import com.gaoshan.linkvote.vote.entity.VoteQuery;
import com.github.pagehelper.PageInfo;

import java.security.Principal;

public interface VoteService {

    int deleteByPrimaryKey(Long id);

    R insert(Vote record, String optionJson);

    int insertSelective(Vote record);

    Vote selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Vote record);

    int updateByPrimaryKey(Vote record);

    PageInfo queryByPage(VoteQuery query);

    R getVoteDetail(Long voteId, String name);

    R doVote(Long voteId, String options, Principal principal);

    R getVoteOptionDetail(Long optionId);
}
