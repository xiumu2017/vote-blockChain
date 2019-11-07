package com.gaoshan.linkvote.user.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Date;

/**
 * @author Paradise
 */
@EqualsAndHashCode()
@Data
@ApiModel
public class SysUser {
    @ApiModelProperty(value = "编辑时必填；新增不需要")
    private Long id;

    /**
     * 钱包地址
     */
    @ApiModelProperty(value = "地址")
    private String address;
    @ApiModelProperty(value = "登录名")
    private String username;
    @ApiModelProperty(value = "昵称")
    private String nickname;
    @ApiModelProperty(value = "年龄")
    private Integer age;
    @ApiModelProperty(hidden = true)
    private String status;
    @ApiModelProperty(value = "头像")
    private String picUrl;

    /**
     * 是否是管理员 1 true 0 false
     */
    @ApiModelProperty(hidden = true)
    private Integer isAdmin;

    @ApiModelProperty(hidden = true)
    private Date createTime;
    @ApiModelProperty(hidden = true)
    private Date updateTime;
    @ApiModelProperty(hidden = true)
    private Long createUser;
    @ApiModelProperty(hidden = true)
    private Long updateUser;
    @ApiModelProperty(hidden = true)
    private String password;
}