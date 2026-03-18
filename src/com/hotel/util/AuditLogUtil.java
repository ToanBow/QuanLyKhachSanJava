package com.hotel.util;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditLogUtil {
    public static void log(String email, String action) {
        try {
            File folder = new File("logs");
            if (!folder.exists()) folder.mkdirs();

            try (PrintWriter out = new PrintWriter(new FileWriter("logs/audit_log.txt", true))) {
                String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                out.println(String.format("[%s] Người dùng: %s | Thao tác: %s", time, (email != null ? email : "UNKNOWN"), action));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}