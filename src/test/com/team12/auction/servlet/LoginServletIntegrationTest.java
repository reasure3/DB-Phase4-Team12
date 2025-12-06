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
 * LoginServlet 통합 테스트 (테스트 데이터 자동 생성 포함)
 * <p>
 * ⚠️ 실행 전 준비사항:
 * 1. 톰캣 서버를 먼저 시작해야 합니다!
 * 2. 프로젝트가 배포되어 있어야 합니다.
 * 3. BASE_URL을 본인 환경에 맞게 수정하세요.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoginServletIntegrationTest {
    private static final String BASE_URL = "http://localhost:8080/ProjectDBPhase4";
    private static final String LOGIN_URL = BASE_URL + "/auth/login";
    private static final String LOGOUT_URL = BASE_URL + "/auth/logout";
    private static final String MAIN_URL = BASE_URL + "/main.jsp";

    // 테스트용 계정 정보 (여러 개)
    private static final TestUser[] TEST_USERS = {
            new TestUser(99999999, "test1234", "테스트학생1", "컴퓨터공학과", 3),
            new TestUser(99999998, "test5678", "테스트학생2", "전자공학과", 2),
            new TestUser(99999997, "test9999", "테스트학생3", "기계공학과", 4)
    };

    private HttpClient httpClient;
    private StudentDAO studentDAO;
    private String sessionCookie;

    @BeforeAll
    static void beforeAll() throws SQLException {
        System.out.println("=========================================");
        System.out.println("  LoginServlet 통합 테스트 시작");
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

        // 세션 쿠키 초기화
        sessionCookie = null;

        // 테스트 계정들 생성
        createAllTestAccounts();

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
        System.out.println("  LoginServlet 통합 테스트 완료");
        System.out.println("=========================================");
    }

    // ========================================
    // 서버 연결 테스트
    // ========================================

    @Test
    @Order(1)
    @DisplayName("1. 서버 연결 확인 테스트")
    void testServerConnection() throws IOException, InterruptedException {
        System.out.println("테스트: 서버 연결 확인");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LOGIN_URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();

        assertEquals(200, statusCode, "서버가 정상적으로 응답해야 함");
        System.out.println("  ✓ 서버 연결 성공 (상태 코드: " + statusCode + ")");
    }

    // ========================================
    // 테스트 데이터 생성 확인
    // ========================================

    @Test
    @Order(2)
    @DisplayName("2. 테스트 데이터 생성 확인")
    void testTestDataCreation() throws SQLException {
        System.out.println("테스트: 테스트 데이터 생성 확인");

        for (TestUser user : TEST_USERS) {
            Student student = studentDAO.selectById(user.studentId);

            assertNotNull(student, "테스트 계정이 생성되어 있어야 함: " + user.name);
            assertEquals(user.name, student.getName(), "이름이 일치해야 함");
            assertEquals(user.department, student.getDepartment(), "학과가 일치해야 함");
            assertEquals(user.grade, student.getGrade(), "학년이 일치해야 함");

            System.out.println("  ✓ " + user.name + " (" + user.studentId + ") 생성 확인");
        }
    }

    // ========================================
    // 로그인 성공 테스트
    // ========================================

    @Test
    @Order(3)
    @DisplayName("3. 로그인 성공 테스트 (계정1)")
    void testLoginSuccess() throws IOException, InterruptedException {
        System.out.println("테스트: 로그인 성공 - " + TEST_USERS[0].name);

        TestUser user = TEST_USERS[0];

        // Given: 로그인 POST 요청 준비
        Map<String, String> formData = new HashMap<>();
        formData.put("studentId", String.valueOf(user.studentId));
        formData.put("password", user.password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LOGIN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpUtil.buildFormDataFromMap(formData))
                .build();

        // When: 로그인 요청
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // 세션 쿠키 저장
        HttpUtil.getCookieFromResponse(response).ifPresent(cookie -> sessionCookie = cookie);

        // Then: 응답 검증
        int statusCode = response.statusCode();

        assertTrue(statusCode == 302 || statusCode == 200,
                "로그인 성공 시 리다이렉트 또는 성공 응답 (실제: " + statusCode + ")");

        System.out.println("  ✓ 로그인 요청 성공 (상태 코드: " + statusCode + ")");

        // main.jsp 접근 가능 여부 확인
        HttpRequest mainRequest = HttpRequest.newBuilder()
                .uri(URI.create(MAIN_URL))
                .header("Cookie", sessionCookie)
                .GET()
                .build();

        HttpResponse<String> mainResponse = httpClient.send(mainRequest,
                HttpResponse.BodyHandlers.ofString());

        int mainPageStatus = mainResponse.statusCode();

        assertEquals(200, mainPageStatus, "로그인 후 main.jsp 접근 가능해야 함");

        // 응답 본문에 학생 이름이 포함되어 있는지 확인
        String responseBody = mainResponse.body();
        assertTrue(responseBody.contains(user.name),
                "main.jsp에 학생 이름이 표시되어야 함");

        System.out.println("  ✓ main.jsp 접근 성공");
        System.out.println("  ✓ 학생 이름 확인: " + user.name);
    }

    // ========================================
    // 여러 계정 로그인 테스트
    // ========================================

    @Test
    @Order(4)
    @DisplayName("4. 여러 계정으로 순차 로그인 테스트")
    void testMultipleAccountsLogin() throws IOException, InterruptedException {
        System.out.println("테스트: 여러 계정 순차 로그인");

        for (int i = 0; i < TEST_USERS.length; i++) {
            TestUser user = TEST_USERS[i];
            System.out.println("  " + (i + 1) + "번째 계정: " + user.name);

            // 로그인
            Map<String, String> formData = new HashMap<>();
            formData.put("studentId", String.valueOf(user.studentId));
            formData.put("password", user.password);

            HttpRequest loginRequest = HttpRequest.newBuilder()
                    .uri(URI.create(LOGIN_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpUtil.buildFormDataFromMap(formData))
                    .build();

            HttpResponse<String> loginResponse = httpClient.send(loginRequest,
                    HttpResponse.BodyHandlers.ofString());
            HttpUtil.getCookieFromResponse(loginResponse).ifPresent(cookie -> sessionCookie = cookie);

            // main.jsp 접근
            HttpRequest mainRequest = HttpRequest.newBuilder()
                    .uri(URI.create(MAIN_URL))
                    .header("Cookie", sessionCookie)
                    .GET()
                    .build();

            HttpResponse<String> mainResponse = httpClient.send(mainRequest,
                    HttpResponse.BodyHandlers.ofString());

            assertEquals(200, mainResponse.statusCode(),
                    user.name + " 로그인 후 main.jsp 접근 가능");
            assertTrue(mainResponse.body().contains(user.name),
                    user.name + "이 표시되어야 함");

            System.out.println("    ✓ " + user.name + " 로그인 성공");

            // 로그아웃
            HttpRequest logoutRequest = HttpRequest.newBuilder()
                    .uri(URI.create(LOGOUT_URL))
                    .header("Cookie", sessionCookie)
                    .GET()
                    .build();
            httpClient.send(logoutRequest, HttpResponse.BodyHandlers.ofString());

            System.out.println("    ✓ " + user.name + " 로그아웃 완료");
        }

        System.out.println("  ✓ 모든 계정 로그인/로그아웃 성공");
    }

    // ========================================
    // 로그인 실패 테스트
    // ========================================

    @Test
    @Order(5)
    @DisplayName("5. 잘못된 비밀번호로 로그인 실패 테스트")
    void testLoginWrongPassword() throws IOException, InterruptedException {
        System.out.println("테스트: 잘못된 비밀번호 로그인");

        TestUser user = TEST_USERS[0];

        // Given: 잘못된 비밀번호로 로그인 요청
        Map<String, String> formData = new HashMap<>();
        formData.put("studentId", String.valueOf(user.studentId));
        formData.put("password", "wrongpassword");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LOGIN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpUtil.buildFormDataFromMap(formData))
                .build();

        // When: 로그인 요청
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Then: 로그인 페이지로 다시 이동 (200)
        int statusCode = response.statusCode();
        assertEquals(200, statusCode, "로그인 실패 시 200 응답");

        // 에러 메시지 확인
        String responseBody = response.body();
        assertTrue(responseBody.contains("올바르지 않습니다"), "에러 메시지가 표시되어야 함");

        System.out.println("  ✓ 로그인 실패 확인");
        System.out.println("  ✓ 에러 메시지 표시 확인");
    }

    @Test
    @Order(6)
    @DisplayName("6. 존재하지 않는 학번으로 로그인 실패 테스트")
    void testLoginNonExistentStudent() throws IOException, InterruptedException {
        System.out.println("테스트: 존재하지 않는 학번 로그인");

        // Given: 존재하지 않는 학번으로 로그인 요청
        Map<String, String> formData = new HashMap<>();
        formData.put("studentId", "11111111");
        formData.put("password", "anypassword");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LOGIN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpUtil.buildFormDataFromMap(formData))
                .build();

        // When: 로그인 요청
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Then: 로그인 실패
        int statusCode = response.statusCode();
        assertEquals(200, statusCode, "로그인 실패 시 200 응답");

        String responseBody = response.body();
        assertTrue(responseBody.contains("올바르지 않습니다"), "에러 메시지가 표시되어야 함");

        System.out.println("  ✓ 로그인 실패 확인");
    }

    @Test
    @Order(7)
    @DisplayName("7. 빈 값으로 로그인 실패 테스트")
    void testLoginEmptyFields() throws IOException, InterruptedException {
        System.out.println("테스트: 빈 값 로그인");

        // Given: 빈 값으로 로그인 요청
        Map<String, String> formData = new HashMap<>();
        formData.put("studentId", "");
        formData.put("password", "");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LOGIN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpUtil.buildFormDataFromMap(formData))
                .build();

        // When: 로그인 요청
        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Then: 로그인 실패
        int statusCode = response.statusCode();
        assertEquals(200, statusCode, "로그인 실패 시 200 응답");

        String responseBody = response.body();
        assertTrue(responseBody.contains("학번과 비밀번호를 모두 입력해주세요"), "에러 메시지가 표시되어야 함");

        System.out.println("  ✓ 빈 값 검증 확인");
    }

    // ========================================
    // 로그아웃 테스트
    // ========================================

    @Test
    @Order(8)
    @DisplayName("8. 로그아웃 테스트")
    void testLogout() throws IOException, InterruptedException {
        System.out.println("테스트: 로그아웃");

        TestUser user = TEST_USERS[0];

        // Given: 먼저 로그인
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
        HttpUtil.getCookieFromResponse(loginResponse).ifPresent(cookie -> sessionCookie = cookie);
        System.out.println("  ✓ 로그인 완료");

        // When: 로그아웃 요청
        HttpRequest logoutRequest = HttpRequest.newBuilder()
                .uri(URI.create(LOGOUT_URL))
                .header("Cookie", sessionCookie)
                .GET()
                .build();

        HttpResponse<String> logoutResponse = httpClient.send(logoutRequest,
                HttpResponse.BodyHandlers.ofString());

        // Then: 로그인 페이지로 리다이렉트
        int statusCode = logoutResponse.statusCode();
        assertTrue(statusCode == 302 || statusCode == 200,
                "로그아웃 후 리다이렉트 또는 성공 응답");
        System.out.println("  ✓ 로그아웃 요청 성공 (상태 코드: " + statusCode + ")");

        System.out.println("  ✓ 로그아웃 완료");
    }

    // ========================================
    // 세션 유지 테스트
    // ========================================

    @Test
    @Order(9)
    @DisplayName("9. 세션 유지 테스트")
    void testSessionPersistence() throws IOException, InterruptedException {
        System.out.println("테스트: 세션 유지");

        TestUser user = TEST_USERS[0];

        // Given: 로그인
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
        HttpUtil.getCookieFromResponse(loginResponse).ifPresent(cookie -> sessionCookie = cookie);
        System.out.println("  ✓ 로그인 완료");

        // When: 여러 번 페이지 접근
        for (int i = 1; i <= 3; i++) {
            Thread.sleep(500); // 0.5초 대기

            HttpRequest mainRequest = HttpRequest.newBuilder()
                    .uri(URI.create(MAIN_URL))
                    .header("Cookie", sessionCookie)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(mainRequest,
                    HttpResponse.BodyHandlers.ofString());

            // Then: 계속 접근 가능해야 함
            assertEquals(200, response.statusCode(),
                    "세션이 유지되어 main.jsp 접근 가능해야 함");

            String responseBody = response.body();
            assertTrue(responseBody.contains(user.name),
                    "세션 정보가 유지되어야 함");

            System.out.println("  ✓ " + i + "번째 접근 성공 (세션 유지)");
        }
    }

    // ========================================
    // 통합 시나리오 테스트
    // ========================================

    @Test
    @Order(10)
    @DisplayName("10. 전체 플로우 테스트 (로그인 → 페이지 이동 → 로그아웃)")
    void testCompleteFlow() throws IOException, InterruptedException {
        System.out.println("테스트: 전체 플로우");

        TestUser user = TEST_USERS[1]; // 두 번째 계정 사용

        // 1. 로그인
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
        HttpUtil.getCookieFromResponse(loginResponse).ifPresent(cookie -> sessionCookie = cookie);

        assertTrue(loginResponse.statusCode() == 302 ||
                        loginResponse.statusCode() == 200,
                "로그인 성공");
        System.out.println("  ✓ 1단계: 로그인 성공 (" + user.name + ")");

        // 2. main.jsp 접근
        HttpRequest mainRequest = HttpRequest.newBuilder()
                .uri(URI.create(MAIN_URL))
                .header("Cookie", sessionCookie)
                .GET()
                .build();

        HttpResponse<String> mainResponse = httpClient.send(mainRequest,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, mainResponse.statusCode(), "main.jsp 접근 가능");
        assertTrue(mainResponse.body().contains(user.name),
                "사용자 이름 표시됨");
        System.out.println("  ✓ 2단계: main.jsp 접근 성공");

        // 3. 로그아웃
        HttpRequest logoutRequest = HttpRequest.newBuilder()
                .uri(URI.create(LOGOUT_URL))
                .header("Cookie", sessionCookie)
                .GET()
                .build();

        HttpResponse<String> logoutResponse = httpClient.send(logoutRequest,
                HttpResponse.BodyHandlers.ofString());
        assertTrue(logoutResponse.statusCode() == 302 ||
                        logoutResponse.statusCode() == 200,
                "로그아웃 성공");
        System.out.println("  ✓ 3단계: 로그아웃 성공");

        System.out.println("  ✓ 전체 플로우 테스트 성공!");
    }

    // ========================================
