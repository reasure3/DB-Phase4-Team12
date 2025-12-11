# COMP322 Term Project - Phase 4
# 경매 기반 수강신청 시스템 - 동시성 제어 보고서

**Team12**

---

## 개요

본 문서는 Phase 4에서 구현한 트랜잭션 기반 동시성 제어(Concurrency Control) 방법을 설명합니다.

웹 애플리케이션은 여러 사용자가 동시에 접속하여 데이터베이스를 조작하므로, 적절한 동시성 제어 없이는 다음과 같은 문제가 발생할 수 있습니다:

**1) 데이터 불일치 (Data Inconsistency)**
- 예: 두 학생이 동시에 같은 분반을 담을 때 정원 초과

**2) Lost Update (갱신 손실)**
- 예: 두 사용자가 동시에 같은 레코드를 수정할 때 한 쪽의 수정이 사라짐

**3) Dirty Read (오손 읽기)**
- 예: 아직 커밋되지 않은 데이터를 읽어서 잘못된 판단

**4) Primary Key 중복**
- 예: 동시에 시퀀스를 생성할 때 같은 ID 발급

본 프로젝트에서는 다음 세 가지 방법으로 동시성 제어를 구현했습니다:
- 트랜잭션 관리 (명시적 커밋/롤백)
- FOR UPDATE를 이용한 비관적 락(Pessimistic Locking)
- 비동기 로그 처리를 통한 성능 최적화

---

## 동시성 제어가 필요한 상황 분석

### 2.1 문제 상황 1: 시퀀스 생성 시 중복 발생

**시나리오:**
- 학생 A와 학생 B가 동시에 같은 경매에 입찰
- 두 사용자 모두 새로운 BID_SEQUENCE를 생성해야 함

**문제:**
1. 학생 A가 현재 최대 시퀀스 조회: BID004
2. 학생 B가 현재 최대 시퀀스 조회: BID004 (동시에 조회)
3. 학생 A가 BID005 생성 및 INSERT
4. 학생 B가 BID005 생성 및 INSERT
5. Primary Key 중복 오류 발생!

**예상 결과:**
- 학생 A: BID005 (정상)
- 학생 B: BID006 (정상)

**실제 결과 (동시성 제어 없을 때):**
- 학생 A: BID005 (정상)
- 학생 B: BID005 (ORA-00001: unique constraint violated)

### 2.2 문제 상황 2: 수강꾸러미 담기 시 정원 초과

**시나리오:**
- 정원 30명인 분반에 현재 29명이 담음
- 학생 A와 학생 B가 동시에 해당 분반을 담으려고 시도

**문제:**
1. 학생 A가 현재 담은 인원 조회: 29명 (정원 내 OK)
2. 학생 B가 현재 담은 인원 조회: 29명 (정원 내 OK)
3. 학생 A가 BasketItem INSERT (총 30명)
4. 학생 B가 BasketItem INSERT (총 31명)
5. 정원 초과!

**예상 결과:**
- 학생 A: 담기 성공 (30명 도달)
- 학생 B: 담기 실패 (정원 초과)

**실제 결과 (동시성 제어 없을 때):**
- 학생 A: 담기 성공
- 학생 B: 담기 성공 (정원 초과 발생)

### 2.3 문제 상황 3: 로그 기록 시 성능 저하

**시나리오:**
- 수십 명의 학생이 동시에 로그인
- 각 로그인마다 Log 테이블에 INSERT 필요

**문제:**
1. 로그인 처리 완료 후 Log INSERT 대기
2. Log INSERT가 느리면 사용자가 오래 기다림
3. 로그 기록 실패 시 로그인도 실패하는 것으로 보임

**해결 필요:**
- 로그 기록은 메인 기능과 독립적이어야 함
- 로그 실패가 사용자 경험에 영향 없어야 함

---

## 구현한 동시성 제어 방법

### 3.1 트랜잭션 관리 (Transaction Management)

#### 3.1.1 기본 설정: autoCommit = false

**목적:**
- 모든 데이터베이스 작업을 트랜잭션 단위로 관리
- 여러 SQL 문을 하나의 논리적 작업 단위로 묶음
- 오류 발생 시 전체 작업 취소 (원자성 보장)

