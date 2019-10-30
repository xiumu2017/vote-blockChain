package com.gaoshan.linkvote.vote.entity;

public enum Vote_Status {
    CREATE("0"),BLCOK_SUCCESS("2"),BLOBK_FAIL("3"), ING("1"), ENDED("2");

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