// 로그인 실패 후 접근 제어 테스트
// ========================================

    @Test
    @Order(11)
    @DisplayName("11. 로그인 실패 후 main.jsp 접근 불가 테스트")
    void testAccessMainWithoutLogin() throws IOException, InterruptedException {
        System.out.println("테스트: 로그인 없이 main.jsp 접근 시도");

        // Given: 로그인하지 않은 상태

        // When: main.jsp 직접 접근 시도
        HttpRequest mainRequest = HttpRequest.newBuilder()
                .uri(URI.create(MAIN_URL))
                .GET()
                .build();

        HttpResponse<String> mainResponse = httpClient.send(mainRequest,
                HttpResponse.BodyHandlers.ofString());

        // Then: 로그인 페이지로 리다이렉트 또는 접근 차단
        int statusCode = mainResponse.statusCode();

        // 리다이렉트(302) 또는 로그인 페이지로 포워드(200)
        assertTrue(statusCode == 302 || statusCode == 200,
                "로그인 없이는 main.jsp 접근 시 리다이렉트 또는 포워드");

        String responseBody = mainResponse.body();

        // main.jsp의 내용이 아닌 login.jsp의 내용이어야 함
        assertFalse(responseBody.contains("경매 목록") ||
                        responseBody.contains("수강꾸러미"),
                "main.jsp 내용이 표시되면 안됨");

        System.out.println("  ✓ 로그인 없이 main.jsp 접근 차단 확인 (상태 코드: " + statusCode + ")");
        System.out.println("  ✓ 로그인 페이지로 이동 확인");
    }

    @Test
    @Order(12)
    @DisplayName("12. 잘못된 비밀번호 로그인 실패 후 main.jsp 접근 불가 테스트")
    void testAccessMainAfterFailedLogin() throws IOException, InterruptedException {
        System.out.println("테스트: 로그인 실패 후 main.jsp 접근 시도");

        TestUser user = TEST_USERS[0];

        // Given: 잘못된 비밀번호로 로그인 시도
        Map<String, String> loginData = new HashMap<>();
        loginData.put("studentId", String.valueOf(user.studentId));
        loginData.put("password", "wrongpassword");

        HttpRequest loginRequest = HttpRequest.newBuilder()
                .uri(URI.create(LOGIN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpUtil.buildFormDataFromMap(loginData))
                .build();

        HttpResponse<String> loginResponse = httpClient.send(loginRequest,
                HttpResponse.BodyHandlers.ofString());

        // 로그인 실패 시 쿠키가 있을 수도 있으니 저장 시도
        HttpUtil.getCookieFromResponse(loginResponse).ifPresent(cookie -> sessionCookie = cookie);

        assertEquals(200, loginResponse.statusCode(), "로그인 실패");
        System.out.println("  ✓ 로그인 실패 확인");

        // When: main.jsp 접근 시도
        HttpRequest.Builder mainRequestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(MAIN_URL))
                .GET();

        // 쿠키가 있으면 포함
        if (sessionCookie != null) {
            mainRequestBuilder.header("Cookie", sessionCookie);
        }

        HttpRequest mainRequest = mainRequestBuilder.build();
        HttpResponse<String> mainResponse = httpClient.send(mainRequest,
                HttpResponse.BodyHandlers.ofString());

        // Then: main.jsp 접근 불가
        String responseBody = mainResponse.body();

        // main.jsp의 실제 내용이 아닌 로그인 페이지여야 함
        assertFalse(responseBody.contains(user.name),
                "로그인 실패 후에는 사용자 이름이 표시되면 안됨");

        assertFalse(responseBody.contains("경매 목록") ||
                        responseBody.contains("수강꾸러미"),
                "로그인 실패 후에는 main.jsp 내용이 표시되면 안됨");

        System.out.println("  ✓ 로그인 실패 후 main.jsp 접근 차단 확인");
        System.out.println("  ✓ 로그인 페이지로 이동 확인");
    }

    @Test
    @Order(13)
    @DisplayName("13. 존재하지 않는 학번 로그인 실패 후 main.jsp 접근 불가 테스트")
    void testAccessMainAfterNonExistentLogin() throws IOException, InterruptedException {
        System.out.println("테스트: 존재하지 않는 학번 로그인 실패 후 main.jsp 접근");

        // Given: 존재하지 않는 학번으로 로그인 시도
        Map<String, String> loginData = new HashMap<>();
        loginData.put("studentId", "11111111");
        loginData.put("password", "anypassword");

        HttpRequest loginRequest = HttpRequest.newBuilder()
                .uri(URI.create(LOGIN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpUtil.buildFormDataFromMap(loginData))
                .build();

        HttpResponse<String> loginResponse = httpClient.send(loginRequest,
                HttpResponse.BodyHandlers.ofString());
        HttpUtil.getCookieFromResponse(loginResponse).ifPresent(cookie -> sessionCookie = cookie);

        assertEquals(200, loginResponse.statusCode(), "로그인 실패");
        System.out.println("  ✓ 로그인 실패 확인");

        // When: main.jsp 접근 시도
        HttpRequest.Builder mainRequestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(MAIN_URL))
                .GET();

        if (sessionCookie != null) {
            mainRequestBuilder.header("Cookie", sessionCookie);
        }

        HttpRequest mainRequest = mainRequestBuilder.build();
        HttpResponse<String> mainResponse = httpClient.send(mainRequest,
                HttpResponse.BodyHandlers.ofString());

        // Then: main.jsp 접근 불가
        String responseBody = mainResponse.body();

        assertFalse(responseBody.contains("경매 목록") ||
                        responseBody.contains("수강꾸러미"),
                "main.jsp 내용이 표시되면 안됨");

        System.out.println("  ✓ main.jsp 접근 차단 확인");
    }