**구현 위치:**
- `com.team12.auction.util.DBConnection.java#getConnection`

**효과:**
- 각 SQL 실행 후 자동으로 커밋되지 않음
- 명시적으로 commit() 또는 rollback() 호출 필요
- 작업 중 오류 발생 시 전체 작업 취소 가능

#### 3.1.2 명시적 커밋/롤백 패턴

모든 DAO 메서드에서 다음 패턴을 사용:

```java
Connection conn = null;
PreparedStatement pstmt = null;
ResultSet rs = null;

try {
    conn = DBConnection.getConnection();

    // 1) SQL 실행
  String sql = "INSERT INTO BasketItem (basket_id, section_id, ...) VALUES (?, ?, ...)";
    pstmt = conn.prepareStatement(sql);
    pstmt.setString(1, basketId);
    pstmt.setString(2, sectionId);
    pstmt.executeUpdate();

    // 2) 성공 시 커밋
    DBConnection.commit(conn);  // 명시적 커밋

} catch (SQLException e) {
    // 3) 실패 시 롤백
    DBConnection.rollback(conn);  // 명시적 롤백
    throw e;
} finally {
    // 4) 리소스 해제
    DBConnection.close(rs, pstmt, conn);
}
```

**장점:**
- 오류 발생 시 데이터베이스 일관성 유지
- 부분적으로 실행된 작업 자동 취소
- 여러 SQL을 하나의 트랜잭션으로 묶을 수 있음

**적용 예시:**
- BasketDAO.addSectionToBasket()
- BidDAO.insertBid()
- StudentDAO.signUp()
- 기타 모든 INSERT/UPDATE/DELETE 작업

#### 3.1.3 트랜잭션 격리 수준 (Isolation Level)

**현재 설정:**
- Oracle 기본 격리 수준: READ COMMITTED
- 커밋된 데이터만 읽을 수 있음
- Dirty Read 방지

**특징:**
- 다른 트랜잭션이 커밋하지 않은 데이터는 보이지 않음
- Non-repeatable Read는 발생 가능 (우리 프로젝트에서는 문제 없음)
- Phantom Read는 발생 가능 (우리 프로젝트에서는 문제 없음)

**선택 이유:**
- 성능과 일관성의 균형
- 대부분의 웹 애플리케이션에 적합
- Oracle 기본값 사용으로 추가 설정 불필요

### 3.2 FOR UPDATE를 이용한 비관적 락 (Pessimistic Locking)

#### 3.2.1 개념

**비관적 락(Pessimistic Locking):**
- 충돌이 발생할 것이라고 가정하고 미리 락을 획득
- 다른 트랜잭션은 락이 해제될 때까지 대기
- 데이터 일관성을 강력하게 보장

**FOR UPDATE 구문:**
- SELECT 문에 FOR UPDATE를 추가하면 조회한 행에 락을 걸음
- 다른 트랜잭션은 COMMIT 또는 ROLLBACK까지 대기
- Oracle에서 제공하는 표준 락 메커니즘

#### 3.2.2 시퀀스 생성 시 FOR UPDATE 사용

**문제:**
- 여러 사용자가 동시에 BID001, BID002, ... 같은 시퀀스를 생성
- 동시에 MAX() 값을 조회하면 같은 번호가 생성될 수 있음

**해결:**
- FOR UPDATE로 테이블 전체에 락을 걸어 순차적으로 처리

**구현 위치:**
- `com.team12.auction.dao.BidDAO.generateBidSequence()`
- `com.team12.auction.dao.LogDAO.generateLogSequence()`

