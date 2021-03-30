package com.cn.bookmarktomb.excepotion;

import com.cn.bookmarktomb.model.constant.ErrorCodeConstant;
import lombok.Getter;

/**
 * The exception about user request data error;
 * @author fallen-angle
 */
@Getter
public class BadRequestException extends RuntimeException{

    private Integer code = ErrorCodeConstant.USER_REQUEST_ERROR_CODE;

    public BadRequestException(String message){
        super(message);
    }

    public BadRequestException(int code, String message){
        super(message);
        this.code = code;
    }
}
