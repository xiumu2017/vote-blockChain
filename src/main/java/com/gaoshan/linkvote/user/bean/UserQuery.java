package com.gaoshan.linkvote.user.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class UserQuery {
    @ApiModelProperty(value = "地址", example = "0x123")
    private String address;
    @ApiModelProperty(value = "用户名", example = "admin")
    private String username;
    @ApiModelProperty(value = "昵称", example = "admin")
    private String nickname;
    @ApiModelProperty(example = "1")
    private int pageNum;
    @ApiModelProperty(example = "10")
    private int pageSize;
}
