package com.gaoshan.linkvote.user.entity;

import com.gaoshan.linkvote.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "黑名单")
@Data
public class BlackList extends BaseEntity {
    public BlackList(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public BlackList() {
    }

    @ApiModelProperty(value = "主键", hidden = true, example = "1")
    private Long id;

    /**
     * 黑名单名称
     */
    @ApiModelProperty(value = "黑名单名称")
    private String name;

    /**
     * 黑名单备注
     */
    @ApiModelProperty(value = "黑名单备注")
    private String desc;
}