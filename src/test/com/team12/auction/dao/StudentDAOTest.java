package com.team12.auction.dao;

import com.team12.auction.model.entity.Student;
import com.team12.auction.util.DBConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * StudentDAO 테스트 클래스
 * <p>
 * 현재 구조: 각 DAO 메서드가 내부에서 Connection을 생성하고 닫음
 * <p>
 * 실행 전 준비사항:
 * 1. DBConnection.java의 DB 연결 정보 확인
 * 2. Oracle DB 실행 확인
 * 3. Student 테이블 존재 확인
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentDAOTest {

    private StudentDAO studentDAO;

    // 테스트용 데이터
    private static final int TEST_STUDENT_ID = 99999999;
    private static final String TEST_PASSWORD = "test1234";
    private static final String TEST_NAME = "테스트학생";
    private static final String TEST_DEPARTMENT = "컴퓨터공학과";
    private static final int TEST_GRADE = 3;
    private static final int TEST_MAX_CREDITS = 18;
    private static final int TEST_MAX_POINTS = 90;

    @BeforeAll
    static void beforeAll() {
        System.out.println("====================================");
        System.out.println("   StudentDAO 테스트 시작");
        System.out.println("====================================");
    }

    @BeforeEach
    void setUp() {
        // StudentDAO는 Connection을 파라미터로 받지만 내부에서 생성하므로 null 전달
        studentDAO = new StudentDAO();
        System.out.println("\n--- 테스트 시작 ---");
    }

    @AfterEach
    void tearDown() {
        System.out.println("--- 테스트 데이터 정리 중... ---");
        cleanupTestData();
        System.out.println("--- 테스트 종료 ---\n");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("====================================");
        System.out.println("   모든 테스트 완료");
        System.out.println("====================================");
    }

    /**
     * 테스트 데이터 정리
     * 각 테스트 후 테스트용 학생 데이터를 삭제
     */
    private void cleanupTestData() {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM Student WHERE student_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, TEST_STUDENT_ID);

            int deleted = pstmt.executeUpdate();
            conn.commit();

            if (deleted > 0) {
                System.out.println("✓ 테스트 데이터 삭제 완료 (학번: " + TEST_STUDENT_ID + ")");
            }

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("✗ 데이터 정리 실패: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // ========================================
    // 회원가입 테스트
    // ========================================

    @Test
    @Order(1)
    @DisplayName("1. 회원가입 성공 테스트")
    void testSignUpSuccess() throws SQLException {
        System.out.println("테스트: 회원가입 성공");

        // Given: 새로운 학생 정보
        Student student = createTestStudent();

        // When: 회원가입 실행 (DAO 내부에서 Connection 생성/해제)
        int result = studentDAO.signUp(student);

        // Then: 1 반환 (성공)
        assertEquals(1, result, "회원가입 결과는 1이어야 함");
        System.out.println("  ✓ 회원가입 성공 (반환값: " + result + ")");

        // 추가 검증: DB에 실제로 저장되었는지 확인
        Student savedStudent = studentDAO.selectById(TEST_STUDENT_ID);
        assertNotNull(savedStudent, "저장된 학생을 조회할 수 있어야 함");
        assertEquals(TEST_NAME, savedStudent.getName(), "이름이 일치해야 함");
        assertEquals(TEST_DEPARTMENT, savedStudent.getDepartment(), "학과가 일치해야 함");
        assertEquals(TEST_GRADE, savedStudent.getGrade(), "학년이 일치해야 함");
        assertEquals(TEST_MAX_CREDITS, savedStudent.getMaxCredits(), "최대 학점 일치해야 함");
        assertEquals(TEST_MAX_POINTS, savedStudent.getMaxPoint(), "최대 포인트 일치해야 함");
        System.out.println("  ✓ DB 저장 확인 완료");
    }

    @Test
    @Order(2)
    @DisplayName("2. 중복 학번으로 회원가입 실패 테스트")
    void testSignUpDuplicate() throws SQLException {
        System.out.println("테스트: 중복 학번 회원가입 실패");

        // Given: 이미 존재하는 학생
        Student student1 = createTestStudent();
        studentDAO.signUp(student1);
        System.out.println("  ✓ 첫 번째 학생 등록 완료");

        // When & Then: 같은 학번으로 다시 회원가입 시도
        Student student2 = createTestStudent();
        student2.setName("다른이름");
        student2.setDepartment("다른학과");
        student2.setGrade(2);
        student2.setPassword("different");

        assertThrows(SQLException.class, () -> studentDAO.signUp(student2),
                "중복 학번으로 회원가입 시 SQLException이 발생해야 함");

        System.out.println("  ✓ 중복 학번 예외 발생 확인");
    }

    // ========================================
    // 학번 존재 여부 확인 테스트
    // ========================================

    @Test
    @Order(3)
    @DisplayName("3. 존재하는 학번 확인 테스트")
    void testExistsByIdTrue() throws SQLException {
        System.out.println("테스트: 존재하는 학번 확인");

        // Given: 학생 등록
        Student student = createTestStudent();
        studentDAO.signUp(student);
        System.out.println("  ✓ 테스트 학생 등록 완료");

        // When: 존재 여부 확인
        boolean exists = studentDAO.existsById(TEST_STUDENT_ID);

        // Then: true 반환
        assertTrue(exists, "등록된 학번은 존재해야 함");
        System.out.println("  ✓ 학번 존재 확인 (존재함)");
    }

    @Test
    @Order(4)
    @DisplayName("4. 존재하지 않는 학번 확인 테스트")
    void testExistsByIdFalse() throws SQLException {
        System.out.println("테스트: 존재하지 않는 학번 확인");

        // Given: 등록되지 않은 학번
        int nonExistentId = 11111111;

        // When: 존재 여부 확인
        boolean exists = studentDAO.existsById(nonExistentId);

        // Then: false 반환
        assertFalse(exists, "등록되지 않은 학번은 존재하지 않아야 함");
        System.out.println("  ✓ 학번 미존재 확인 (존재하지 않음)");
    }

    // ========================================
    // 로그인 테스트
    // ========================================

    @Test
    @Order(5)
    @DisplayName("5. 로그인 성공 테스트")
    void testLoginSuccess() throws SQLException {
        System.out.println("테스트: 로그인 성공");

        // Given: 학생 등록
        Student student = createTestStudent();
        studentDAO.signUp(student);
        System.out.println("  ✓ 테스트 학생 등록 완료");

        // When: 올바른 학번과 비밀번호로 로그인
        Student loginResult = studentDAO.login(TEST_STUDENT_ID, TEST_PASSWORD);

        // Then: Student 객체 반환 및 검증
        assertNotNull(loginResult, "로그인 성공 시 Student 객체를 반환해야 함");
        assertEquals(TEST_STUDENT_ID, loginResult.getStudentId(), "학번이 일치해야 함");
        assertEquals(TEST_NAME, loginResult.getName(), "이름이 일치해야 함");
        assertEquals(TEST_DEPARTMENT, loginResult.getDepartment(), "학과가 일치해야 함");
        assertEquals(TEST_GRADE, loginResult.getGrade(), "학년이 일치해야 함");
        assertEquals(18, loginResult.getMaxCredits(), "최대 학점은 18이어야 함");
        assertEquals(90, loginResult.getMaxPoint(), "최대 포인트는 90이어야 함");

        System.out.println("  ✓ 로그인 성공: " + loginResult.getName() + " (" + loginResult.getStudentId() + ")");
    }

    @Test
    @Order(6)
    @DisplayName("6. 잘못된 비밀번호로 로그인 실패 테스트")
    void testLoginWrongPassword() throws SQLException {
        System.out.println("테스트: 잘못된 비밀번호 로그인");

        // Given: 학생 등록
        Student student = createTestStudent();
        studentDAO.signUp(student);
        System.out.println("  ✓ 테스트 학생 등록 완료");

        // When: 잘못된 비밀번호로 로그인
        String wrongPassword = "wrongpassword123";
        Student loginResult = studentDAO.login(TEST_STUDENT_ID, wrongPassword);

        // Then: null 반환
        assertNull(loginResult, "잘못된 비밀번호로 로그인 시 null을 반환해야 함");
        System.out.println("  ✓ 로그인 실패 확인 (잘못된 비밀번호)");
    }

    @Test
    @Order(7)
    @DisplayName("7. 존재하지 않는 학번으로 로그인 실패 테스트")
    void testLoginNonExistentStudent() throws SQLException {
        System.out.println("테스트: 존재하지 않는 학번 로그인");

        // Given: 등록되지 않은 학번
        int nonExistentId = 11111111;

        // When: 존재하지 않는 학번으로 로그인
        Student loginResult = studentDAO.login(nonExistentId, TEST_PASSWORD);

        // Then: null 반환
        assertNull(loginResult, "존재하지 않는 학번으로 로그인 시 null을 반환해야 함");
        System.out.println("  ✓ 로그인 실패 확인 (존재하지 않는 학번)");
    }

    @Test
    @Order(8)
    @DisplayName("8. 빈 비밀번호로 로그인 실패 테스트")
    void testLoginEmptyPassword() throws SQLException {
        System.out.println("테스트: 빈 비밀번호 로그인");

        // Given: 학생 등록
        Student student = createTestStudent();
        studentDAO.signUp(student);
        System.out.println("  ✓ 테스트 학생 등록 완료");

        // When: 빈 비밀번호로 로그인
        Student loginResult = studentDAO.login(TEST_STUDENT_ID, "");

        // Then: null 반환
        assertNull(loginResult, "빈 비밀번호로 로그인 시 null을 반환해야 함");
        System.out.println("  ✓ 로그인 실패 확인 (빈 비밀번호)");
    }

    // ========================================
    // 학생 조회 테스트
    // ========================================

    @Test
    @Order(9)
    @DisplayName("9. 학번으로 학생 조회 성공 테스트")
    void testSelectByIdSuccess() throws SQLException {
        System.out.println("테스트: 학번으로 학생 조회 성공");

        // Given: 학생 등록
        Student student = createTestStudent();
        studentDAO.signUp(student);
        System.out.println("  ✓ 테스트 학생 등록 완료");

        // When: 학번으로 조회
        Student result = studentDAO.selectById(TEST_STUDENT_ID);

        // Then: Student 객체 반환 및 검증
        assertNotNull(result, "등록된 학생은 조회할 수 있어야 함");
        assertEquals(TEST_STUDENT_ID, result.getStudentId(), "학번이 일치해야 함");
        assertEquals(TEST_NAME, result.getName(), "이름이 일치해야 함");
        assertEquals(TEST_DEPARTMENT, result.getDepartment(), "학과가 일치해야 함");
        assertEquals(TEST_GRADE, result.getGrade(), "학년이 일치해야 함");

        System.out.println("  ✓ 학생 조회 성공: " + result.getName() + " (" + result.getStudentId() + ")");
    }

    @Test
    @Order(10)
    @DisplayName("10. 존재하지 않는 학번으로 학생 조회 실패 테스트")
    void testSelectByIdNotFound() throws SQLException {
        System.out.println("테스트: 존재하지 않는 학번 조회");

        // Given: 등록되지 않은 학번
        int nonExistentId = 11111111;

        // When: 학번으로 조회
        Student result = studentDAO.selectById(nonExistentId);

        // Then: null 반환
        assertNull(result, "등록되지 않은 학생은 null을 반환해야 함");
        System.out.println("  ✓ null 반환 확인 (학생 없음)");
    }

    // ========================================
    // 통합 시나리오 테스트
    // ========================================

    @Test
    @Order(11)
    @DisplayName("11. 전체 플로우 테스트 (회원가입 → 로그인 → 조회)")
    void testCompleteFlow() throws SQLException {
        System.out.println("테스트: 전체 플로우 (회원가입 → 로그인 → 조회)");

        // 1. 회원가입
        Student student = createTestStudent();
        int signUpResult = studentDAO.signUp(student);
        assertEquals(1, signUpResult, "회원가입 성공");
        System.out.println("  ✓ 1단계: 회원가입 성공");

        // 2. 로그인
        Student loginResult = studentDAO.login(TEST_STUDENT_ID, TEST_PASSWORD);
        assertNotNull(loginResult, "로그인 성공");
        assertEquals(TEST_NAME, loginResult.getName(), "로그인한 학생 정보 일치");
        System.out.println("  ✓ 2단계: 로그인 성공");

        // 3. 조회
        Student selectResult = studentDAO.selectById(TEST_STUDENT_ID);
        assertNotNull(selectResult, "조회 성공");
        assertEquals(TEST_NAME, selectResult.getName(), "조회한 학생 정보 일치");
        System.out.println("  ✓ 3단계: 학생 조회 성공");

        // 4. 존재 확인
        boolean exists = studentDAO.existsById(TEST_STUDENT_ID);
        assertTrue(exists, "학번 존재 확인");
        System.out.println("  ✓ 4단계: 학번 존재 확인");

        System.out.println("  ✓ 전체 플로우 테스트 성공!");
    }

    // ========================================
    // 헬퍼 메서드
    // ========================================

    /**
     * 테스트용 Student 객체 생성
     */
    private Student createTestStudent() {
        Student student = new Student();
        student.setStudentId(TEST_STUDENT_ID);
        student.setName(TEST_NAME);
        student.setDepartment(TEST_DEPARTMENT);
        student.setGrade(TEST_GRADE);
        student.setPassword(TEST_PASSWORD);
        student.setMaxCredits(TEST_MAX_CREDITS);
        student.setMaxPoint(TEST_MAX_POINTS);
        return student;
    }
}