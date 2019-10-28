package com.gaoshan.linkvote.user.service;

import com.gaoshan.linkvote.base.R;
import com.gaoshan.linkvote.user.entity.BlackList;
import com.gaoshan.linkvote.user.entity.WhiteList;

import java.security.Principal;

public interface BlackWhiteUserService {

    R addBlack(BlackList blackList, Principal principal);

    R updateBlack(BlackList blackList, Principal principal);

    R delBlack(Long id, Principal principal);

    R queryBlackPage(BlackList blackList, Integer pageNum, Integer pageSize, Principal principal);

    R delFromBlackList(Long id);

    R queryBlackUserList(Long blackId, Integer pageNum, Integer pageSize);

    R addUserToBlackList(Long blackId, String userIds);

    R addWhite(WhiteList whiteList, Principal principal);

    R updateWhite(WhiteList whiteList, Principal principal);

    R delWhite(Long id, Principal principal);

    R queryWhitePage(WhiteList whiteList, Integer pageNum, Integer pageSize, Principal principal);

    R delFromWhiteList(Long id);

    R queryWhiteUserList(Long whiteId, Integer pageNum, Integer pageSize);

    R addUserToWhiteList(Long whiteId, String userIds);
}
