package com.gaoshan.linkvote.vote.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author Paradise
 */
@Data
@ApiModel("投票实体")
public class VoteModel {

    @ApiModelProperty(value = "投票id", hidden = true)
    private Long id;

    @ApiModelProperty(value = "投票主题", required = true)
    private String topic;

    @ApiModelProperty("投票描述")
    private String desc;

    @ApiModelProperty(value = "投票图片Id", hidden = true)
    private Long fileId;

    @ApiModelProperty(value = "投票图片url", example = "http://192.168.100.48:8080/moac-vote/vote/showPic?fileId=1")
    private String picUrl;

    @ApiModelProperty(value = "投票限制多选数量，单选则为1", example = "1")
    private Integer limitNum;

    @ApiModelProperty(value = "投票开始时间", hidden = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT +8")
    private Date startTime;

    @ApiModelProperty(value = "投票结束时间", required = true, example = "2019-11-11 12:00:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
}