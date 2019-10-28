package com.gaoshan.linkvote.user.service;

import com.gaoshan.linkvote.base.R;
import com.gaoshan.linkvote.base.component.UmsPermission;
import com.gaoshan.linkvote.user.bean.RegisterBean;
import com.gaoshan.linkvote.user.bean.SysUser;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

public interface SysUserService {


    int deleteByPrimaryKey(Long id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    SysUser register(RegisterBean registerBean);

    String login(String username, String password);

    default List<UmsPermission> getPermissionList(Long adminId) {
        return new ArrayList<>();
    }

    SysUser selectByName(String userName);

    R changePassword(String password, Principal principal);
}
