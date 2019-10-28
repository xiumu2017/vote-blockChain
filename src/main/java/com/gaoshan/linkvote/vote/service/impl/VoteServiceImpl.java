package com.gaoshan.linkvote.vote.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.gaoshan.linkvote.base.R;
import com.gaoshan.linkvote.base.Rx;
import com.gaoshan.linkvote.user.bean.SysUser;
import com.gaoshan.linkvote.user.mapper.SysUserMapper;
import com.gaoshan.linkvote.vote.entity.*;
import com.gaoshan.linkvote.vote.mapper.VoteOptionMapper;
import com.gaoshan.linkvote.vote.mapper.VoteUserMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import com.gaoshan.linkvote.vote.mapper.VoteMapper;
import com.gaoshan.linkvote.vote.service.VoteService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VoteServiceImpl implements VoteService {

    @Resource
    private VoteOptionMapper optionMapper;
    @Resource
    private VoteMapper voteMapper;
    @Resource
    private SysUserMapper userMapper;
    @Resource
    private VoteUserMapper voteUserMapper;

    @Override
    public int deleteByPrimaryKey(Long id) {
        return voteMapper.deleteByPrimaryKey(id);
    }

    @Override
    public R insert(Vote record, String optionJson) {
        List<VoteOption> optionList = JSONArray.parseArray(optionJson, VoteOption.class);
        if (optionList.size() < 2) {
            return Rx.error("投票选项数量小于2");
        }
        voteMapper.insert(record);
        if (record.getId() != null) {
            for (VoteOption option : optionList) {
                option.setVoteId(record.getId());
                option.setCreateUser(record.getCreateUser());
                option.setUpdateUser(record.getCreateUser());
                optionMapper.insert(option);
            }
        }
        return Rx.success(record);
    }

    @Override
    public int insertSelective(Vote record) {
        return voteMapper.insertSelective(record);
    }

    @Override
    public Vote selectByPrimaryKey(Long id) {
        return voteMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(Vote record) {
        return voteMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(Vote record) {
        return voteMapper.updateByPrimaryKey(record);
    }

    @Override
    public PageInfo queryByPage(VoteQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<Vote> list = voteMapper.selectByAll(query);
        return new PageInfo<>(list);
    }

    @Override
    public R getVoteDetail(Long voteId, String name) {
        Map<String, Object> resultMap = new HashMap<>();
        Vote vote = voteMapper.selectByPrimaryKey(voteId);
        List<VoteOption> optionList = optionMapper.selectByVoteId(voteId);
        for (VoteOption option : optionList) {
            option.setCount(voteUserMapper.countByVoteIdAndOptId(voteId, option.getId()));
        }
        SysUser user = userMapper.selectByName(name);
        List<VoteUser> voteUserList = voteUserMapper.selectByVoteIdAndUserId(voteId, user.getId());
        resultMap.put("vote", vote);
        resultMap.put("optionList", optionList);
        resultMap.put("voteUserList", voteUserList);
        return Rx.success(resultMap);
    }

    @Override
    public R doVote(Long voteId, String options, Principal principal) {
        SysUser user = userMapper.selectByName(principal.getName());
        // 判断是否可以投票
        Vote vote = voteMapper.selectByPrimaryKey(voteId);
        if (!vote.getStatus().equals(Vote_Status.ING.getCode())) {
            return Rx.error("当前投票未开始或已结束");
        }
        List<VoteUser> voteUserList = voteUserMapper.selectByVoteIdAndUserId(voteId, user.getId());
        if (!voteUserList.isEmpty()) {
            return Rx.error("不能重复投票");
        }
        String[] optionArr = options.split(",");
        List<Long> optionIdList = new ArrayList<>();
        for (String optionId : optionArr) {
            if (optionMapper.selectByPrimaryKey(Long.valueOf(optionId)) == null) {
                return Rx.error("投票选项id不存在");
            } else {
                optionIdList.add(Long.valueOf(optionId));
            }
        }
        voteUserMapper.insertBatch(user.getId(), voteId, optionIdList);
        return Rx.success();
    }

    @Override
    public R getVoteOptionDetail(Long optionId) {
        VoteOption option = optionMapper.selectByPrimaryKey(optionId);
        if (option == null) {
            return Rx.error("选项不存在");
        }
        List<VoteUser> voteUserList = voteUserMapper.selectAllByOptId(optionId);
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> map;
        if (!voteUserList.isEmpty()) {
            for (VoteUser voteUser : voteUserList) {
                SysUser sysUser = userMapper.selectByPrimaryKey(voteUser.getUserId());
                map = new HashMap<>();
                map.put("voteTime", voteUser.getVoteTime());
                map.put("userName", sysUser.getName());
                map.put("pic", sysUser.getPic());
                resultList.add(map);
            }
        }
        return Rx.success(resultList);
    }

}
