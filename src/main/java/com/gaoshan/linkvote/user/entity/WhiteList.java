package com.gaoshan.linkvote.user.entity;

import com.gaoshan.linkvote.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "白名单")
@Data
public class WhiteList extends BaseEntity {
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "白名单名称")
    private String name;

    @ApiModelProperty(value = "白名单备注")
    private String desc;

    public WhiteList(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public WhiteList() {
    }
}