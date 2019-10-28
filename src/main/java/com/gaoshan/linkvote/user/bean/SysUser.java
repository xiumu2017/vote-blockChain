package com.gaoshan.linkvote.user.bean;

import com.gaoshan.linkvote.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author Paradise
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysUser extends BaseEntity {
    private Long id;

    /**
     * 钱包地址
     */
    private String address;

    private String name;

    private Integer age;

    private String status;

    private String pic;

    private Date createTime;

    private Date updateTime;

    private Long createUser;

    private Long updateUser;

    private String password;
}