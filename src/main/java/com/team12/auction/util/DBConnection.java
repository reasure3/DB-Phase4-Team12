package com.team12.auction.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DBConnection {

    private static final String DEFAULT_URL = "jdbc:oracle:thin:@localhost:1521:orcl";
    private static final String DEFAULT_USER = "course_registration";
    private static final String DEFAULT_PASSWORD = "oracle";

    private static String URL = DEFAULT_URL;
    private static String USER = DEFAULT_USER;
    private static String PASSWORD = DEFAULT_PASSWORD;

    static {
        try {
            System.out.println("[INFO] Loading Oracle JDBC Driver...");
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("[OK] Oracle JDBC Driver Loaded!");
        } catch (ClassNotFoundException e) {
            System.err.println("[ERROR] Failed to load Oracle JDBC Driver");
            System.err.println("build path에서 ojdbc11.jar 설정을 다시해주세요.");
            throw new InitializerException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.out.println("[INFO] Loading DB Properties...");
        	Properties props = new Properties();
            // 클래스패스에서 파일 읽기
            InputStream input = DBConnection.class.getClassLoader()
                .getResourceAsStream("db.properties");
            if (input == null) {
                System.err.println("db.properties 파일을 찾을 수 없습니다!");
                throw new InitializerException("db.properties not found");
            }

            props.load(input);
            URL = props.getProperty("db.url", DEFAULT_URL);
            USER = props.getProperty("db.user", DEFAULT_USER);
            PASSWORD = props.getProperty("db.password", DEFAULT_PASSWORD);

            input.close();
            System.out.println("[OK] DB Properties Loaded!");
        } catch (IOException e) {
            System.err.println("DB 설정 파일 로드 실패");
            throw new InitializerException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Connection 테스트
        Connection conn = getConnection();
        close(conn);
    }

    public static void init() {}

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

    /**
     * ResultSet, PreparedStatement, Connection 닫기
     */
    public static void close(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (pstmt != null) pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * PreparedStatement, Connection 닫기
     */
    public static void close(PreparedStatement pstmt, Connection conn) {
        close(null, pstmt, conn);
    }
}
