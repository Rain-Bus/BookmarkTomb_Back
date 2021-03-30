package com.cn.bookmarktomb.excepotion;

import com.cn.bookmarktomb.model.constant.ErrorCodeConstant;
import lombok.Getter;

/**
 * The exception of can't get the entity;
 * @author fallen-angle
 */
@Getter
public class EntityNotFoundException extends RuntimeException {

    private Integer code = ErrorCodeConstant.DB_ENTITY_NOT_FOUND_CODE;

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(int code, String message){
        super(message);
        this.code = code;
    }

}