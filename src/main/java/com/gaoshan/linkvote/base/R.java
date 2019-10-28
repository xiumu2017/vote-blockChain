package com.gaoshan.linkvote.base;

import lombok.Data;

@Data
public class R<T> {
    private String code;
    private String msg;
    private T data;

    private R() {
    }

    R(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

}
