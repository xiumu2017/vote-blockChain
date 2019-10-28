package com.gaoshan.linkvote.vote.entity;

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
public class VoteQuery {

    @ApiModelProperty("投票主题")
    private String topic;

    @ApiModelProperty(value = "投票状态，未开始、进行中、已结束")
    private String status;

    @ApiModelProperty(value = "pageNum", example = "1")
    private int pageNum = 1;
    @ApiModelProperty(value = "pageSize", example = "10")
    private int pageSize = 10;

    @ApiModelProperty(value = "查询开始时间", example = "2019-10-24 11:22:23")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:ss:mm")
    private Date queryStartTime;
    @ApiModelProperty(value = "查询结束时间", example = "2019-11-20 11:22:23")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:ss:mm")
    private Date queryEntTime;
}