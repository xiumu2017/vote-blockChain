package com.gaoshan.linkvote.user.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("用户注册")
@Data
public class RegisterBean {
    @ApiModelProperty(value = "用户名", required = true, example = "user")
    private String userName;
    @ApiModelProperty(value = "密码", required = true, example = "123")
    private String password;
}
