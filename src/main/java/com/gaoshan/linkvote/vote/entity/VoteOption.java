package com.gaoshan.linkvote.vote.entity;

import com.gaoshan.linkvote.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class VoteOption extends BaseEntity {
    private Long id;

    private Long voteId;

    private String index;

    private String content;

    private String status;

    private Long count;
}