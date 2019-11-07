package com.gaoshan.linkvote.user.service.impl;

import com.gaoshan.linkvote.base.R;
import com.gaoshan.linkvote.base.Rx;
import com.gaoshan.linkvote.base.utils.JwtTokenUtil;
import com.gaoshan.linkvote.user.bean.RegisterBean;
import com.gaoshan.linkvote.user.bean.SysUser;
import com.gaoshan.linkvote.user.bean.UserQuery;
import com.gaoshan.linkvote.user.service.SysUserService;
import com.gaoshan.linkvote.user.mapper.SysUserMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.List;

@Service
@Slf4j
public class SysUserServiceImpl implements SysUserService {
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Resource
    private SysUserMapper sysUserMapper;

    public SysUserServiceImpl(UserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return sysUserMapper.deleteByPrimaryKey(id);
    }

    @Override
    public R delAdminUser(Principal principal, Long userId) {
        SysUser sysUser = sysUserMapper.selectByName(principal.getName());
        if (sysUserMapper.delAdminUser(sysUser.getId(), userId) == 1) {
            return Rx.success();
        }
        return Rx.fail();
    }

    @Override
    public int insert(SysUser record) {
        return sysUserMapper.insert(record);
    }

    @Override
    public SysUser selectByPrimaryKey(Long id) {
        return sysUserMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(SysUser record) {
        return sysUserMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public SysUser register(RegisterBean registerBean) {
        SysUser sysUser = new SysUser();
        sysUser.setStatus("1");
        //查询是否有相同用户名的用户
        SysUser user = sysUserMapper.selectByName(registerBean.getUserName());
        if (user != null) {
            return null;
        }
        //将密码进行加密操作
        String encodePassword = passwordEncoder.encode(registerBean.getPassword());
        sysUser.setPassword(encodePassword);
        sysUser.setUsername(registerBean.getUserName());
        sysUserMapper.insert(sysUser);
        return sysUser;
    }

    @Override
    public String login(String username, String password) {
        String token = null;
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("密码不正确");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                    null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            log.warn("登录异常:{}", e.getMessage());
        }
        return token;
    }

    @Override
    public SysUser selectByName(String name) {
        return sysUserMapper.selectByName(name);
    }

    @Override
    public SysUser selectByAddress(String address) {
        return sysUserMapper.selectByAddress(address);
    }

    @Override
    public R changePassword(String password, Principal principal) {
        SysUser user = sysUserMapper.selectByName(principal.getName());
        String encodePassword = passwordEncoder.encode(password);
        if (encodePassword.equals(user.getPassword())) {
            return Rx.error("新旧密码相同");
        }
        if (sysUserMapper.changePassword(user.getId(), encodePassword) == 1) {
            return Rx.success();
        }
        return Rx.fail();
    }

    @Override
    public R getAdminUserPage(UserQuery userQuery) {
        SysUser user = new SysUser();
        user.setUsername(userQuery.getUsername());
        user.setAddress(userQuery.getAddress());
        user.setNickname(userQuery.getNickname());
        user.setIsAdmin(1);
        user.setStatus("1");
        PageHelper.startPage(userQuery.getPageNum(), userQuery.getPageSize());
        List<SysUser> list = sysUserMapper.selectByAll(user);
        return Rx.success(new PageInfo<>(list));
    }
}
