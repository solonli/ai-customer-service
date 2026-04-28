package com.smartcs.common.core.result;

import lombok.Data;
import java.io.Serializable;

@Data
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer code;
    private String message;
    private T data;
    private Long timestamp;

    public R() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> R<T> ok() {
        return ok(null);
    }

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMessage("success");
        r.setData(data);
        return r;
    }

    public static <T> R<T> ok(T data, String message) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMessage(message);
        r.setData(data);
        return r;
    }

    public static <T> R<T> fail() {
        return fail(500, "系统错误");
    }

    public static <T> R<T> fail(String message) {
        return fail(500, message);
    }

    public static <T> R<T> fail(Integer code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

    public boolean isSuccess() {
        return this.code != null && this.code == 200;
    }
}
