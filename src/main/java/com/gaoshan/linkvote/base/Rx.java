package com.gaoshan.linkvote.base;

public class Rx {

    private static final String SUCCESS_CODE = "200";
    private static final String FAIL_CODE = "500";
    private static final String FORBIDDEN_CODE = "FORBIDDEN_";
    private static final String UNAUTHORIZED_CODE = "UNAUTHORIZED_";
    private static final String SUCCESS_MSG = "操作成功！";
    private static final String FAIL_MSG = "操作失败！";

    public static <T> R<T> success(T data) {
        return new R<>(SUCCESS_CODE, SUCCESS_MSG, data);
    }

    public static <T> R<T> success() {
        return new R<>(SUCCESS_CODE, SUCCESS_MSG, null);
    }

    public static <T> R<T> success(String msg, T data) {
        return new R<>(SUCCESS_CODE, msg, data);
    }

    public static <T> R<T> error(String code, String msg) {
        return new R<>(code, msg, null);
    }

    public static <T> R<T> error(String msg) {
        return new R<>("500", msg, null);
    }

    public static <T> R<T> error(String code, String msg, T data) {
        return new R<>(code, msg, data);
    }

    public static <T> R<T> fail() {
        return fail(FAIL_MSG);
    }

    public static <T> R<T> forbidden(String message) {
        return new R<>(FORBIDDEN_CODE, message, null);
    }

    public static <T> R<T> unauthorized(String message) {
        return new R<>(UNAUTHORIZED_CODE, message, null);
    }

    public static <T> R<T> fail(String msg) {
        return new R<>(FAIL_CODE, msg, null);
    }
}
