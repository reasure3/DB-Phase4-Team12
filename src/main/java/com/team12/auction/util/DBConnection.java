package com.team12.auction.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:orcl";
    private static final String USER = "course_registration";
    private static final String PASSWORD = "oracle";


    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("[OK] Oracle JDBC Driver Loaded!");
        } catch (ClassNotFoundException e) {
            System.out.println("[ERROR] Failed to load Oracle JDBC Driver");
            e.printStackTrace();
        }
    }

    /**
     * DB 연결 생성
     */
    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            conn.setAutoCommit(false);  // 수동 커밋 모드
            return conn;
        } catch (SQLException e) {
            System.out.println("[ERROR] Failed to connect to DB");
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 리소스 해제
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 커밋
     */
    public static void commit(Connection conn) {
        if (conn != null) {
            try {
                conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 롤백
     */
    public static void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
