package com.gaoshan.linkvote.user.controller;

import com.gaoshan.linkvote.base.R;
import com.gaoshan.linkvote.base.Rx;
import com.gaoshan.linkvote.base.component.UmsPermission;
import com.gaoshan.linkvote.user.bean.SysUser;
import com.gaoshan.linkvote.user.service.SysUserService;
import com.gaoshan.linkvote.user.bean.RegisterBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台用户管理
 * Created by macro on 2018/4/26.
 */
@Controller
@Api(tags = "后台用户管理")
@RequestMapping("/admin")
@Slf4j
public class SysUserController {
    @Autowired
    private SysUserService adminService;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @ApiOperation(value = "用户注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public R register(@RequestBody RegisterBean registerBean, BindingResult result) {
        SysUser sysUser = adminService.register(registerBean);
        if (sysUser == null) {
            return Rx.fail("用户名已存在！");
        }
        return Rx.success(sysUser);
    }

    @ApiOperation(value = "登录以后返回token")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public R login(@ApiParam(value = "用户名密码") @RequestBody RegisterBean registerBean, BindingResult result) {
        String token = adminService.login(registerBean.getUserName(), registerBean.getPassword());
        if (token == null) {
            return Rx.error("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        tokenMap.put("cc", tokenHead + " " + token);
        return Rx.success(tokenMap);
    }

    @ApiOperation(value = "管理员修改密码")
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    @ResponseBody
    public R changePassword(@RequestBody String password, Principal principal) {
        return adminService.changePassword(password, principal);
    }

    @ApiIgnore
    @ApiOperation("获取用户所有权限（包括+-权限）")
    @RequestMapping(value = "/permission/{adminId}", method = RequestMethod.GET)
    @ResponseBody
    public R getPermissionList(@PathVariable Long adminId) {
        List<UmsPermission> permissionList = adminService.getPermissionList(adminId);
        return Rx.success(permissionList);
    }

    @ApiIgnore
    @ApiOperation("拉取用户信息")
    @GetMapping(value = "/info")
    @ResponseBody
    public R getUserInfo(Principal principal) {
        log.info(principal.getName());
        return Rx.success(adminService.selectByName(principal.getName()));
    }
}
