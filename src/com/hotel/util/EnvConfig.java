package com.hotel.util;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {

    private static final Dotenv dotenv = Dotenv.configure()
            .directory(".")     // đọc file .env ở thư mục gốc project
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load();

    public static String getEmailUser() {
        return dotenv.get("EMAIL_USER");
    }

    public static String getEmailPass() {
        return dotenv.get("EMAIL_PASS");
    }

    public static String getReportEmail() {
        return dotenv.get("REPORT_EMAIL");
    }
}