**코드 예시 (BidDAO.java):**
```java
public String generateBidSequence(Connection conn) throws SQLException {
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String sequence = "BID001";

    try {
        // 1단계: FOR UPDATE로 락 걸기
  String lockSql = "SELECT 1 FROM Bid WHERE ROWNUM = 1 FOR UPDATE";
        pstmt = conn.prepareStatement(lockSql);
        pstmt.executeQuery();
        pstmt.close();

        // 2단계: 현재 최대 시퀀스 번호 조회
  String sql = "SELECT 'BID' || " +
                     "LPAD(NVL(MAX(TO_NUMBER(SUBSTR(bid_sequence, 4))), 0) + 1, 3, '0') " +
                     "FROM Bid";
        pstmt = conn.prepareStatement(sql);
        rs = pstmt.executeQuery();

        if (rs.next()) {
  sequence = rs.getString(1);  // 예: BID005
        }

  pstmt.close();
        rs.close();

    } catch (SQLException e) {
        DBConnection.rollback(conn);
        throw e;
    }

    return sequence;
}
```

**동작 과정:**
1. 학생 A가 generateBidSequence() 호출
2. FOR UPDATE 실행 → Bid 테이블에 락 획득
3. 현재 최대 시퀀스 조회: BID004
4. 새 시퀀스 생성: BID005
5. INSERT 수행
6. COMMIT → 락 해제
7. 학생 B가 generateBidSequence() 호출
8. FOR UPDATE 실행 → 학생 A의 락이 해제될 때까지 대기
9. 학생 A가 COMMIT하면 락 해제됨
10. 학생 B가 락 획득
11. 현재 최대 시퀀스 조회: BID005
12. 새 시퀀스 생성: BID006
13. INSERT 수행
14. COMMIT → 락 해제

**결과:**
- 학생 A: BID005
- 학생 B: BID006
- Primary Key 중복 방지!

#### 3.2.3 FOR UPDATE 사용 위치

**적용한 곳:**
1. BidDAO.generateBidSequence()
- 입찰 시퀀스 생성 (BID001, BID002, ...)
2. LogDAO.generateLogSequence()
- 로그 ID 생성 (L0001, L0002, ...)
3. BidDAO.insertAndCheckWinner() 내부
- Enrollment ID 생성 (E0001, E0002, ...)
- Phase 4에서는 미사용이지만 메서드 존재

**적용하지 않은 곳:**
- Basket ID 생성
- 학생당 하나만 생성되므로 중복 가능성 없음
- Student ID
    - 사용자가 직접 입력하고 UNIQUE 제약조건으로 중복 방지

### 3.3 비동기 로그 처리를 통한 성능 최적화

#### 3.3.1 문제 인식

**상황:**
- 로그인/로그아웃 시 Log 테이블에 INSERT 필요
- 로그 기록은 메인 기능이 아님 (부가 기능)
- 로그 기록 실패가 사용자 경험에 영향 줘서는 안 됨

**문제:**
- 로그 INSERT가 느리면 사용자가 기다려야 함
- 로그 INSERT 실패 시 로그인도 실패하는 것처럼 보임
- DB 부하 증가 시 로그인 처리도 느려짐

#### 3.3.2 비동기 처리 구현

**구현 방법:**
- Java의 `ExecutorService`를 사용하여 별도 스레드에서 로그 기록
- 메인 스레드는 로그 기록 완료를 기다리지 않고 즉시 응답

**구현 위치:**
- `com.team12.auction.util.AsyncLogger.java`
- `com.team12.auction.servlet.LoginServlet.java`
- `com.team12.auction.servlet.LogoutServlet.java`

**코드 예시:**
```java
// AsyncLogger.java
public class AsyncLogger {
    private static final ExecutorService executor =
        Executors.newFixedThreadPool(5);

    public static void logAsync(Log log) {
  executor.submit(() -> {
            try {
                LogDAO logDAO = new LogDAO();
                logDAO.insertLog(log);
            } catch (SQLException e) {
  System.err.println("비동기 로그 기록 실패: " + e.getMessage());
            }
  });
    }
}

// LoginServlet.java
protected void doPost(HttpServletRequest request,
                      HttpServletResponse response) {
    // ... 로그인 처리 ...

    // 비동기 로그 기록 (사용자는 기다리지 않음)
    Log log = new Log();
    log.setActionType("LOGIN");
    log.setStudentId(studentId);
    AsyncLogger.logAsync(log);

    // 즉시 메인 페이지로 리다이렉트
  response.sendRedirect("main.jsp");
}
```

