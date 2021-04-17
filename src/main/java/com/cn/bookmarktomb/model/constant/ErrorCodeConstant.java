package com.cn.bookmarktomb.model.constant;

import com.cn.bookmarktomb.controller.PublicInfoController;

/**
 * @author fallen-anlgle
 * This is the error code constant.
 */
public class ErrorCodeConstant {

    public static final int SUCCESS_CODE = 0;
    public static final String SUCCESS_MSG = "SUCCESS";


    public static final int USER_REQUEST_ERROR_CODE = 400;
    public static final String USER_REQUEST_ERROR_MSG = "User request error";

    public static final int USER_ACCESS_DENY = 401;
    public static final String USER_ACCESS_DENY_MSG = "Access deny";

    public static final int USER_PERMISSION_DENY = 403;
    public static final String USER_PERMISSION_DENY_MSG = "Permission deny";

    public static final int UNEXPECT_URL_CODE = 404;
    public static final String UNEXPECT_URL_MSG = "Unexpect URL";

    public static final int FORM_DATE_ERROR_CODE = 4000;
    public static final String FORM_DATE_ERROR_MSG = "Form date error";

    public static final int USER_AUTH_ERROR_CODE = 4100;
    public static final String USER_AUTH_ERROR_MSG = "User auth error";

    public static final int USER_ACCOUNT_OR_PWD_ERROR_CODE = 4101;
    public static final String USER_ACCOUNT_OR_PWD_ERROR_MSG = "User account or password error";

    public static final int USER_ACCOUNT_NOT_FOUNT_CODE = 4102;
    public static final String USER_ACCOUNT_NOT_FOUND_MSG = "User account not found";

    public static final int USER_PWD_ERROR_CODE = 4103;
    public static final String USER_PWD_ERROR_MSG = "User password error";

    public static final int USER_VERIFY_ERROR_CODE = 4104;
    public static final String USER_VERIFY_ERROR_MSG = "User verify code error";

    public static final int USER_UNIQUE_ID_USED_ERROR_CODE = 4105;
    public static final String USER_UNIQUE_ID_USED_ERROR_MSG = "User unique id has been used";

    public static final int USER_BIND_ID_IS_NULL_CODE = 4106;
    public static final String USER_BIND_ID_IS_NULL_MSG = "User bind id is null";

    public static final int USER_ACCOUNT_NOT_ENABLED_CODE = 4107;
    public static final String USER_ACCOUNT_NOT_ENABLED_MSG = "User account hasn't been enabled, please contact with administrator";

    public static final int USER_TOKEN_EXPIRED_CODE = 4108;
    public static final String USER_TOKEN_EXPIRED_MSG = "User token was expired";

    public static final int USER_CREDENTIAL_ERROR_CODE = 4109;
    public static final String USER_CREDENTIAL_ERROR_MSG = "User's credential is invalid";

    public static final int USER_ACTIVE_EXPIRED_CODE = 4110;
    public static final String USER_ACTIVE_EXPIRED_MSG = "Account activate time is expired";

    public static final int DATA_NOT_FOUND_CODE = 4201;
    public static final String DATA_NOT_FOUND_MSG = "Data not found";

    public static final int DATA_EXISTS_CODE = 4202;
    public static final String DATA_EXISTS_MSG = "Data has been existed";

    public static final int SYSTEM_ERROR_CODE = 500;
    public static final String SYSTEM_ERROR_MSG = "System error";
    public static final String SYSTEM_ERROR_DATA = "System appeared errors, please contact with administrator or try again later!";

    public static final int CONFIGURATION_ERROR_CODE = 5000;

    public static final int SYSTEM_NOT_INIT_CODE = 5001;
    public static final String SYSTEM_NOT_INIT_MSG = "System hasn't been initialized";

    public static final int ADMIN_NOT_SET_CODE = 5002;
    public static final String ADMIN_NOT_SET_MSG = "Admin account hasn't been created";

    public static final int DATABASE_UNREACHABLE_CODE = 5003;
    public static final String DATABASE_UNREACHABLE_MSG = "Database reach error";

    public static final int DATABASE_NOT_AUTH_CODE = 5004;
    public static final String DATABASE_NOT_AUTH_MSG = "Database auth error";

    public static final int SYSTEM_STARING_CODE = 5005;
    public static final String SYSTEM_STARING_MSG = "System is starting";

    public static final int EMAIL_UNREACHABLE_CODE = 5006;
    public static final String EMAIL_UNREACHABLE_MSG = "Email host unreachable";

    public static final int EMAIL_NOT_AUTH_CODE = 5007;
    public static final String EMAIL_NOT_AUTH_MSG = "Email server auth error";

    public static final int DB_OPERATION_CODE = 5100;
    public static final String DB_OPERATION_ERROR = "Database operation error";

    public static final int DB_ENTITY_NOT_FOUND_CODE = 5101;
    public static final String DB_ENTITY_NOT_FOUND_MSG = "Entity not found!";

    public static final int DB_ENTITY_EXIST_CODE = 5102;
    public static final String DB_ENTITY_EXIST_MSG = "Entity has been exist";

    private ErrorCodeConstant(){}

}
