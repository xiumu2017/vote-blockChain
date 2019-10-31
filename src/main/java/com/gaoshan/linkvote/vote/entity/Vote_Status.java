package com.gaoshan.linkvote.vote.entity;

public enum Vote_Status {
    /**
     * 初始化创建
     */
    CREATE("0"),
    /**
     * 上链成功
     */
    BLOCK_SUCCESS("1"),
    /**
     * 上链失败
     */
    BLOCK_FAIL("2"),
    /**
     * 进行中
     */
    ING("3"),
    /**
     * 已截止
     */
    ENDED("4");

    private String code;

    Vote_Status(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