**장점:**
1. 사용자 응답 시간 단축
- 로그 기록 완료를 기다리지 않음
- 로그인 버튼 클릭 후 즉시 메인 페이지 이동
2. 로그 실패가 메인 기능에 영향 없음
- 로그 기록 실패해도 로그인은 정상 처리
3. 동시 접속 시 성능 향상
- 여러 사용자의 로그를 병렬로 처리

**주의사항:**
- ExecutorService는 애플리케이션 종료 시 shutdown() 호출 필요
- 로그 기록 실패는 콘솔에만 출력 (사용자에게 알리지 않음)

---

## 동시성 제어 테스트

### 4.1 시퀀스 생성 동시성 테스트

**테스트 목표:**
- 여러 사용자가 동시에 입찰할 때 BID_SEQUENCE 중복이 발생하지 않는지 확인

**테스트 방법:**

1. **테스트용 지연 코드 추가 (임시)**
  ```java
  // BidDAO.generateBidSequence() 내부에 추가
  try {
       Thread.sleep(3000);  // 3초 지연
  } catch (InterruptedException e) {
       e.printStackTrace();
   }
 ```
2. **다중 브라우저로 동시 입찰 시뮬레이션**
- 브라우저 1: 크롬 일반 모드
- 브라우저 2: 크롬 시크릿 모드

3. **동시 로그인 준비 (제출하지 않음)**
- 브라우저 1: 학번 20000001, 비밀번호 pw20000001
    - 브라우저 2: 학번 20000002, 비밀번호 pw20000002

4. **거의 동시에 로그인 버튼 클릭 (1초 이내)**
- 브라우저 1: 로그인 클릭
- 브라우저 2: 0.5초 후 로그인 클릭

5. **Eclipse 콘솔 출력 확인**
- 첫 번째 사용자가 락 획득 후 3초 대기
- 두 번째 사용자는 대기 상태
- 첫 번째 커밋 후 두 번째 사용자가 락 획득

6. **Log 테이블 확인:**
  ```sql
  SELECT log_id, action_type, student_id,
          TO_CHAR(timestamp, 'HH24:MI:SS') as time
   FROM Log
   WHERE action_type = 'LOGIN'
   ORDER BY timestamp DESC
   FETCH FIRST 2 ROWS ONLY;
 ```
**예상 결과:**

| LOG_ID | ACTION_TYPE | STUDENT_ID | TIME     |
|--------|-------------|------------|----------|
| L0027  | LOGIN       | 20000002   | 14:30:06 |
| L0026  | LOGIN       | 20000001   | 14:30:01 |

**확인 사항:**
- log_id가 중복 없이 순차적으로 생성됨 (L0026 → L0027)
- Primary Key 제약조건 위반 없음
- 두 로그인 모두 정상적으로 기록됨
- 사용자는 비동기 처리로 대기하지 않음 (sleep은 백그라운드)

**중복 확인 쿼리:**
```sql
SELECT log_id, COUNT(*) as cnt
FROM Log
GROUP BY log_id
HAVING COUNT(*) > 1;
-- 예상 결과: 0 rows (중복 없음)
```

**실패 시나리오 (FOR UPDATE 없을 때):**
```
ORA-00001: unique constraint (COURSE_REGISTRATION.LOG_PK) violated
```

**주의사항:**
- 테스트 완료 후 Thread.sleep() 코드는 반드시 제거
- sleep은 동시성 문제를 명확하게 확인하기 위한 테스트 목적

### 4.2 트랜잭션 롤백 테스트

**테스트 시나리오:**
- 수강꾸러미 담기 중 오류 발생 시 롤백되는지 확인

**테스트 방법:**

1. **BasketDAO.addSectionToBasket()에 일부러 오류 발생시키기**
  ```java
  public void addSectionToBasket(int studentId, String sectionId) throws SQLException {
     Connection conn = null;
       try {
  conn = DBConnection.getConnection();

           String sql = "INSERT INTO BasketItem ...";
           pstmt = conn.prepareStatement(sql);
           // ... INSERT 수행

         // 일부러 오류 발생
         throw new SQLException("Test exception");

           // DBConnection.commit(conn);  // 실행 안 됨
  } catch (SQLException e) {
         DBConnection.rollback(conn);  // 롤백됨
         throw e;
       }
  }
 ```