// ========================================
// 로그아웃 후 접근 제어 테스트
// ========================================

    @Test
    @Order(14)
    @DisplayName("14. 로그아웃 후 main.jsp 접근 불가 테스트")
    void testAccessMainAfterLogout() throws IOException, InterruptedException {
        System.out.println("테스트: 로그아웃 후 main.jsp 접근 시도");

        TestUser user = TEST_USERS[0];

        // Given: 로그인
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
        HttpUtil.getCookieFromResponse(loginResponse).ifPresent(cookie -> sessionCookie = cookie);

        assertTrue(loginResponse.statusCode() == 302 ||
                        loginResponse.statusCode() == 200,
                "로그인 성공");
        System.out.println("  ✓ 로그인 완료");

        // 로그인 후 main.jsp 접근 가능 확인
        HttpRequest mainRequest1 = HttpRequest.newBuilder()
                .uri(URI.create(MAIN_URL))
                .header("Cookie", sessionCookie)
                .GET()
                .build();

        HttpResponse<String> mainResponse1 = httpClient.send(mainRequest1,
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, mainResponse1.statusCode(), "로그인 후 main.jsp 접근 가능");
        assertTrue(mainResponse1.body().contains(user.name),
                "사용자 이름이 표시됨");
        System.out.println("  ✓ 로그인 후 main.jsp 접근 가능 확인");

        // When: 로그아웃
        HttpRequest logoutRequest = HttpRequest.newBuilder()
                .uri(URI.create(LOGOUT_URL))
                .header("Cookie", sessionCookie)
                .GET()
                .build();

        HttpResponse<String> logoutResponse = httpClient.send(logoutRequest,
                HttpResponse.BodyHandlers.ofString());

        assertTrue(logoutResponse.statusCode() == 302 ||
                        logoutResponse.statusCode() == 200,
                "로그아웃 성공");
        System.out.println("  ✓ 로그아웃 완료");

        // Then: 로그아웃 후 main.jsp 접근 불가
        HttpRequest mainRequest2 = HttpRequest.newBuilder()
                .uri(URI.create(MAIN_URL))
                .header("Cookie", sessionCookie)
                .GET()
                .build();

        HttpResponse<String> mainResponse2 = httpClient.send(mainRequest2,
                HttpResponse.BodyHandlers.ofString());

        String responseBody = mainResponse2.body();

        // 사용자 이름이 표시되지 않아야 함
        assertFalse(responseBody.contains(user.name),
                "로그아웃 후에는 사용자 이름이 표시되면 안됨");

        // main.jsp의 실제 내용이 아닌 로그인 페이지여야 함
        assertFalse(responseBody.contains("경매 목록") ||
                        responseBody.contains("수강꾸러미"),
                "로그아웃 후에는 main.jsp 내용이 표시되면 안됨");

        System.out.println("  ✓ 로그아웃 후 main.jsp 접근 차단 확인");
        System.out.println("  ✓ 로그인 페이지로 이동 확인");
    }

    @Test
    @Order(15)
    @DisplayName("15. 로그아웃 후 여러 페이지 접근 불가 테스트")
    void testAccessMultiplePagesAfterLogout() throws IOException, InterruptedException {
        System.out.println("테스트: 로그아웃 후 여러 페이지 접근 시도");

        TestUser user = TEST_USERS[0];

        // Given: 로그인
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
        HttpUtil.getCookieFromResponse(loginResponse).ifPresent(cookie -> sessionCookie = cookie);
        System.out.println("  ✓ 로그인 완료");

        // When: 로그아웃
        HttpRequest logoutRequest = HttpRequest.newBuilder()
                .uri(URI.create(LOGOUT_URL))
                .header("Cookie", sessionCookie)
                .GET()
                .build();

        httpClient.send(logoutRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println("  ✓ 로그아웃 완료");

        // Then: 여러 번 접근 시도해도 모두 차단되어야 함
        for (int i = 1; i <= 3; i++) {
            HttpRequest mainRequest = HttpRequest.newBuilder()
                    .uri(URI.create(MAIN_URL))
                    .header("Cookie", sessionCookie)
                    .GET()
                    .build();

            HttpResponse<String> mainResponse = httpClient.send(mainRequest,
                    HttpResponse.BodyHandlers.ofString());

            String responseBody = mainResponse.body();

            assertFalse(responseBody.contains(user.name),
                    i + "번째 시도: 사용자 이름이 표시되면 안됨");

            assertFalse(responseBody.contains("경매 목록") ||
                            responseBody.contains("수강꾸러미"),
                    i + "번째 시도: main.jsp 내용이 표시되면 안됨");

            System.out.println("    ✓ " + i + "번째 접근 차단 확인");

            Thread.sleep(300); // 0.3초 대기
        }

        System.out.println("  ✓ 로그아웃 후 지속적인 접근 차단 확인");
    }

// ========================================
// 세션 없이 여러 페이지 접근 테스트
// ========================================

    @Test
    @Order(16)
    @DisplayName("16. 세션 없이 직접 URL 접근 불가 테스트")
    void testDirectURLAccessWithoutSession() throws IOException, InterruptedException {
        System.out.println("테스트: 세션 없이 직접 URL 접근");

        // Given: 로그인하지 않은 상태 (새 HTTP 클라이언트)
        HttpClient newClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        // When & Then: 다양한 보호된 페이지 접근 시도
        String[] protectedPages = {
                MAIN_URL,
                // 추가로 보호해야 할 페이지들이 있다면 여기에 추가
        };

        for (String pageUrl : protectedPages) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(pageUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = newClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            String responseBody = response.body();

            newClient.close();

            // 보호된 페이지의 내용이 표시되면 안됨
            assertFalse(responseBody.contains("경매 목록") ||
                            responseBody.contains("수강꾸러미"),
                    pageUrl + " 내용이 표시되면 안됨");

            // 로그인 페이지로 리다이렉트되어야 함
            assertTrue(response.statusCode() == 302 ||
                            responseBody.contains("로그인"),
                    pageUrl + "은 로그인 페이지로 리다이렉트되어야 함");

            System.out.println("  ✓ " + pageUrl + " 접근 차단 확인");
        }
    }

// ========================================
// 만료된 세션 테스트 (선택사항)
// ========================================

    @Test
    @Order(17)
    @DisplayName("17. 잘못된 세션 쿠키로 접근 불가 테스트")
    void testAccessWithInvalidSessionCookie() throws IOException, InterruptedException {
        System.out.println("테스트: 잘못된 세션 쿠키로 접근");

        // Given: 존재하지 않는 세션 쿠키
        String fakeSessionCookie = "JSESSIONID=FAKE_SESSION_12345";

        // When: 가짜 세션 쿠키로 main.jsp 접근 시도
        HttpRequest mainRequest = HttpRequest.newBuilder()
                .uri(URI.create(MAIN_URL))
                .header("Cookie", fakeSessionCookie)
                .GET()
                .build();

        HttpResponse<String> mainResponse = httpClient.send(mainRequest,
                HttpResponse.BodyHandlers.ofString());

        // Then: 접근 차단
        String responseBody = mainResponse.body();

        assertFalse(responseBody.contains("경매 목록") ||
                        responseBody.contains("수강꾸러미"),
                "잘못된 세션으로는 main.jsp 내용이 표시되면 안됨");

        System.out.println("  ✓ 잘못된 세션 쿠키 접근 차단 확인");
    }

    // ========================================
    // 헬퍼 메서드
    // ========================================

    /**
     * 모든 테스트 계정 생성
     */
    private void createAllTestAccounts() {
        System.out.println("테스트 계정 생성 중...");

        for (TestUser user : TEST_USERS) {
            createTestAccount(user);
        }

        System.out.println("✓ 총 " + TEST_USERS.length + "개 테스트 계정 생성 완료");
    }

    /**
     * 특정 테스트 계정 생성
     */
    private void createTestAccount(TestUser user) {
        Student student = new Student();
        student.setStudentId(user.studentId);
        student.setName(user.name);
        student.setDepartment(user.department);
        student.setGrade(user.grade);
        student.setPassword(user.password);

        try {
            studentDAO.signUp(student);
            System.out.println("  ✓ " + user.name + " (" + user.studentId + ") 생성");
        } catch (SQLException e) {
            // 이미 존재하면 무시
            System.out.println("  ✓ " + user.name + " 이미 존재");
        }
    }

    /**
     * 모든 테스트 데이터 정리
     */
    private static void cleanupAllTestData() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getConnection();

            // 테스트 학번 범위 일괄 삭제 (99999990 ~ 99999999)
            String sql = "DELETE FROM Student WHERE student_id BETWEEN 99999990 AND 99999999";
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
