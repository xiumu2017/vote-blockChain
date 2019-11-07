package com.gaoshan.linkvote.user.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("管理员登录实体信息")
@Data
public class RegisterBean {
    @ApiModelProperty(value = "用户名", required = true, example = "adminX")
    private String userName;
    @ApiModelProperty(value = "密码", required = true, example = "123456")
    private String password;
}
