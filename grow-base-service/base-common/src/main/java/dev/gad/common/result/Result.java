package dev.gad.common.result;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Result", description = "所有接口使用的统一JSON响应结构")
public class Result<T> {

    @Schema(description = "业务状态码", example = "200")
    private Integer code;
    @Schema(description = "响应消息", example = "操作成功")
    private String message;
    @Schema(description = "响应数据")
    private T data;

    private Result() {}

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> Result<T> fail(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    public static <T> Result<T> fail(ErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public Integer getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }

    public T getData(){
        return data;
    }

}
