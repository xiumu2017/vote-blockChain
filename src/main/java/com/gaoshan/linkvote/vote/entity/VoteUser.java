package com.gaoshan.linkvote.vote.entity;

import com.gaoshan.linkvote.base.BaseEntity;
import java.util.Date;
import lombok.Data;

@Data
public class VoteUser extends BaseEntity {
    private Long id;

    private Long userId;

    private Long voteId;

    private Long optId;

    private Date voteTime;
}