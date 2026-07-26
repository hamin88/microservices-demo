package com.example.common.utils;

public final class SecurityConstants {

    private SecurityConstants() {
        throw new IllegalStateException("Utility class");
    }

    // =========================
    // JWT Claims
    // =========================

    public static final String CLAIM_SUB = "sub";
    public static final String CLAIM_USER_ID = "user_id";
    public static final String CLAIM_USERNAME = "preferred_username";
    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_ROLES = "roles";
    public static final String CLAIM_PERMISSIONS = "permissions";

    // =========================
    // HTTP Headers
    // =========================

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    public static final String USER_ID_HEADER = "X-Authenticated-User-Id";
    public static final String USERNAME_HEADER = "X-Authenticated-Username";
    public static final String TENANT_ID_HEADER = "X-Tenant-Id";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    // =========================
    // Roles
    // =========================

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    // =========================
    // Authorities
    // =========================

    public static final String USER_READ = "USER_READ";
    public static final String USER_WRITE = "USER_WRITE";
    public static final String USER_DELETE = "USER_DELETE";

    public static final String ORDER_READ = "ORDER_READ";
    public static final String ORDER_WRITE = "ORDER_WRITE";

    // =========================
    // Token Types
    // =========================

    public static final String TOKEN_TYPE = "Bearer";
}