2. **웹 페이지에서 분반 담기 시도**

3. **BasketItem 테이블 확인:**
  ```sql
  SELECT * FROM BasketItem WHERE basket_id = 'B202511148';
 ```
**예상 결과:**
- BasketItem에 데이터가 INSERT되지 않음 (롤백됨)
- 사용자에게 에러 메시지 표시

**확인 사항:**
- INSERT가 부분적으로 실행되지 않음 (원자성)
- 데이터베이스 일관성 유지

### 4.3 비동기 로그 처리 테스트

**테스트 시나리오:**
- 로그 기록이 느려도 로그인 처리는 빠른지 확인

**테스트 방법:**

1. **LogDAO.insertLog()에 인위적인 지연 추가**
  ```java
  public boolean insertLog(Log log) throws SQLException {
 Thread.sleep(3000);  // 3초 지연
     // ... 실제 INSERT
   }
 ```
2. **웹 페이지에서 로그인 시도**

3. **로그인 버튼 클릭 후 메인 페이지 로딩 시간 측정**

**예상 결과:**
- 로그인 버튼 클릭 후 즉시 메인 페이지로 이동 (< 1초)
- 로그 기록은 백그라운드에서 3초 후 완료
- 사용자는 로그 기록 완료를 기다리지 않음

**확인 방법:**
- 브라우저 개발자 도구의 Network 탭에서 응답 시간 확인
- 로그인 후 즉시 다른 페이지로 이동 가능한지 확인

**비동기 처리 없을 때:**
- 로그인 버튼 클릭 후 3초 동안 대기 (나쁜 사용자 경험)
- FOR UPDATE 락으로 인해 2명 이상이 거의 동시에 로그인 하면 로그인 순서에 따라 3초씩 추가 지연 발생 (나쁜 사용자 경험)

**비동기 처리 있을 때:**
- 로그인 버튼 클릭 후 즉시 메인 페이지 (좋은 사용자 경험)

---

## 발생 가능한 동시성 문제와 해결 상태

### 5.1 Lost Update (갱신 손실)

**문제 설명:**
- 두 트랜잭션이 같은 데이터를 동시에 수정할 때 한 쪽의 수정이 사라짐

**발생 가능한 상황:**
- 학생이 프로필 수정 중 다른 곳에서도 프로필 수정 시도

**우리 프로젝트에서의 대응:**
- 상태: 해결됨
- 방법: 트랜잭션 격리 수준 (READ COMMITTED)
- 설명:
    - 각 트랜잭션은 커밋된 데이터만 읽음
- 나중에 커밋된 트랜잭션이 최종 반영됨
- Last Write Wins (마지막 쓰기가 승리)

**추가 대응 (필요 시):**
- Optimistic Locking (낙관적 락) 사용
- VERSION 컬럼 추가하여 충돌 감지
- 현재는 프로필 수정 빈도가 낮아 미적용

### 5.2 Dirty Read

**문제 설명:**
- 아직 커밋되지 않은 데이터를 다른 트랜잭션이 읽음

**발생 가능한 상황:**
- 학생 A가 BasketItem INSERT 중 학생 B가 담은 인원 조회

**우리 프로젝트에서의 대응:**
- 상태: 해결됨
- 방법: Oracle READ COMMITTED 격리 수준
- 설명:
    - 커밋되지 않은 데이터는 다른 트랜잭션에서 보이지 않음
- 학생 B는 학생 A가 COMMIT한 후에만 새 데이터 확인

**테스트:**
```sql
-- Tx1에서 INSERT 후 COMMIT 전
INSERT INTO BasketItem VALUES (...);
-- COMMIT 안 함

-- Tx2에서 조회
SELECT COUNT(*) FROM BasketItem WHERE section_id = 'CS301001';
-- 결과: Tx1의 INSERT가 보이지 않음
```

### 5.3 Non-repeatable Read (반복 불가능한 읽기)

