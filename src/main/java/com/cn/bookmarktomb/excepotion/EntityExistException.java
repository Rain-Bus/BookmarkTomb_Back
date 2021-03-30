package com.cn.bookmarktomb.excepotion;

import com.cn.bookmarktomb.model.constant.ErrorCodeConstant;
import lombok.Getter;

/**
 * The exception of the entity need to insert(or other operation) has been existing;
 * @author fallen-angle
 */
@Getter
public class EntityExistException extends RuntimeException {

    private Integer code = ErrorCodeConstant.DB_OPERATION_CODE;
    private Object data;

    public EntityExistException(String message) {
        super(message);
    }

    public EntityExistException(int code, String message) {
        super(message);
        this.code = code;
    }

    public EntityExistException(int code, Object data) {
        super();
        this.code = code;
        this.data = data;
    }

}