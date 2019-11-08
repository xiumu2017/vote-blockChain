package com.gaoshan.linkvote.user.entity;

import com.gaoshan.linkvote.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "白名单用户")
@Data
public class WhiteUser extends BaseEntity {
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "白名单主键")
    private Long whiteId;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户地址")
    private String address;

    public WhiteUser() {
    }

    public WhiteUser(Long whiteId, String address) {
        this.whiteId = whiteId;
        this.address = address;
    }
}