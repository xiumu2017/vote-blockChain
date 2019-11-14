package com.gaoshan.linkvote.user.controller;

import com.gaoshan.linkvote.base.R;
import com.gaoshan.linkvote.base.Rx;
import com.gaoshan.linkvote.base.component.UmsPermission;
import com.gaoshan.linkvote.user.bean.RegisterBean;
import com.gaoshan.linkvote.user.bean.SysUser;
import com.gaoshan.linkvote.user.bean.UserQuery;
import com.gaoshan.linkvote.user.service.SysUserService;
import com.gaoshan.linkvote.user.utils.UserUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
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
@Api(tags = "后台用户/管理员管理")
@RequestMapping("/admin")
@Slf4j
public class SysUserController {
    private final SysUserService adminService;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    public SysUserController(SysUserService adminService) {
        this.adminService = adminService;
    }

    @ApiIgnore
    @ApiOperation(value = "用户注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public R register(@RequestBody RegisterBean registerBean) {
        SysUser sysUser = adminService.register(registerBean);
        if (sysUser == null) {
            return Rx.fail("用户名已存在！");
        }
        return Rx.success(sysUser);
    }

    @ApiOperation(value = "登录以后返回token")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public R login(@ApiParam(value = "用户名密码") @RequestBody RegisterBean registerBean) {
        String token = adminService.login(registerBean.getUserName(), registerBean.getPassword());
        if (token == null) {
            return Rx.error("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        // 测试用，TODO 待删除
        tokenMap.put("cc", tokenHead + " " + token);
        return Rx.success(tokenMap);
    }

    @ApiOperation(value = "管理员修改密码")
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    @ResponseBody
    public R changePassword(String password, @ApiIgnore Principal principal) {
        if (StringUtils.isBlank(password)) {
            return Rx.error("密码不能为空");
        }
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

    @ApiOperation("拉取用户信息")
    @GetMapping(value = "/info")
    @ResponseBody
    public R getUserInfo(@ApiIgnore Principal principal) {
        log.info(principal.getName());
        return Rx.success(adminService.selectByName(principal.getName()));
    }

    // 管理员管理

    /**
     * 分页查询管理员列表
     *
     * @param principal 登录用户信息
     * @param userQuery 查询实体
     * @return 分页列表信息
     */
    @ApiOperation("分页查询管理员列表")
    @GetMapping(value = "/getAdminUserPage")
    @ResponseBody
    public R getAdminUserPage(@ApiIgnore Principal principal, UserQuery userQuery) {
        log.info("分页查询管理员列表" + principal.getName());
        return adminService.getAdminUserPage(userQuery);
    }

    @ApiOperation("新增管理员")
    @PostMapping(value = "addAdminUser")
    @ResponseBody
    public R addAdminUser(@ApiIgnore Principal principal, SysUser user) {
        // 数据校验 地址信息
        if (StringUtils.isBlank(user.getAddress()) || !user.getAddress().startsWith("0x")) {
            return Rx.error("address 参数格式错误");
        }
        //校验地址重复 用户名重复
        SysUser sysUser = adminService.selectByName(user.getUsername());
        if (sysUser != null) {
            if (sysUser.getIsAdmin() == 1) {
                return Rx.error("用户名已存在");
            }
        }
        SysUser createUser = adminService.selectByName(principal.getName());
        sysUser = adminService.selectByAddress(user.getAddress());
        if (sysUser != null) {
            if (UserUtils.isAdmin(sysUser)) {
                return Rx.error("address 已存在");
            } else {
                // 更新用户为管理员
                user.setUpdateUser(createUser.getId());
                user.setStatus("1");
                user.setIsAdmin(1);
                user.setId(sysUser.getId());
                adminService.updateByPrimaryKeySelective(user);
                return Rx.success();
            }
        }
        user.setCreateUser(createUser.getId());
        user.setUpdateUser(createUser.getId());
        user.setStatus("1");
        user.setIsAdmin(1);
        adminService.insert(user);
        return Rx.success();
    }


    @ApiOperation("修改管理员")
    @PostMapping(value = "updateAdminUser")
    @ResponseBody
    public R updateAdminUser(@ApiIgnore Principal principal, SysUser user) {
        if (user.getId() == null) {
            return Rx.error("id 为空");
        }
        if (StringUtils.isBlank(user.getAddress()) || !user.getAddress().startsWith("0x")) {
            return Rx.error("address 参数格式错误");
        }
        SysUser priUser = adminService.selectByPrimaryKey(user.getId());
        if (!priUser.getAddress().equals(user.getAddress()) &&
                adminService.selectByAddress(user.getAddress()) != null) {
            return Rx.error("address 已存在");
        }
        if (!priUser.getUsername().equals(user.getUsername()) &&
                adminService.selectByName(user.getUsername()) != null) {
            return Rx.error("用户名已存在");
        }

        SysUser sysUser = adminService.selectByName(principal.getName());
        user.setUpdateUser(sysUser.getId());
        adminService.updateByPrimaryKeySelective(user);
        return Rx.success();
    }

    @ApiOperation("删除管理员")
    @PostMapping(value = "/delAdminUser")
    @ResponseBody
    public R delAdminUser(@ApiIgnore Principal principal, Long userId) {
        if (userId == null) {
            return Rx.error("userId 参数为空");
        }
        return adminService.delAdminUser(principal, userId);
    }

}
