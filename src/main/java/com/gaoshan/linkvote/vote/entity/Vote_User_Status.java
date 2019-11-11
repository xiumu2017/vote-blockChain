package com.gaoshan.linkvote.vote.entity;

public enum Vote_User_Status {
    /**
     * 初始化创建 - 未上链
     */
    CREATE("0"),
    /**
     * 等待交易确认
     */
    WAIT_CONFIRM("1"),
    /**
     * 交易成功
     */
    TX_SUCCESS("2"),
    /**
     * 交易失败
     */
    TX_FAIL("3");

    private String code;

    Vote_User_Status(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
