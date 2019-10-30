package com.gaoshan.linkvote.vote.entity;

import lombok.Data;

import java.util.Date;

@Data
public class VoteUser {
    private Long id;

    private Long userId;

    private Long voteId;

    private Long optId;

    private String address;

    private String status;

    private String hash;

    private Date voteTime;
}