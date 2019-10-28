package com.gaoshan.linkvote.user.controller;

import com.gaoshan.linkvote.base.R;
import com.gaoshan.linkvote.user.entity.BlackList;
import com.gaoshan.linkvote.user.entity.WhiteList;
import com.gaoshan.linkvote.user.service.BlackWhiteUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Api(tags = "黑白名单相关接口")
@RestController
@RequestMapping("/user")
public class BlackWhiteUserController {
    private final BlackWhiteUserService blackWhiteUserService;

    @Autowired
    public BlackWhiteUserController(BlackWhiteUserService blackWhiteUserService) {
        this.blackWhiteUserService = blackWhiteUserService;
    }

    @ApiOperation("新增黑名单")
    @PostMapping("/addBlack")
    public R addBlack(BlackList blackList, Principal principal) {
        return blackWhiteUserService.addBlack(blackList, principal);
    }

    @ApiOperation("修改黑名单")
    @PostMapping("/updateBlack")
    public R updateBlack(BlackList blackList, Principal principal) {
        return blackWhiteUserService.updateBlack(blackList, principal);
    }

    @ApiOperation("删除黑名单")
    @PostMapping("/delBlack")
    public R delBlack(Long id, Principal principal) {
        return blackWhiteUserService.delBlack(id, principal);
    }

    @ApiOperation("分页查询黑名单列表")
    @GetMapping("/queryBlackPage")
    public R queryBlackPage(BlackList blackList, Principal principal,
                            Integer pageNum, Integer pageSize) {
        return blackWhiteUserService.queryBlackPage(blackList, pageNum, pageSize, principal);
    }

    @ApiOperation("查询黑名单中用户列表")
    @GetMapping("/queryBlackUserList")
    public R queryBlackUserList(Long blackId, Integer pageNum, Integer pageSize) {
        return blackWhiteUserService.queryBlackUserList(blackId, pageNum, pageSize);
    }

    @ApiOperation("增加用户到黑名单列表")
    @PostMapping("/addUserToBlackList")
    public R addUserToBlackList(Long blackId, String userIds) {
        return blackWhiteUserService.addUserToBlackList(blackId, userIds);
    }

    @ApiOperation("从黑名单列表中删除")
    @PostMapping("/delFromBlackList")
    public R delFromBlackList(Long id) {
        return blackWhiteUserService.delFromBlackList(id);
    }


    @ApiOperation("新增白名单")
    @PostMapping("/addWhite")
    public R addWhite(WhiteList whiteList, Principal principal) {
        return blackWhiteUserService.addWhite(whiteList, principal);
    }

    @ApiOperation("修改白名单")
    @PostMapping("/updateWhite")
    public R updateWhite(WhiteList whiteList, Principal principal) {
        return blackWhiteUserService.updateWhite(whiteList, principal);
    }

    @ApiOperation("删除白名单")
    @PostMapping("/delWhite")
    public R delWhite(Long id, Principal principal) {
        return blackWhiteUserService.delWhite(id, principal);
    }

    @ApiOperation("分页查询白名单列表")
    @GetMapping("/queryWhitePage")
    public R queryWhitePage(WhiteList whiteList, Principal principal,
                            Integer pageNum, Integer pageSize) {
        return blackWhiteUserService.queryWhitePage(whiteList, pageNum, pageSize, principal);
    }

    @ApiOperation("分页查询白名单中用户列表")
    @GetMapping("/queryWhiteUserList")
    public R queryWhiteUserList(Long whiteId, Integer pageNum, Integer pageSize) {
        return blackWhiteUserService.queryWhiteUserList(whiteId, pageNum, pageSize);
    }

    @ApiOperation("增加用户到白名单列表")
    @PostMapping("/addUserToWhiteList")
    public R addUserToWhiteList(Long whiteId, String userIds) {
        return blackWhiteUserService.addUserToWhiteList(whiteId, userIds);
    }

    @ApiOperation("从白名单列表中删除")
    @PostMapping("/delFromWhiteList")
    public R delFromWhiteList(Long id) {
        return blackWhiteUserService.delFromWhiteList(id);
    }

}