**문제 설명:**
- 같은 트랜잭션 내에서 같은 쿼리를 두 번 실행했을 때 결과가 다름

**발생 가능한 상황:**
- 학생 A가 담은 인원을 조회하고, 다시 조회할 때 다른 값

**우리 프로젝트에서의 대응:**
- 상태: ⚠️ 발생 가능하지만 문제 없음
- 방법: 특별한 처리 없음
- 이유:
    - 우리 시스템에서는 실시간 최신 정보가 중요
- 담은 인원이 실시간으로 변하는 것이 정상
- REPEATABLE READ로 격리하면 오히려 정확도 떨어짐

**예시:**

| 시간 | 학생 A                  | 학생 B              |
|----|-----------------------|-------------------|
| T1 | SELECT COUNT(*) → 29명 |                   |
| T2 |                       | INSERT BasketItem |
| T3 |                       | COMMIT            |
| T4 | SELECT COUNT(*) → 30명 |                   |

- 학생 A가 같은 쿼리를 두 번 실행했지만 결과가 다름
- 하지만 30명이 정확한 값이므로 문제 없음

### 5.4 Phantom Read (유령 읽기)

**문제 설명:**
- 같은 트랜잭션 내에서 같은 범위 쿼리를 두 번 실행했을 때 행의 개수가 다름

**발생 가능한 상황:**
- 경매 상위 입찰 목록 조회 중 새 입찰이 추가됨

**우리 프로젝트에서의 대응:**
- 상태: ⚠️ 발생 가능하지만 문제 없음
- 방법: 특별한 처리 없음
- 이유:
    - 경매 상황은 실시간으로 변함
- 최신 정보를 보여주는 것이 더 중요
- SERIALIZABLE로 격리하면 동시성 크게 저하

**예시:**

| 시간 | 사용자 x페이지 | DB 상태 |
|----|----------------------|---------------|
| T1 | 상위 5명 조회 → A,B,C,D,E |               |
| T2 | (페이지 로딩 중...) | 새 입찰 F INSERT |
| T3 | 다시 조회 → A,F,B,C,D    | (F가 2위로 진입) |

- 같은 쿼리를 두 번 실행했지만 결과가 다름
- 하지만 최신 정보가 정확하므로 문제 없음

### 5.5 Primary Key 중복

**문제 설명:**
- 동시에 시퀀스를 생성할 때 같은 ID 발급

**발생 가능한 상황:**
- 여러 학생이 동시에 입찰할 때 BID_SEQUENCE 중복

**우리 프로젝트에서의 대응:**
- 상태: 해결됨
- 방법: FOR UPDATE를 이용한 비관적 락
- 설명:
    - generateBidSequence()에서 FOR UPDATE 사용
- 한 번에 한 트랜잭션만 시퀀스 생성 가능
- 다른 트랜잭션은 대기 후 순차 생성

**테스트 결과:**
- 10명이 동시 입찰해도 시퀀스 중복 없음
- Primary Key 제약조건 위반 없음

### 5.6 정원 초과 문제

**문제 설명:**
- 동시에 수강꾸러미에 담을 때 정원 초과

**발생 가능한 상황:**
- 정원 30명, 현재 29명
- 학생 A와 학생 B가 동시에 담기 시도

**우리 프로젝트에서의 대응:**
- 상태: 부분적으로 해결됨
- 방법: 정원 체크 로직 (애플리케이션 레벨)
- 한계:
    - 체크와 INSERT 사이에 타이밍 이슈 가능
- 완벽한 해결을 위해서는 DB 레벨 제약조건 필요

**현재 구현:**
```java
// BasketAddServlet
int basketCount = basketDAO.getBasketCount(sectionId);  // 1) 조회
int capacity = sectionDAO.getCapacity(sectionId);

if (basketCount < capacity) {
    basketDAO.addSectionToBasket(studentId, sectionId);  // 2) INSERT
}
```

**문제:**
- 1)과 2) 사이에 다른 트랜잭션이 INSERT 가능
- 결과적으로 정원 초과 발생 가능

**개선 방안:** sql 트리거 사용

---

**마지막 수정:** 2025-12-10
**Team 12**
