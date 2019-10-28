package com.gaoshan.linkvote.vote.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author Paradise
 */
@Data
@ApiModel("投票实体")
public class Vote {

    @ApiModelProperty(value = "投票id", hidden = true)
    private Long id;

    @ApiModelProperty("投票主题")
    private String topic;

    @ApiModelProperty("投票描述")
    private String desc;

    @ApiModelProperty(value = "投票图片url", hidden = true)
    private String picture;

    @ApiModelProperty(value = "投票类型", hidden = true)
    private String type;

    @ApiModelProperty(value = "投票状态，未开始、进行中、已结束", hidden = true)
    private String status;

    @ApiModelProperty(hidden = true)
    private String isDel;

    @ApiModelProperty(value = "投票限制多选数量，单选则为1", example = "1")
    private Integer limitNum;

    @ApiModelProperty("投票开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT +8")
    private Date startTime;

    @ApiModelProperty("投票结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private Long createUser;
    private Long updateUser;
    private Date createTime;
    private Date updateTime;
}