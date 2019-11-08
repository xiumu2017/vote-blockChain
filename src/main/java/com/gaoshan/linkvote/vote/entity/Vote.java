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
public class Vote {

    public Vote() {
    }

    public Vote(VoteModel model) {
        this.topic = model.getTopic();
        this.desc = model.getDesc();
        this.picUrl = model.getPicUrl();
        this.endTime = model.getEndTime();
        this.startTime = model.getStartTime();
        this.limitNum = model.getLimitNum();
    }


    @ApiModelProperty(value = "投票id", hidden = true)
    private Long id;

    @ApiModelProperty(value = "投票主题", required = true)
    private String topic;

    @ApiModelProperty("投票描述")
    private String desc;

    @ApiModelProperty(value = "投票图片Id", hidden = true)
    private Long fileId;

    @ApiModelProperty(value = "投票图片url", required = true)
    private String picUrl;

    @ApiModelProperty(value = "投票类型", hidden = true)
    private String type;

    @ApiModelProperty(value = "投票状态", hidden = true)
    private String status;

    @ApiModelProperty(hidden = true)
    private String isDel;

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

    @ApiModelProperty(hidden = true)
    private String hash;

    @ApiModelProperty(hidden = true)
    private Long createUser;
    @ApiModelProperty(hidden = true)
    private Long updateUser;
    @ApiModelProperty(hidden = true)
    private Date createTime;
    @ApiModelProperty(hidden = true)
    private Date updateTime;

    /**
     * 投票选项列表
     */
    @ApiModelProperty(hidden = true)
    private List<VoteOption> optionList;
    /**
     * 投票用户选项列表
     */
    @ApiModelProperty(hidden = true)
    private List<VoteUser> voteUserList;

    /**
     * 创建人 - 昵称
     */
    @ApiModelProperty(hidden = true)
    private String createUserName;
    /**
     * 创建人 - 头像url
     */
    @ApiModelProperty(hidden = true)
    private String createUserPic;

    @ApiModelProperty(hidden = true)
    private Long blackId;
    @ApiModelProperty(hidden = true)
    private Long whiteId;

}