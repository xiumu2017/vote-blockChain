package com.gaoshan.linkvote.user.service.impl;

import com.gaoshan.linkvote.base.R;
import com.gaoshan.linkvote.base.Rx;
import com.gaoshan.linkvote.user.bean.SysUser;
import com.gaoshan.linkvote.user.entity.BlackList;
import com.gaoshan.linkvote.user.entity.BlackUser;
import com.gaoshan.linkvote.user.entity.WhiteList;
import com.gaoshan.linkvote.user.entity.WhiteUser;
import com.gaoshan.linkvote.user.mapper.*;
import com.gaoshan.linkvote.user.service.BlackWhiteUserService;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.List;

@Service
@Slf4j
public class BlackWhiteUserServiceImpl implements BlackWhiteUserService {

    @Resource
    private BlackListMapper blackListMapper;
    @Resource
    private WhiteListMapper whiteListMapper;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private WhiteUserMapper whiteUserMapper;
    @Resource
    private BlackUserMapper blackUserMapper;

    @Override
    public R addBlack(BlackList blackList, Principal principal) {
        SysUser user = sysUserMapper.selectByName(principal.getName());
        blackList.setUpdateUser(user.getId());
        blackList.setCreateUser(user.getId());
        blackListMapper.insert(blackList);
        return Rx.success();
    }

    @Override
    public R updateBlack(BlackList blackList, Principal principal) {
        SysUser user = sysUserMapper.selectByName(principal.getName());
        blackList.setUpdateUser(user.getId());
        blackListMapper.updateByPrimaryKey(blackList);
        return Rx.success();
    }

    @Override
    public R delBlack(Long id, Principal principal) {
        if (blackListMapper.deleteByPrimaryKey(id) == 1) {
            return Rx.success();
        } else {
            if (blackListMapper.selectByPrimaryKey(id) != null) {
                return Rx.fail();
            }
        }
        return Rx.success();
    }

    @Override
    public R queryBlackPage(BlackList blackList, Integer pageNum, Integer pageSize, Principal principal) {
        SysUser user = sysUserMapper.selectByName(principal.getName());
        blackList.setCreateUser(user.getId());
        PageHelper.startPage(pageNum, pageSize);
        List<BlackList> lists = blackListMapper.selectByAll(blackList);
        return Rx.success(lists);
    }

    @Override
    public R delFromBlackList(Long id) {
        blackUserMapper.deleteByPrimaryKey(id);
        return Rx.success();
    }

    @Override
    public R queryBlackUserList(Long blackId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<BlackUser> blackUserList = blackUserMapper.selectAllByBlackId(blackId);
        return Rx.success(blackUserList);
    }

    @Override
    public R addUserToBlackList(Long blackId, String userIds) {
        BlackUser blackUser;
        String[] userIdArr = userIds.split(",");
        for (String userId : userIdArr) {
            blackUser = new BlackUser();
            blackUser.setBlackId(blackId);
            blackUser.setUserId(Long.valueOf(userId));
            blackUserMapper.insert(blackUser);
        }
        return Rx.success();
    }

    @Override
    public R addWhite(WhiteList whiteList, Principal principal) {
        SysUser user = sysUserMapper.selectByName(principal.getName());
        whiteList.setUpdateUser(user.getId());
        whiteList.setCreateUser(user.getId());
        whiteListMapper.insert(whiteList);
        return Rx.success();
    }

    @Override
    public R updateWhite(WhiteList whiteList, Principal principal) {
        SysUser user = sysUserMapper.selectByName(principal.getName());
        whiteList.setUpdateUser(user.getId());
        whiteListMapper.updateByPrimaryKey(whiteList);
        return Rx.success();
    }

    @Override
    public R delWhite(Long id, Principal principal) {
        if (whiteListMapper.deleteByPrimaryKey(id) == 1) {
            return Rx.success();
        } else {
            if (whiteListMapper.selectByPrimaryKey(id) != null) {
                return Rx.fail();
            }
        }
        return Rx.success();
    }

    @Override
    public R queryWhitePage(WhiteList whiteList, Integer pageNum, Integer pageSize, Principal principal) {
        SysUser user = sysUserMapper.selectByName(principal.getName());
        whiteList.setCreateUser(user.getId());
        PageHelper.startPage(pageNum, pageSize);
        List<WhiteList> lists = whiteListMapper.selectByAll(whiteList);
        return Rx.success(lists);
    }

    @Override
    public R delFromWhiteList(Long id) {
        whiteUserMapper.deleteByPrimaryKey(id);
        return Rx.success();
    }

    @Override
    public R queryWhiteUserList(Long whiteId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<WhiteUser> whiteUserList = whiteUserMapper.selectAllByWhiteId(whiteId);
        return Rx.success(whiteUserList);
    }

    @Override
    public R addUserToWhiteList(Long whiteId, String userIds) {
        WhiteUser whiteUser;
        String[] userIdArr = userIds.split(",");
        for (String userId : userIdArr) {
            whiteUser = new WhiteUser();
            whiteUser.setWhiteId(whiteId);
            whiteUser.setUserId(Long.valueOf(userId));
            whiteUserMapper.insert(whiteUser);
        }
        return Rx.success();
    }
}
