package com.gaoshan.linkvote.vote.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.gaoshan.linkvote.base.R;
import com.gaoshan.linkvote.base.Rx;
import com.gaoshan.linkvote.user.bean.SysUser;
import com.gaoshan.linkvote.user.bean.UserHash;
import com.gaoshan.linkvote.user.entity.BlackList;
import com.gaoshan.linkvote.user.entity.BlackUser;
import com.gaoshan.linkvote.user.entity.WhiteList;
import com.gaoshan.linkvote.user.entity.WhiteUser;
import com.gaoshan.linkvote.user.mapper.*;
import com.gaoshan.linkvote.user.utils.UserUtils;
import com.gaoshan.linkvote.vote.entity.*;
import com.gaoshan.linkvote.vote.mapper.VoteMapper;
import com.gaoshan.linkvote.vote.mapper.VoteOptionMapper;
import com.gaoshan.linkvote.vote.mapper.VoteUserMapper;
import com.gaoshan.linkvote.vote.service.VoteService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
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
    @Resource
    private BlackUserMapper blackUserMapper;
    @Resource
    private WhiteUserMapper whiteUserMapper;
    @Resource
    private BlackListMapper blackListMapper;
    @Resource
    private WhiteListMapper whiteListMapper;

    @Override
    public int deleteByPrimaryKey(Long id) {
        return voteMapper.deleteByPrimaryKey(id);
    }

    @Override
    public R insert(Vote record, String optionJson, String address) {
        SysUser user = userMapper.selectByAddress(address);
        if (!UserUtils.isAdmin(user)) {
            return Rx.error("没有操作权限");
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
        List<UserHash> userHashList = userMapper.selectVoteUserHashByVoteId(voteId);
        resultMap.put("userHashList", userHashList);
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
                map.put("hash", voteUser.getHash());
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
        vote.setStatus(Vote_Status.WAIT_CONFIRM.getCode());
        // 更新投票的hash和投票状态
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
        List<String> indexList = new ArrayList<>();
        VoteOption option;
        for (String optionId : optionArr) {
            option = optionMapper.selectByPrimaryKey(Long.valueOf(optionId));
            if (option == null) {
                return Rx.error("投票选项id不存在");
            } else {
                indexList.add(option.getIndex());
                optionIdList.add(Long.valueOf(optionId));
            }
        }
        // 删除原有记录
        int r = voteUserMapper.deleteByVoteIdAndUserId(voteId, user.getId());
        log.info("删除原有投票记录: " + r);
        voteUserMapper.insertBatch(user.getId(), address, voteId, optionIdList);
        // 处理返回的json
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("voteId", voteId);
        resMap.put("hash", vote.getHash());
        resMap.put("indexList", indexList);
        return Rx.success(resMap);
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
        SysUser sysUser = userMapper.selectByAddress(address);
        // 黑白名单处理
        // 根据用户address 查询 黑名单 list，关联投票id； 排除这些投票 not in
        List<String> voteIdListBlack = voteMapper.selectByBlackAddress(address);
        // 查询全部 白名单投票 list ; 判断当前地址是否包含在内，不包含则 排除
        List<String> voteIdListWhite = voteMapper.selectByWhiteAddress(address);
        voteIdListBlack.addAll(voteIdListWhite);
        Long userId = sysUser == null ? null : sysUser.getId();

        PageHelper.startPage(pageNum, pageSize);
        List<Vote> list = voteMapper.selectByApp(voteIdListBlack, userId);
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

        // 权限判断 在黑名单 or 不在白名单，么得权限
        if (vote.getBlackId() != null) {
            if (blackUserMapper.selectCountByBlackIdAndAddress(vote.getBlackId(), address) > 0) {
                return Rx.error("没有权限");
            }
        }
        if (vote.getWhiteId() != null
                && whiteUserMapper.selectAddressByWhiteId(vote.getWhiteId()).size() > 0) {
            if (whiteUserMapper.countByWhiteIdAndAddress(vote.getWhiteId(), address) < 1) {
                return Rx.error("没有权限");
            }
        }

        SysUser createUser = userMapper.selectByPrimaryKey(vote.getCreateUser());
        vote.setCreateUserName(createUser.getUsername());
        vote.setCreateUserPic(createUser.getPicUrl());
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
        List<UserHash> userHashList = userMapper.selectVoteUserHashByVoteId(voteId);
        resultMap.put("userHashList", userHashList);
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

    @Override
    public R addVoteBWList(MultipartFile file, Long voteId, String type) {
        // 文件解析成号码列表
        List<String> addressList = new ArrayList<>();
        try {
            addressList = dealTxtFile(file);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        if (addressList.size() < 1) {
            return Rx.error("地址列表为空");
        }
        // 判断是否已有黑白名单；有则去重追加；否则新增
        Vote vote = voteMapper.selectByPrimaryKey(voteId);
        if (type.equals("black")) {
            if (vote.getBlackId() != null) {
                List<BlackUser> blackUserList = blackUserMapper.selectAllByBlackId(vote.getBlackId());
                Map<String, Long> map = listToMap(blackUserList);
                for (String address : addressList) {
                    if (map.get(address) == null) {
                        map.put(address, 0L);
                        blackUserMapper.insert(new BlackUser(vote.getBlackId(), address));
                    }
                }
            } else {
                // 新增一个 black_list
                BlackList blackList = new BlackList(file.getName(), file.getOriginalFilename() + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
                blackListMapper.insert(blackList);
                // 批量保存 black_user
                if (blackList.getId() != null) {
                    blackUserMapper.batchInsert(blackList.getId(), addressList);
                }
                // 更新投票的黑名单id
                Vote v = new Vote();
                v.setId(voteId);
                v.setBlackId(blackList.getId());
                voteMapper.updateByPrimaryKeySelective(v);
            }
        } else if (type.equals("white")) {
            if (vote.getWhiteId() != null) {
                List<WhiteUser> whiteUserList = whiteUserMapper.selectAllByWhiteId(vote.getWhiteId());
                Map<String, Long> map = listToMap2(whiteUserList);
                for (String address : addressList) {
                    if (map.get(address) == null) {
                        map.put(address, 0L);
                        whiteUserMapper.insert(new WhiteUser(vote.getWhiteId(), address));
                    }
                }
            } else {
                // 新增一个 black_list
                WhiteList whiteList = new WhiteList(file.getName(), file.getOriginalFilename() + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
                whiteListMapper.insert(whiteList);
                // 批量保存 black_user
                if (whiteList.getId() != null) {
                    whiteUserMapper.batchInsert(whiteList.getId(), addressList);
                }
                // 更新投票的黑名单id
                Vote v = new Vote();
                v.setId(voteId);
                v.setWhiteId(whiteList.getId());
                voteMapper.updateByPrimaryKeySelective(v);
            }
        }
        return Rx.success();
    }

    @Override
    public R getBlackPage(Long voteId, Integer pageNum, Integer pageSize) {
        Vote vote = voteMapper.selectByPrimaryKey(voteId);
        PageHelper.startPage(pageNum, pageSize);
        List<BlackUser> addressList = blackUserMapper.selectAllByBlackId(vote.getBlackId());
        return Rx.success(new PageInfo<>(addressList));
    }

    @Override
    public R getWhitePage(Long voteId, Integer pageNum, Integer pageSize) {
        Vote vote = voteMapper.selectByPrimaryKey(voteId);
        PageHelper.startPage(pageNum, pageSize);
        List<WhiteUser> addressList = whiteUserMapper.selectAllByWhiteId(vote.getWhiteId());
        return Rx.success(new PageInfo<>(addressList));
    }

    @Override
    public R delWhiteUser(Long id) {
        if (whiteUserMapper.selectByPrimaryKey(id) == null) {
            return Rx.fail("数据已删除");
        }
        if (whiteUserMapper.deleteByPrimaryKey(id) == 1) {
            return Rx.success();
        }
        return Rx.success();
    }

    @Override
    public R delBlackUser(Long id) {
        if (blackUserMapper.selectByPrimaryKey(id) == null) {
            return Rx.fail("数据已删除");
        }
        if (blackUserMapper.deleteByPrimaryKey(id) == 1) {
            return Rx.success();
        }
        return Rx.success();
    }

    private Map<String, Long> listToMap(List<? extends BlackUser> list) {
        Map<String, Long> map = new HashMap<>();
        for (BlackUser blackUser : list) {
            map.put(blackUser.getAddress(), blackUser.getId());
        }
        return map;
    }

    private Map<String, Long> listToMap2(List<? extends WhiteUser> list) {
        Map<String, Long> map = new HashMap<>();
        for (WhiteUser WhiteUser : list) {
            map.put(WhiteUser.getAddress(), WhiteUser.getId());
        }
        return map;
    }

    private List<String> dealTxtFile(MultipartFile file) throws IOException {
        Set<String> addressList = new HashSet<>();
        // 建立一个输入流对象reader
        InputStreamReader reader = new InputStreamReader(file.getInputStream());
        // 建立一个对象，它把文件内容转成计算机能读懂的语言
        BufferedReader br = new BufferedReader(reader);
        // 一次读入一行数据
        String line = br.readLine();
        while (line != null) {
            if (line.startsWith("0x") && line.length() == "0x0000000000000000000000000000000000000064".length()) {
                addressList.add(line);
            }
            log.info(line);
            line = br.readLine();
        }
        return new ArrayList<>(addressList);
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
