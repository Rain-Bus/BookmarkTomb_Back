package com.cn.bookmarktomb.model.constant;

/**
 * @author fallen-angle
 * This is the regular expressions.
 */
public class RegularConstant {

    public static final String EMAIL_REGEXP = "^(?=.{9,40}$)([a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+)$";

    public static final String TELEPHONE_REGEXP = "^1([3456789])\\d{9}$";

    public static final String USERNAME_REGEXP = "^[a-zA-Z][a-zA-Z0-9_-]{5,19}$";

    public static final String PASSWORD_REGEXP = "^[A-Za-z0-9@_]{8,20}$";

    public static final String SEX_REGEXP = "^[F|M]$";

    public static final String AVATAR_URI_REGEXP = "^[a-z0-9]{20}$";

    public static final String QQ_REGEXP = "^[1-9][0-9]{4,10}$";

    public static final String WECHAT_REGEXP = "^[a-zA-Z][-_a-zA-Z0-9]{5,19}$";

    public static final String GITHUB_REGEXP = "^[a-zA-Z][-a-zA-Z0-9]{3,38}$";

    public static final String EMAIL_OR_TELEPHONE_REGEXP = "^1([3456789])\\d{9}$|^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    public static final String COLLECTION_NAME_REGEXP = "^[\\u4e00-\\u9fa5A-Za-z0-9@_]{1,40}$";

    public static final String COLLECTION_VISIT_STATUS_REGEXP = "^(private|team)$";

    public static final String ROLE_NAME_REGEXP = "^[a-zA-z]{3,20}";

    public static final String URL_REGEXP = "^((https?|ftp|file):\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})(\\S*)\\/?$";

    public static final String ORDER_REGEXP = "^(a|de)sc$";

    public static final String ACCORDING_REGEXP = "^(defaults|edit|create)$";

    private RegularConstant() {
    }
}
