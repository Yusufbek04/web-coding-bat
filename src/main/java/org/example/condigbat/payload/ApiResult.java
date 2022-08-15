package org.example.condigbat.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class ApiResult<E> {


    private boolean success;

    private String message;

    private E data;

    private List<ErrorData> errors;

    private ApiResult(String message, E data) {
        this.success = true;
        this.message = message;
        this.data = data;
    }

    public ApiResult(E data) {
        this.success=true;
        this.data = data;
    }

    private ApiResult(List<ErrorData> errors) {
        this.errors = errors;
    }

    public static <T> ApiResult<T> successResponse(String message, T data) {
        return new ApiResult<>(message, data);
    }

    public static <T> ApiResult<T> successResponse(T data) {
        return new ApiResult<>(data);
    }

    public static ApiResult<List<ErrorData>> failResponse(List<ErrorData> errors) {
        return new ApiResult<>(errors);
    }

    public static ApiResult<List<ErrorData>> failResponse(String msg, Integer code) {
        List<ErrorData> errorDataList = List.of(new ErrorData(msg, code));

        return failResponse(errorDataList);
    }


}
