package com.team12.auction.servlet;

import com.team12.auction.dao.StudentDAO;
import com.team12.auction.model.entity.Student;
import com.team12.auction.servlet.util.HttpUtil;
import com.team12.auction.util.DBConnection;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SignUpServlet 통합 테스트
 * <p>
 * ⚠️ 실행 전 준비사항:
 * 1. 톰캣 서버를 먼저 시작해야 합니다!
 * 2. 프로젝트가 배포되어 있어야 합니다.
 * 3. BASE_URL을 본인 환경에 맞게 수정하세요.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SignUpServletIntegrationTest {
    private static final String BASE_URL = "http://localhost:8080/ProjectDBPhase4";
    private static final String SIGNUP_URL = BASE_URL + "/auth/signup";
    private static final String LOGIN_URL = BASE_URL + "/auth/login";

    // 테스트용 계정 정보
    private static final TestUser[] TEST_USERS = {
            new TestUser(98888888, "pass1234", "회원가입테스트1", "컴퓨터공학과", 1),
            new TestUser(98888887, "pass5678", "회원가입테스트2", "전자공학과", 2),
            new TestUser(98888886, "pass9999", "회원가입테스트3", "기계공학과", 3),
            new TestUser(98888885, "pass0000", "회원가입테스트4", "화학공학과", 4)
    };

    private HttpClient httpClient;
    private StudentDAO studentDAO;

    @BeforeAll
    static void beforeAll() throws SQLException {
        System.out.println("=========================================");
        System.out.println("  SignUpServlet 통합 테스트 시작");
        System.out.println("  서버 URL: " + BASE_URL);
        System.out.println("=========================================\n");

        // 모든 테스트 시작 전 테스트 데이터 정리
        System.out.println("--- 기존 테스트 데이터 정리 중... ---");
        cleanupAllTestData();
        System.out.println("✓ 기존 테스트 데이터 정리 완료\n");
    }

    @BeforeEach
    void setUp() {
        System.out.println("--- 테스트 환경 설정 ---");

        // HTTP 클라이언트 생성
        httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        // StudentDAO 생성
        studentDAO = new StudentDAO();

        System.out.println("✓ 테스트 환경 설정 완료\n");
    }

    @AfterEach
    void tearDown() throws SQLException {
        System.out.println("\n--- 테스트 정리 ---");

        // 테스트 계정 삭제
        cleanupAllTestData();

        System.out.println("✓ 테스트 정리 완료\n");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("=========================================");
        System.out.println("  SignUpServlet 통합 테스트 완료");
        System.out.println("=========================================");
    }

    // ========================================
    // 서버 연결 테스트
    // ========================================

    @Test
    @Order(1)
    @DisplayName("1. 회원가입 페이지 접근 테스트")
    void testSignUpPageAccess() throws IOException, InterruptedException {
        System.out.println("테스트: 회원가입 페이지 접근");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SIGNUP_URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();

        assertEquals(200, statusCode, "회원가입 페이지가 정상적으로 응답해야 함");

        String responseBody = response.body();
        assertTrue(responseBody.contains("회원가입") ||
                        responseBody.contains("signup"),
                "회원가입 페이지 내용 확인");

        System.out.println("  ✓ 회원가입 페이지 접근 성공 (상태 코드: " + statusCode + ")");
    }

    // ========================================
    // 회원가입 성공 테스트
    // ========================================

    @Test
    @Order(2)
    @DisplayName("2. 회원가입 성공 테스트")
    void testSignUpSuccess() throws IOException, InterruptedException, SQLException {
        System.out.println("테스트: 회원가입 성공");

        TestUser user = TEST_USERS[0];

        // Given: 회원가입 POST 요청 준비
        Map<String, String> formData = new HashMap<>();
        formData.put("studentId", String.valueOf(user.studentId));
        formData.put("name", user.name);
        formData.put("department", user.department);
        formData.put("grade", String.valueOf(user.grade));
        formData.put("password", user.password);
        formData.put("passwordConfirm", user.password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SIGNUP_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpUtil.buildFormDataFromMap(formData))
                .build();

        // When: 회원가입 요청
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Then: 응답 검증
        int statusCode = response.statusCode();
        assertEquals(200, statusCode, "회원가입 성공 시 200 응답");

        String responseBody = response.body();
        assertTrue(responseBody.contains("완료") ||
                        responseBody.contains("성공") ||
                        responseBody.contains("로그인"),
                "성공 메시지가 표시되어야 함");

        System.out.println("  ✓ 회원가입 요청 성공 (상태 코드: " + statusCode + ")");

        // DB에 실제로 저장되었는지 확인
        Student savedStudent = studentDAO.selectById(user.studentId);
        assertNotNull(savedStudent, "DB에 학생 정보가 저장되어야 함");
        assertEquals(user.name, savedStudent.getName(), "이름이 일치해야 함");
        assertEquals(user.department, savedStudent.getDepartment(), "학과가 일치해야 함");
        assertEquals(user.grade, savedStudent.getGrade(), "학년이 일치해야 함");

        System.out.println("  ✓ DB 저장 확인: " + user.name + " (" + user.studentId + ")");
    }

    @Test
    @Order(3)
    @DisplayName("3. 회원가입 후 바로 로그인 가능 테스트")
    void testSignUpThenLogin() throws IOException, InterruptedException {
        System.out.println("테스트: 회원가입 후 즉시 로그인");

        TestUser user = TEST_USERS[1];

        // 1. 회원가입
        Map<String, String> signupData = new HashMap<>();
        signupData.put("studentId", String.valueOf(user.studentId));
        signupData.put("name", user.name);
        signupData.put("department", user.department);
        signupData.put("grade", String.valueOf(user.grade));
        signupData.put("password", user.password);
        signupData.put("passwordConfirm", user.password);

        HttpRequest signupRequest = HttpRequest.newBuilder()
                .uri(URI.create(SIGNUP_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpUtil.buildFormDataFromMap(signupData))
                .build();

        HttpResponse<String> signupResponse = httpClient.send(signupRequest,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, signupResponse.statusCode(), "회원가입 성공");
        System.out.println("  ✓ 1단계: 회원가입 완료");

        // 2. 로그인 시도
        Map<String, String> loginData = new HashMap<>();
        loginData.put("studentId", String.valueOf(user.studentId));
        loginData.put("password", user.password);

        HttpRequest loginRequest = HttpRequest.newBuilder()
                .uri(URI.create(LOGIN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpUtil.buildFormDataFromMap(loginData))
                .build();

        HttpResponse<String> loginResponse = httpClient.send(loginRequest,
                HttpResponse.BodyHandlers.ofString());

        assertTrue(loginResponse.statusCode() == 302 ||
                        loginResponse.statusCode() == 200,
                "로그인 성공");
        System.out.println("  ✓ 2단계: 로그인 성공");
    }

    // ========================================
    // 회원가입 실패 테스트 - 중복 학번
    // ========================================

    @Test
    @Order(4)
    @DisplayName("4. 중복 학번으로 회원가입 실패 테스트")
    void testSignUpDuplicateStudentId() throws IOException, InterruptedException, SQLException {
        System.out.println("테스트: 중복 학번 회원가입 실패");

        TestUser user = TEST_USERS[2];

        // Given: 먼저 회원가입
        Map<String, String> firstSignup = new HashMap<>();
        firstSignup.put("studentId", String.valueOf(user.studentId));
        firstSignup.put("name", user.name);
        firstSignup.put("department", user.department);
        firstSignup.put("grade", String.valueOf(user.grade));
        firstSignup.put("password", user.password);
        firstSignup.put("passwordConfirm", user.password);

        HttpRequest firstRequest = HttpRequest.newBuilder()
                .uri(URI.create(SIGNUP_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpUtil.buildFormDataFromMap(firstSignup))
                .build();

        httpClient.send(firstRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("  ✓ 첫 번째 회원가입 완료");

        // When: 같은 학번으로 다시 회원가입 시도
        Map<String, String> secondSignup = new HashMap<>();
        secondSignup.put("studentId", String.valueOf(user.studentId));
        secondSignup.put("name", "다른이름");
        secondSignup.put("department", "다른학과");
        secondSignup.put("grade", "1");
        secondSignup.put("password", "differentpass");
        secondSignup.put("passwordConfirm", "differentpass");

        HttpRequest secondRequest = HttpRequest.newBuilder()
                .uri(URI.create(SIGNUP_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpUtil.buildFormDataFromMap(secondSignup))
                .build();

        HttpResponse<String> response = httpClient.send(secondRequest,
                HttpResponse.BodyHandlers.ofString());

        // Then: 회원가입 실패
        int statusCode = response.statusCode();
        assertEquals(200, statusCode, "중복 학번 시 200 응답");

        String responseBody = response.body();
        assertTrue(responseBody.contains("이미 등록된 학번입니다."), "중복 에러 메시지가 표시되어야 함");

        System.out.println("  ✓ 중복 학번 에러 확인");

        // DB에서 원래 정보가 유지되는지 확인
        Student student = studentDAO.selectById(user.studentId);
        assertEquals(user.name, student.getName(), "원래 이름이 유지되어야 함");
        System.out.println("  ✓ 원래 정보 유지 확인");
    }

    // ========================================
    // 회원가입 실패 테스트 - 비밀번호 불일치
    // ========================================

    @Test
    @Order(5)
    @DisplayName("5. 비밀번호 불일치로 회원가입 실패 테스트")
    void testSignUpPasswordMismatch() throws IOException, InterruptedException {
        System.out.println("테스트: 비밀번호 불일치");

        TestUser user = TEST_USERS[3];

        // Given: 비밀번호와 비밀번호 확인이 다른 경우
        Map<String, String> formData = new HashMap<>();
        formData.put("studentId", String.valueOf(user.studentId));
        formData.put("name", user.name);
        formData.put("department", user.department);
        formData.put("grade", String.valueOf(user.grade));
        formData.put("password", user.password);
        formData.put("passwordConfirm", "differentpassword");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SIGNUP_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpUtil.buildFormDataFromMap(formData))
                .build();

        // When: 회원가입 요청
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Then: 회원가입 실패
        int statusCode = response.statusCode();
        assertEquals(200, statusCode, "비밀번호 불일치 시 200 응답");

        String responseBody = response.body();
        assertTrue(responseBody.contains("비밀번호가 일치하지 않습니다"), "비밀번호 불일치 에러 메시지가 표시되어야 함");

        System.out.println("  ✓ 비밀번호 불일치 에러 확인");
    }

    // ========================================
    // 회원가입 실패 테스트 - 필수 필드 누락
    // ========================================

    @Test
    @Order(6)
    @DisplayName("6. 필수 필드 누락으로 회원가입 실패 테스트 - 이름")
    void testSignUpMissingName() throws IOException, InterruptedException {
        System.out.println("테스트: 이름 누락");

        TestUser user = TEST_USERS[0];

        // Given: 이름 없이 회원가입
        Map<String, String> formData = new HashMap<>();
        formData.put("studentId", String.valueOf(user.studentId));
        formData.put("name", "");  // 이름 비어있음
        formData.put("department", user.department);
        formData.put("grade", String.valueOf(user.grade));
        formData.put("password", user.password);
        formData.put("passwordConfirm", user.password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SIGNUP_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpUtil.buildFormDataFromMap(formData))
                .build();

        // When: 회원가입 요청
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Then: 회원가입 실패
        int statusCode = response.statusCode();
        assertEquals(200, statusCode, "필수 필드 누락 시 200 응답");

        String responseBody = response.body();
        assertTrue(responseBody.contains("모든 필드를 입력해주세요"), "필수 필드 에러 메시지가 표시되어야 함");

        System.out.println("  ✓ 필수 필드 누락 에러 확인");
    }

    @Test
    @Order(7)
    @DisplayName("7. 필수 필드 누락으로 회원가입 실패 테스트 - 학번")
    void testSignUpMissingStudentId() throws IOException, InterruptedException {
        System.out.println("테스트: 학번 누락");

        TestUser user = TEST_USERS[0];

        // Given: 학번 없이 회원가입
        Map<String, String> formData = new HashMap<>();
        formData.put("studentId", "");  // 학번 비어있음
        formData.put("name", user.name);
        formData.put("department", user.department);
        formData.put("grade", String.valueOf(user.grade));
        formData.put("password", user.password);
        formData.put("passwordConfirm", user.password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SIGNUP_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpUtil.buildFormDataFromMap(formData))
                .build();

        // When: 회원가입 요청
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Then: 회원가입 실패
        int statusCode = response.statusCode();
        assertEquals(200, statusCode, "필수 필드 누락 시 200 응답");

        String responseBody = response.body();
        assertTrue(responseBody.contains("모든 필드를 입력해주세요"), "필수 필드 에러 메시지가 표시되어야 함");

        System.out.println("  ✓ 필수 필드 누락 에러 확인");
    }

    // ========================================
    // 회원가입 실패 테스트 - 잘못된 학년
    // ========================================

    @Test
    @Order(8)
    @DisplayName("8. 잘못된 학년으로 회원가입 실패 테스트")
    void testSignUpInvalidGrade() throws IOException, InterruptedException {
        System.out.println("테스트: 잘못된 학년 (5학년)");

        TestUser user = TEST_USERS[0];

        // Given: 5학년으로 회원가입 (유효하지 않음)
        Map<String, String> formData = new HashMap<>();
        formData.put("studentId", String.valueOf(user.studentId));
        formData.put("name", user.name);
        formData.put("department", user.department);
        formData.put("grade", "5");  // 잘못된 학년
        formData.put("password", user.password);
        formData.put("passwordConfirm", user.password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SIGNUP_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpUtil.buildFormDataFromMap(formData))
                .build();

        // When: 회원가입 요청
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Then: 회원가입 실패
        int statusCode = response.statusCode();
        assertEquals(200, statusCode, "잘못된 학년 시 200 응답");

        String responseBody = response.body();
        assertTrue(responseBody.contains("학년은 1~4 사이의 숫자여야 합니다"), "학년 유효성 에러 메시지가 표시되어야 함");

        System.out.println("  ✓ 잘못된 학년 에러 확인");
    }

    // ========================================
    // 여러 계정 연속 회원가입 테스트
    // ========================================

    @Test
    @Order(9)
    @DisplayName("9. 여러 계정 연속 회원가입 테스트")
    void testMultipleSignUps() throws IOException, InterruptedException, SQLException {
        System.out.println("테스트: 여러 계정 연속 회원가입");

        for (int i = 0; i < TEST_USERS.length; i++) {
            TestUser user = TEST_USERS[i];
            System.out.println("  " + (i + 1) + "번째 계정: " + user.name);

            // 회원가입
            Map<String, String> formData = new HashMap<>();
            formData.put("studentId", String.valueOf(user.studentId));
            formData.put("name", user.name);
            formData.put("department", user.department);
            formData.put("grade", String.valueOf(user.grade));
            formData.put("password", user.password);
            formData.put("passwordConfirm", user.password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SIGNUP_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpUtil.buildFormDataFromMap(formData))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode(),
                    user.name + " 회원가입 성공");

            // DB 확인
            Student student = studentDAO.selectById(user.studentId);
            assertNotNull(student, user.name + " DB 저장 확인");

            System.out.println("    ✓ " + user.name + " 회원가입 완료");
        }

        System.out.println("  ✓ 총 " + TEST_USERS.length + "개 계정 회원가입 성공");
    }

    // ========================================
    // 통합 시나리오 테스트
    // ========================================

    @Test
    @Order(10)
    @DisplayName("10. 전체 플로우 테스트 (회원가입 → 로그인 → 성공)")
    void testCompleteSignUpFlow() throws IOException, InterruptedException {
        System.out.println("테스트: 전체 플로우 (회원가입 → 로그인)");

        TestUser user = TEST_USERS[0];

        // 1. 회원가입
        Map<String, String> signupData = new HashMap<>();
        signupData.put("studentId", String.valueOf(user.studentId));
        signupData.put("name", user.name);
        signupData.put("department", user.department);
        signupData.put("grade", String.valueOf(user.grade));
        signupData.put("password", user.password);
        signupData.put("passwordConfirm", user.password);

        HttpRequest signupRequest = HttpRequest.newBuilder()
                .uri(URI.create(SIGNUP_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpUtil.buildFormDataFromMap(signupData))
                .build();

        HttpResponse<String> signupResponse = httpClient.send(signupRequest,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, signupResponse.statusCode(), "회원가입 성공");
        System.out.println("  ✓ 1단계: 회원가입 완료");

        // 2. 로그인
        Map<String, String> loginData = new HashMap<>();
        loginData.put("studentId", String.valueOf(user.studentId));
        loginData.put("password", user.password);

        HttpRequest loginRequest = HttpRequest.newBuilder()
                .uri(URI.create(LOGIN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpUtil.buildFormDataFromMap(loginData))
                .build();

        HttpResponse<String> loginResponse = httpClient.send(loginRequest,
                HttpResponse.BodyHandlers.ofString());
        assertTrue(loginResponse.statusCode() == 302 ||
                        loginResponse.statusCode() == 200,
                "로그인 성공");
        System.out.println("  ✓ 2단계: 로그인 성공");

        System.out.println("  ✓ 전체 플로우 테스트 성공!");
    }

    // ========================================
    // 헬퍼 메서드
    // ========================================

    /**
     * 모든 테스트 데이터 정리
     */
    private static void cleanupAllTestData() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();

            // 테스트 학번 범위 일괄 삭제 (98888880 ~ 98888889)
            String sql = "DELETE FROM Student WHERE student_id BETWEEN 98888880 AND 98888889";
            pstmt = conn.prepareStatement(sql);
            int deleted = pstmt.executeUpdate();

            conn.commit();

            if (deleted > 0) {
                System.out.println("✓ 테스트 계정 " + deleted + "개 삭제 완료");
            }
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    // ========================================
    // 테스트 사용자 클래스
    // ========================================

    /**
     * 테스트용 사용자 정보를 담는 내부 클래스
     */
    private record TestUser(int studentId, String password, String name, String department, int grade) {}
}