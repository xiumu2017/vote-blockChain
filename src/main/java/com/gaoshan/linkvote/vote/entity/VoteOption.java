package com.gaoshan.linkvote.vote.entity;

import com.gaoshan.linkvote.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import springfox.documentation.annotations.ApiIgnore;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiIgnore
public class VoteOption extends BaseEntity {
    private Long id;

    private Long voteId;

    private String index;

    private String content;

    private String status;

    private Long count;
}