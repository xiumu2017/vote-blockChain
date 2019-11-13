package com.gaoshan.linkvote.user.utils;

import com.gaoshan.linkvote.user.bean.SysUser;

public class UserUtils {
    public static boolean isAdmin(SysUser user) {
        return user != null && user.getIsAdmin() != null && user.getIsAdmin() == 1;
    }
}
