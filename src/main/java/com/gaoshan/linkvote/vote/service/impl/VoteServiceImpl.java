package com.gaoshan.linkvote.vote.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.gaoshan.linkvote.base.R;
import com.gaoshan.linkvote.base.Rx;
import com.gaoshan.linkvote.user.bean.SysUser;
import com.gaoshan.linkvote.user.mapper.SysUserMapper;
import com.gaoshan.linkvote.vote.entity.*;
import com.gaoshan.linkvote.vote.mapper.VoteMapper;
import com.gaoshan.linkvote.vote.mapper.VoteOptionMapper;
import com.gaoshan.linkvote.vote.mapper.VoteUserMapper;
import com.gaoshan.linkvote.vote.service.VoteService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
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
    public R insert(Vote record, String optionJson, String address) {
        SysUser user = userMapper.selectByAddress(address);
        if (user == null) {
            user = initUser(address);
        }
        List<VoteOption> optionList = JSONArray.parseArray(optionJson, VoteOption.class);
        if (optionList.size() < 2) {
            return Rx.error("投票选项数量小于2");
        }
        record.setCreateUser(user.getId());
        record.setUpdateUser(user.getId());
        record.setStatus(Vote_Status.CREATE.getCode());
        voteMapper.insert(record);
        if (record.getId() != null) {
            for (VoteOption option : optionList) {
                option.setVoteId(record.getId());
                option.setCreateUser(record.getCreateUser());
                option.setUpdateUser(record.getCreateUser());
                optionMapper.insert(option);
            }
        }
        record.setOptionList(optionMapper.selectByVoteId(record.getId()));
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
        // 查询投票信息
        Vote vote = voteMapper.selectByPrimaryKey(voteId);
        // 查询选项信息和统计数量
        List<VoteOption> optionList = optionMapper.selectByVoteId(voteId);
        for (VoteOption option : optionList) {
            option.setCount(voteUserMapper.countByVoteIdAndOptId(voteId, option.getId()));
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("vote", vote);
        resultMap.put("optionList", optionList);
        // 查询 已投的用户信息
        List<SysUser> userList = userMapper.selectUsersByVoteId(voteId);
        resultMap.put("userList", userList);
        return Rx.success(resultMap);
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
                map.put("userName", sysUser.getUsername());
                map.put("pic", sysUser.getPicUrl());
                resultList.add(map);
            }
        }
        return Rx.success(resultList);
    }

    @Override
    public R updateVoteHash(String address, Long voteId, String hash) {
        Vote vote = voteMapper.selectByPrimaryKey(voteId);
        if (vote == null) {
            return Rx.error("投票不存在或已删除");
        }
        vote.setHash(hash);
        SysUser sysUser = userMapper.selectByAddress(address);
        if (sysUser == null) {
            return Rx.error("地址信息未入库");
        }
        vote.setUpdateUser(sysUser.getId());
        if (voteMapper.updateByPrimaryKeySelective(vote) == 1) {
            return Rx.success();
        }
        return Rx.fail();
    }

    @Override
    public R doVoteApp(Long voteId, String options, String address) {
        // 判断投票状态
        Vote vote = voteMapper.selectByPrimaryKey(voteId);
        if (vote == null || !vote.getStatus().equals(Vote_Status.ING.getCode())) {
            return Rx.error("投票不存在或已截止");
        }
        SysUser user = userMapper.selectByAddress(address);
        if (user == null) {
            user = initUser(address);
        }
        // 判断选项合理性
        String[] optionArr = options.split(",");
        if (optionArr.length > vote.getLimitNum()) {
            return Rx.error("选项数量超出限制");
        }
        // 判断是否已经投票
        List<VoteUser> voteUserList = voteUserMapper.selectByVoteIdAndUserId(voteId, user.getId());
        if (!voteUserList.isEmpty()) {
            return Rx.error("不能重复投票");
        }
        // 选项入库
        List<Long> optionIdList = new ArrayList<>();
        for (String optionId : optionArr) {
            if (optionMapper.selectByPrimaryKey(Long.valueOf(optionId)) == null) {
                return Rx.error("投票选项id不存在");
            } else {
                optionIdList.add(Long.valueOf(optionId));
            }
        }
        voteUserMapper.insertBatch(user.getId(), address, voteId, optionIdList);
        // 处理返回的json
        return Rx.success(voteId + File.separator + options);
    }

    @Override
    public R updateAppVoteHash(String address, Long voteId, String hash) {
        SysUser user = userMapper.selectByAddress(address);
        if (user == null) return Rx.error("address地址信息未入库");

        Vote vote = voteMapper.selectByPrimaryKey(voteId);
        if (vote == null) return Rx.error("投票不存在或已删除");

        voteUserMapper.updateVoteHash(user.getId(), voteId, hash);
        return Rx.success();
    }

    @Override
    public R appQueryVotePage(String address, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Vote> list = voteMapper.selectByApp();
        SysUser sysUser = userMapper.selectByAddress(address);
        if (!list.isEmpty()) {
            for (Vote vote : list) {
                List<VoteOption> optionList = optionMapper.selectByVoteId(vote.getId());
                vote.setOptionList(optionList);
                SysUser user = userMapper.selectByPrimaryKey(vote.getCreateUser());
                vote.setCreateUserName(user.getUsername());
                vote.setCreateUserPic(user.getPicUrl());
                if (sysUser != null) {
                    List<VoteUser> voteUserList = voteUserMapper.selectByVoteIdAndUserId(vote.getId(), sysUser.getId());
                    vote.setVoteUserList(voteUserList);
                }
            }
        }
        return Rx.success(new PageInfo<>(list));
    }

    @Override
    public R getAppVoteDetail(Long voteId, String address) {
        // 查询投票信息
        Vote vote = voteMapper.selectByPrimaryKey(voteId);
        if (vote == null) {
            return Rx.error("投票不存在或已删除");
        }
        // 查询选项信息和统计数量
        List<VoteOption> optionList = optionMapper.selectByVoteId(voteId);
        for (VoteOption option : optionList) {
            option.setCount(voteUserMapper.countByVoteIdAndOptId(voteId, option.getId()));
        }
        Map<String, Object> resultMap = new HashMap<>();
        // 查询当前用户信息以及用户投票信息
        SysUser user = userMapper.selectByAddress(address);
        if (user == null) {
            user = initUser(address);
        }
        List<VoteUser> voteUserList = voteUserMapper.selectByVoteIdAndUserId(voteId, user.getId());
        resultMap.put("vote", vote);
        resultMap.put("optionList", optionList);
        resultMap.put("voteUserList", voteUserList);
        // 查询 已投的用户信息
        List<SysUser> userList = userMapper.selectUsersByVoteId(voteId);
        resultMap.put("userList", userList);
        return Rx.success(resultMap);
    }

    @Override
    public R delete(Long id, String address) {
        Vote vote = voteMapper.selectByPrimaryKey(id);
        if (vote == null) {
            return Rx.error("投票不存在或已删除");
        }
        SysUser user = userMapper.selectByAddress(address);
        if (user == null || !user.getId().equals(vote.getCreateUser())) {
            return Rx.error("没有权限");
        }
        if (voteMapper.deleteByPrimaryKey(id) == 1) {
            return Rx.success();
        }
        return Rx.fail();
    }

    private SysUser initUser(String address) {
        SysUser user = new SysUser();
        user.setAddress(address);
        user.setUsername(address.substring(Math.min(address.length(), 10)));
        user.setStatus("1");
        userMapper.insert(user);
        return user;
    }

}
