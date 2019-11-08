package com.gaoshan.linkvote.user.entity;

import com.gaoshan.linkvote.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "黑名单用户")
@Data
public class BlackUser extends BaseEntity {
    public BlackUser() {
    }

    public BlackUser(Long blackId, String address) {
        this.blackId = blackId;
        this.address = address;
    }

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "关联黑名单id")
    private Long blackId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户地址")
    private String address;
}