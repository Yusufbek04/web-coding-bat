package org.example.condigbat.error;


import org.example.condigbat.payload.ApiResult;
import org.example.condigbat.payload.ErrorData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = RestException.class)
    public ResponseEntity<ApiResult<List<ErrorData>>> exceptionHandle(RestException ex) {
        System.out.println(ex.getMessage());
        ApiResult<List<ErrorData>> result =
                ApiResult.failResponse(ex.getMessage(),
                        ex.getStatus().value());
        return new ResponseEntity<>(result, ex.getStatus());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<List<ErrorData>>> exceptionHandle(
            MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        List<ErrorData> errorDataList = new ArrayList<>();

        for (FieldError fieldError : fieldErrors)
            errorDataList.add(
                    new ErrorData(fieldError.getDefaultMessage(),
                            HttpStatus.BAD_REQUEST.value(),
                            fieldError.getField()));

        ApiResult<List<ErrorData>> apiResult = ApiResult.failResponse(errorDataList);
        return new ResponseEntity<>(apiResult, HttpStatus.BAD_REQUEST);
    }

}
