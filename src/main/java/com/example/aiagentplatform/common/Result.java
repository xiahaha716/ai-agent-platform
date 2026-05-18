package com.example.aiagentplatform.common;

public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    // 无参构造
    public Result() {}

    // 全参构造
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "Success", data);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    // Getter 和 Setter 方法 (如果你项目里有 Lombok 的 @Data 注解，也可以直接在类头上加 @Data，把下面这些省略掉)
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
