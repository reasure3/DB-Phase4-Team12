package com.team12.auction.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BasketDAO extends BaseDao {

	public BasketDAO(Connection conn) {
		super(conn);
	}
	
	// 1) 학생의 장바구니가 없으면 생성 (basket_id = studentId)
    public void ensureBasketExists(long studentId) throws SQLException {
       String basketId = String.valueOf(studentId);
       String checkSql = "SELECT COUNT(*) FROM Basket WHERE basket_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            pstmt.setString(1, basketId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return; // 이미 있으니까 생성 안 함
                }
            }
        }

        // 없으면 생성
        String insertSql = "INSERT INTO Basket (basket_id, student_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setString(1, basketId);
            pstmt.setLong(2, studentId);
            pstmt.executeUpdate();
        }
    }

    // 2) 해당 분반이 이미 장바구니에 있는지 확인
    public boolean isSectionInBasket(long studentId, String sectionId) throws SQLException {
       String basketId = String.valueOf(studentId);
       String sql =
            "SELECT COUNT(*) " +
            "FROM BasketItem " +
            "WHERE basket_id = ? AND section_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, basketId);
            pstmt.setString(2, sectionId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // 3) 장바구니에 분반 추가
    public boolean addSectionToBasket(long studentId, String sectionId) throws SQLException {
       String basketId = String.valueOf(studentId);
       String sql =
            "INSERT INTO BasketItem " +
            "  (registration_time, status, processed_time, reason, basket_id, section_id) " +
            "VALUES (SYSDATE, 'PENDING', NULL, NULL, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, basketId);  // basket_id
            pstmt.setString(2, sectionId);
            pstmt.executeUpdate();
        }
        
       return processCapacityAndMaybeEnroll(studentId, basketId, sectionId);
    } 
    
    private boolean processCapacityAndMaybeEnroll(long studentId, String basketId, String sectionId) throws SQLException {

       // 1) 분반 정원 가져오기 (Section.capacity)
       int capacity = 0;
       String capSql = "SELECT capacity FROM Section WHERE section_id = ?";
       try (PreparedStatement pstmt = conn.prepareStatement(capSql)) {
          pstmt.setString(1, sectionId);
          try (ResultSet rs = pstmt.executeQuery()) {
             if (rs.next()) {
                capacity = rs.getInt(1);
             } else {
                // 존재하지 않는 분반이면 FAILED 처리
                String upd =
                   "UPDATE BasketItem " +
                   "SET status = 'FAILED', reason = '존재하지 않는 분반', processed_time = SYSDATE " +
                   "WHERE basket_id = ? AND section_id = ?";
                try (PreparedStatement u = conn.prepareStatement(upd)) {
                   u.setString(1, basketId);
                   u.setString(2, sectionId);
                   u.executeUpdate();
                }
                throw new SQLException("해당 SECTION_ID를 가진 분반이 없습니다: " + sectionId);
             }
          }
       }

       // 2) 현재 이 분반을 담고 있는 장바구니 인원 수 계산
       int count = 0;
       String cntSql =
          "SELECT COUNT(*) " +
          "FROM BasketItem " +
          "WHERE section_id = ?";
       try (PreparedStatement pstmt = conn.prepareStatement(cntSql)) {
          pstmt.setString(1, sectionId);
          try (ResultSet rs = pstmt.executeQuery()) {
             if (rs.next()) {
                count = rs.getInt(1);
             }
          }
       }

       // 3) 정원 초과 여부 판단
       if (count > capacity) {
          // 정원 초과 -> FAILED 처리
          String upd =
             "UPDATE BasketItem " +
             "SET status = 'FAILED', reason = '정원 초과', processed_time = SYSDATE " +
             "WHERE basket_id = ? AND section_id = ?";
          try (PreparedStatement pstmt = conn.prepareStatement(upd)) {
             pstmt.setString(1, basketId);
             pstmt.setString(2, sectionId);
             pstmt.executeUpdate();
          }
          return false;   // 정원 초과
       } else {
          // 정원 내 -> SUCCESS 처리 + ENROLLMENT에 INSERT
          String upd =
             "UPDATE BasketItem " +
             "SET status = 'SUCCESS', reason = NULL, processed_time = SYSDATE " +
             "WHERE basket_id = ? AND section_id = ?";
          try (PreparedStatement pstmt = conn.prepareStatement(upd)) {
             pstmt.setString(1, basketId);
             pstmt.setString(2, sectionId);
             pstmt.executeUpdate();
          }   

          String nextIdSql =
                 "SELECT NVL(MAX( " +
                 "         CASE " +
                 "           WHEN REGEXP_LIKE(enrollment_id, '^[0-9]+$') " +
                 "           THEN TO_NUMBER(enrollment_id) " +
                 "           ELSE NULL " +
                 "         END" +
                 "       ), 0) + 1 AS next_id " +
                 "FROM Enrollment";
      
          String nextEnrollmentId = null;
          try (PreparedStatement pstmt = conn.prepareStatement(nextIdSql);
               ResultSet rs = pstmt.executeQuery()) {
              if (rs.next()) {
                  long nextId = rs.getLong(1);        // 1, 2, 3, ...
                  nextEnrollmentId = String.valueOf(nextId);  // "1", "2", "3", ...
              } else {
                  nextEnrollmentId = "1";
              }
          }
      
      
      // ENROLLMENT 테이블에 등록 (수강신청)
      String enrSql =
         "INSERT INTO Enrollment " +
         "  (enrollment_id, enrollment_source, points_used, enrollment_time, student_id, section_id) " +
         "VALUES (?, 'FROM_BASKET', 0, SYSDATE, ?, ?)";
   
      try (PreparedStatement pstmt = conn.prepareStatement(enrSql)) {
         pstmt.setString(1, nextEnrollmentId);
         pstmt.setLong(2, studentId);   // NUMBER(10)
         pstmt.setString(3, sectionId); // VARCHAR2(14)
         pstmt.executeUpdate();
      }
   
      return true;    // 성공적으로 등록까지 완료
      }
    }
    
 // 로그인한 학생의 수강꾸러미 조회
    public void printMyBasket(long studentId) throws SQLException {

        String sql =
            "SELECT s.section_id, s.section_number, s.professor, " +
            "       s.capacity, s.classroom, s.course_id, c.course_name " +
            "FROM Basket b " +
            "JOIN BasketItem bi ON b.basket_id = bi.basket_id " +
            "JOIN Section s ON bi.section_id = s.section_id " +
            "JOIN Course c ON s.course_id = c.course_id " +
            "WHERE b.student_id = ? " +
            "ORDER BY s.section_id DESC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, studentId);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (!rs.isBeforeFirst()) { // 결과가 비었는지 체크
                    System.out.println("현재 수강꾸러미에 담긴 분반이 없습니다.");
                    return;
                }

                System.out.printf("%-14s %-6s %-15s %-8s %-10s %-10s %-25s\n",
                        "SECTION_ID", "SEC_NO", "PROFESSOR", "CAP", "CLASS", "COURSE_ID", "COURSE_NAME");
                System.out.println("----------------------------------------------------------------------------------------");

                while (rs.next()) {
                    String sectionId    = rs.getString(1);
                    int sectionNumber   = rs.getInt(2);
                    String professor    = rs.getString(3);
                    int capacity        = rs.getInt(4);
                    String classroom    = rs.getString(5);
                    String courseId     = rs.getString(6);
                    String courseName   = rs.getString(7);

                    System.out.printf("%-14s %-6d %-15s %-8d %-10s %-10s %-25s\n",
                            sectionId, sectionNumber, professor, capacity, classroom, courseId, courseName);
                }
            }
        }
    }
    
 // 학생의 basket_id 조회
    public String getBasketId(long studentId) throws SQLException {
        String sql = "SELECT basket_id FROM Basket WHERE student_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, studentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }
        return null; // 장바구니가 없다면 null
    }

 // 장바구니에서 분반 삭제 (basketId + sectionId 기준)
    public int deleteSectionFromBasket(String basketId, String sectionId) throws SQLException {
        if (basketId == null) return 0;

        String sql =
            "DELETE FROM BasketItem " +
            "WHERE basket_id = ? AND section_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, basketId);
            pstmt.setString(2, sectionId);
            return pstmt.executeUpdate();
        }
    }
}
