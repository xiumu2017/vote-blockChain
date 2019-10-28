package com.gaoshan.linkvote.base;

import lombok.Data;

import java.util.Date;

/**
 * @author Paradise
 */
@Data
public class BaseEntity {
    private transient Long createUser;
    private transient Long updateUser;
    private transient Date createTime;
    private transient Date updateTime;
}
