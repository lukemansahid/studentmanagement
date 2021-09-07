package com.lukish.studentmanagement.security.constant;

public class SecurityConstant {
    public static final long EXPIRATION_TIME = 431_000_000; // 5 days in milliseconds depends on your time
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String TOKEN_PROVIDER = "Lukeman S Kamara";
    public static final String TOKEN_FOR_ADMIN = "Administration Audience";
    public static final String AUTHORITIES = "authorities";
    public static final String FORBIDDEN_MESSAGE = "You need to login to access this page";
    public static final String ACCESS_DENIDED_MESSAGE = "You do not have permission to access this page";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String[] PUBLIC_URL = {"/user/login","/user/register","/user/resetpassword/**","/user/image/**"};
}
