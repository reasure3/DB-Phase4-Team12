# DB-Phase4-Team12

## 목차
1. [프로젝트 개요](#프로젝트-개요)
2. [데모 영상](#데모-영상)
3. [사전 준비](#사전-준비)
4. [개발 환경 설정](#개발-환경-설정)
- [3.1 JDK 설치](#31-jdk-설치)
- [3.2 Apache Tomcat 설치](#32-apache-tomcat-설치)
- [3.3 Eclipse IDE 설치](#33-eclipse-ide-설치)
5. [프로젝트 가져오기](#프로젝트-가져오기)
6. [데이터베이스 설정](#데이터베이스-설정)
7. [실행 방법](#실행-방법)
8. [주요 기능 설명](#주요-기능-설명)
- [7.1 인증 시스템](#71-인증-시스템)
- [7.2 학생 정보 관리](#72-학생-정보-관리)
- [7.3 강의 조회 및 검색](#73-강의-조회-및-검색)
- [7.4 수강꾸러미 관리](#74-수강꾸러미-관리)
- [7.5 경매 시스템](#75-경매-시스템)
- [7.6 등록 조회](#76-등록-조회)
9. [사용 시나리오 예시](#사용-시나리오-예시)
10. [주의사항 및 알려진 이슈](#주의사항-및-알려진-이슈)
11. [문제 해결](#문제-해결)

---

## 프로젝트 개요

경매 기반 수강신청 시스템을 위한 웹 데이터베이스 프로젝트입니다.

**기술 스택:**
- Backend: JSP/Servlet
- Database: Oracle Database 21c
- Library: ojdbc11.jar
- Server: Apache Tomcat 10.1.48
- IDE: Eclipse IDE for Enterprise Java and Web Developers

---

## 데모 영상
[https://youtu.be/_ZQxtj74sJo?si=RMcB6t_lTN64A6Pu](https://youtu.be/_ZQxtj74sJo?si=RMcB6t_lTN64A6Pu)

## 사전 준비

다음 소프트웨어가 설치되어 있어야 합니다:

- **JDK 21**
- **Apache Tomcat 10.1.48**
- **Eclipse IDE for Enterprise Java and Web Developers** (2025-03 R 이상)
- **Oracle Database 21c**
- **Git**

---

## 개발 환경 설정

### 3.1 JDK 설치

1. [Oracle JDK 21 다운로드 페이지](https://www.oracle.com/java/technologies/downloads/#java21)에서 JDK 21 설치
2. 환경 변수 설정:
    - `JAVA_HOME` 환경 변수를 JDK 설치 경로로 설정
  ```cmd
  set JAVA_HOME=C:\Program Files\Java\jdk-21
 ```
3. 설치 확인:
 ```cmd
  echo %JAVA_HOME%
 ```
### 3.2 Apache Tomcat 설치

#### Step 1: Tomcat 다운로드
- [Apache Tomcat 10.1.48 다운로드](https://tomcat.apache.org/download-10.cgi)
- **64-bit Windows zip** 파일 다운로드

#### Step 2: 압축 해제
- 다운로드한 zip 파일을 `C:\` 드라이브에 압축 해제
- 경로: `C:\apache-tomcat-10.1.48`

#### Step 3: Tomcat 서비스 설치
1. 명령 프롬프트(cmd)를 **관리자 권한**으로 실행
2. Tomcat bin 디렉토리로 이동:
 ```cmd
  cd C:\apache-tomcat-10.1.48\bin
 ```
3. 서비스 설치:
```cmd
  service.bat install tomcat10
 ```
4.설치 성공 메시지 확인: `The service 'tomcat10' has been installed.`

#### Step 4: Tomcat 실행 확인
1. `C:\apache-tomcat-10.1.48\bin\tomcat10w.exe`를 **관리자 권한**으로 실행
2. `General` 탭에서 `Start` 버튼 클릭
3. `Service Status`가 `Started`로 변경되는지 확인
4. 웹 브라우저에서 `http://localhost:8080` 접속하여 Tomcat 기본 페이지 확인

### 3.3 Eclipse IDE 설치

#### Step 1: Eclipse 다운로드
- [Eclipse Downloads](https://www.eclipse.org/downloads/packages/)에서 **Eclipse IDE for Enterprise Java and Web Developers** 다운로드

#### Step 2: Eclipse 설치
1. 다운로드한 zip 파일을 `C:\` 드라이브에 압축 해제
2. 경로 예시: `C:\eclipse-jee-2025-09-R-win32-x86_64\eclipse`

#### Step 3: Eclipse 실행 및 Workspace 설정
1. `eclipse.exe` 실행
2. Workspace 경로 설정 (예: `C:\eclipse-workspace`)

#### Step 4: Tomcat 서버를 Eclipse에 등록

1. Eclipse에서 **Window → Show View → Other → Server → Servers** 선택
2. Servers 탭에서 **"No servers are available. Click this link to create a new server..."** 클릭
3. **Apache → Tomcat v10.1 Server** 선택 → **Next**
4. **Browse** 버튼 클릭 후 `C:\apache-tomcat-10.1.48` 선택 → **Finish**
5. **중요:** `tomcat10w.exe`에서 Tomcat 서비스를 **Stop** (Eclipse에서 실행하기 위해)

#### Step 5: Eclipse에서 Tomcat 서버 시작
1. Servers 탭에서 `Tomcat v10.1 Server at localhost` 우클릭 → **Start**
2. Console 탭에서 서버 시작 로그 확인
3. `Server startup in [xxxx] milliseconds` 메시지 확인

---

## 프로젝트 가져오기

### Step 1: Git Clone
```bash
git clone https://github.com/reasure3/DB-Phase4-Team12.git
cd DB-Phase4-Team12
```

### Step 2: ojdbc11.jar 준비하기

Oracle JDBC 드라이버를 다운로드합니다.

1. [Oracle JDBC Downloads 페이지](https://www.oracle.com/kr/database/technologies/appdev/jdbc-downloads.html) 접속
2. **Oracle Database 21c (21.1.0.0) JDBC Driver & UCP Downloads** 섹션 선택
3. **ojdbc11.jar** 다운로드

> **참고:** Oracle 계정 로그인이 필요할 수 있습니다.

#### 2-2: Tomcat lib 폴더에 복사

Tomcat이 JDBC 드라이버를 인식할 수 있도록 lib 폴더에 복사합니다.

```cmd
copy [다운로드 경로]\ojdbc11.jar C:\apache-tomcat-10.1.48\lib\
```

예시:
```cmd
copy C:\Users\사용자명\Downloads\ojdbc11.jar C:\apache-tomcat-10.1.48\lib\
```

#### 2-3: 프로젝트 lib 폴더에 복사

프로젝트에서도 JDBC 드라이버를 참조할 수 있도록 복사합니다.

```bash
# 프로젝트 루트에서 실행
copy [다운로드 경로]\ojdbc11.jar lib\
```

예시:
```cmd
copy C:\Users\사용자명\Downloads\ojdbc11.jar lib\
```

#### 2-4: 설치 확인

1. **Tomcat lib 확인:** `C:\apache-tomcat-10.1.48\lib\ojdbc11.jar` 존재 확인
2. **프로젝트 lib 확인:** `<프로젝트 루트>\lib\ojdbc11.jar` 존재 확인

> **중요:** 두 위치 모두에 ojdbc11.jar 파일이 있어야 정상적으로 동작합니다.


### Step 3: Eclipse로 프로젝트 Import

1. Eclipse에서 **File → Import...**
2. **Existing Projects into Workspace** 선택 → **Next**
3. **Select root directory**에서 클론한 프로젝트 폴더 선택
4. 프로젝트 체크 확인 → **Finish**

> **참고:** 프로젝트에 `.classpath`, `.project`, `.settings/` 파일이 포함되어 있어 Eclipse가 자동으로 설정을 읽어옵니다.

### Step 3-1: IntelliJ 사용 (선택사항)

해당 프로젝트는 Eclipse를 기준으로 생성되었기에 Eclipse 사용을 추천합니다,

그래도 IntelliJ를 사용하려면 [INTELLIJ_SETUP.md](./docs/INTELLIJ_SETUP.md)를 참고하세요.

---

## 데이터베이스 설정

### Step 1: Oracle 사용자 생성 (처음 설정 시)

1. Oracle SQL*Plus 또는 SQL Developer에서 **SYSTEM** 또는 **SYS** 계정으로 접속
2. 다음 명령어로 사용자 생성:
  ```sql
  ALTER SESSION SET "_ORACLE_SCRIPT" = TRUE; -- oracle 21c 버전의 경우
  CREATE USER course_registration IDENTIFIED BY oracle;
   GRANT CONNECT, RESOURCE, DBA TO course_registration;
 ```
### Step 2: 데이터베이스 스키마 및 데이터 생성

1. `course_registration` 계정으로 접속
2. 테이블 생성 및 데이터 생성:
  ```bash
  sqlplus course_registration/oracle@localhost:1521/orcl

   SQL> @create_schema.sql
   SQL> @insert_data.sql
 ```

> ! `create_schema.sql`와 `insert_data.sql`은 사용자가 가지고 있다고 가정합니다.
> `create_schema.sql` 의 경우 `Team12-Phase2-1.sql`와 동일한 내용입니다.
> `insert_data.sql` 의 경우 `Team12-Phase3-2.sql`와 동일한 내용입니다.


### Step 3: db.properties 파일 생성

1. `src/main/resources/db.properties.example` 파일을 복사하여 `db.properties` 생성:
  ```bash
  cp src/main/resources/db.properties.example src/main/resources/db.properties
 ```
### Step 4: db.properties 파일 수정

`src/main/resources/db.properties` 파일을 본인의 Oracle 환경에 맞게 수정:

```properties
db.url=jdbc:oracle:thin:@localhost:1521:orcl
db.user=course_registration
db.password=oracle
```

**설정 값 설명:**
- `orcl`: 본인의 Oracle SID 또는 서비스명 (예: `orcl`, `xe`, `orclpdb`)
- `course_registration`: Oracle 사용자명
- `oracle`: Oracle 비밀번호

---

## 실행 방법

### Step 1: 프로젝트를 서버에 배포

1. Eclipse의 **Servers** 탭에서 `Tomcat v10.1 Server at localhost` 확인
2. 프로젝트 우클릭 → **Run As → Run on Server**
3. **Tomcat v10.1 Server at localhost** 선택 → **Finish**

### Step 2: 웹 브라우저에서 접속

```
http://localhost:8080/ProjectDBPhase4/
```
접속 후, 로그인 페이지가 나타납니다.

---

## 주요 기능 설명

본 시스템은 학생들이 웹 브라우저를 통해 수강신청을 하고, 인기 강의에 대해서는 경매 방식으로 수강 기회를 얻을 수 있는 시스템입니다.

### 7.1 인증 시스템

#### 7.1.1 회원가입 (/auth/signup)
- 학번, 이름, 학과, 학년, 비밀번호를 입력하여 회원가입
- 학번 중복 검사 자동 수행
- 최대 학점(18학점) 및 최대 포인트(90점) 자동 설정
- 입력값 검증: 학년은 1~4 사이, 비밀번호 일치 확인

#### 7.1.2 로그인 (/auth/login)
- 학번과 비밀번호로 인증
- 로그인 성공 시 세션에 학생 정보 저장
- 로그인 실패 시 오류 메시지 표시
- 로그인 기록을 Log 테이블에 자동 저장 (비동기 처리)

#### 7.1.3 로그아웃 (/auth/logout)
- 세션 무효화 및 로그인 페이지로 이동
- 로그아웃 기록을 Log 테이블에 자동 저장 (비동기 처리)

#### 7.1.4 세션 관리
- 모든 보호된 페이지는 loginCheck.jsp를 통해 세션 확인
- 미로그인 사용자는 자동으로 로그인 페이지로 리다이렉트

### 7.2 학생 정보 관리

#### 7.2.1 내 정보 조회 (/mypage)
- 기본 정보: 학번, 이름, 학과, 학년
- 학점 정보: 현재 수강 학점 / 최대 학점
- 포인트 정보: 보유 포인트 / 최대 포인트
- 보유 포인트 = 최대 포인트 - 사용한 포인트 총합
- 최근 활동 내역: 최근 10개의 로그 기록 표시
- 로그인, 로그아웃, 프로필 수정, 비밀번호 변경 등

#### 7.2.2 프로필 수정 (/editProfile)
- 이름, 학과, 학년 수정 가능
- 학번은 Primary Key이므로 수정 불가
- 수정 성공 시 세션의 studentName도 자동 업데이트
- 수정 내역을 Log 테이블에 자동 기록

#### 7.2.3 비밀번호 변경 (/auth/changePassword)
- 현재 비밀번호 확인 필수
- 새 비밀번호는 최소 4자 이상
- 새 비밀번호는 현재 비밀번호와 달라야 함
- 변경 성공 시 Log 테이블에 기록

### 7.3 강의 조회 및 검색

#### 7.3.1 전체 강의 조회 (/section/list)
- 시스템에 등록된 모든 강의의 분반 정보 표시
- 표시 정보:
    - 강의코드, 분반 번호, 강의명, 교수명
- 학과, 학점, 정원
- 현재 수강꾸러미에 담은 인원 수
- 정원 대비 담은 인원 수를 색상으로 구분:
    - 초록색: 여유 있음 (담은 인원 < 정원)
- 노란색: 정원 일치 (담은 인원 = 정원)
- 빨간색: 정원 초과 (담은 인원 > 정원)

#### 7.3.2 강의 검색 기능
- 키워드 검색: 강의명, 교수명, 강의코드로 검색 (대소문자 무시)
- 학과 필터: 특정 학과의 강의만 조회
- 검색 조건 조합 가능 (키워드 + 학과)

#### 7.3.3 수강꾸러미 담기
- 각 분반마다 "담기" 버튼 제공
- 담기 성공/실패 메시지는 Flash Message 방식으로 표시
- 성공 메시지: 녹색 배경
- 실패 메시지: 빨간색 배경

### 7.4 수강꾸러미 관리

#### 7.4.1 수강꾸러미 개념
- 학생이 수강하고 싶은 분반을 임시로 담아두는 장바구니
- 각 학생은 하나의 Basket을 가지며, 여러 분반을 담을 수 있음
- 담긴 분반은 BasketItem 테이블에 기록됨

#### 7.4.2 분반 담기 (/basket/add)
- 수강꾸러미에 분반 추가
- 자동 검증:
    - 학점 제한 체크: 현재 담은 학점 + 추가할 학점 ≤ 최대 학점
- 중복 체크: 이미 담은 분반은 추가 불가
- 검증 실패 시 구체적인 오류 메시지 제공
- 예: "학점 제한을 초과합니다. (현재: 15학점, 추가: 3학점, 제한: 18학점)"

#### 7.4.3 수강꾸러미 조회 (/basket/list)
- 담은 모든 분반의 상세 정보 표시
- 표시 정보:
    - 강의코드, 분반 번호, 강의명, 교수명
- 학점, 정원, 강의실
- 현재 담은 인원 수
- 상태 (PENDING/SUCCESS/FAILED)
- 상단에 총 학점 표시: "현재 담긴 강의 수: X과목 (Y / Z 학점)"
    - Y: 현재 담은 총 학점
- Z: 최대 학점

#### 7.4.4 분반 삭제 (/basket/remove)
- 수강꾸러미에서 분반 제거
- 삭제 성공 시 메시지 표시: "수강꾸러미에서 분반을 삭제했습니다."

#### 7.4.5 상태(Status) 설명
- PENDING: 수강꾸러미에 담긴 상태 (기본값)
- SUCCESS: 수강신청 성공
- FAILED: 수강신청 실패

### 7.5 경매 시스템

#### 7.5.1 경매 개념
- 인기 강의(정원 초과 예상)에 대해 포인트 입찰 방식으로 수강권 분배
- 각 경매는 특정 분반에 대해 진행되며, 수강 가능 인원(available_slots) 설정
- 경매 기간 내에 학생들이 포인트를 입찰
- 경매 종료 후 상위 N명이 해당 분반을 수강 (Phase 4에서는 일괄 처리 미구현)

#### 7.5.2 나의 경매 조회 (/auction/list)
- 내가 수강꾸러미에 담은 분반 중 경매가 진행 중인 목록 표시
- 표시 정보:
    - 경매 ID, 강의 정보, 수강 가능 인원
- 경매 시작일/종료일, 상태 (ACTIVE/COMPLETED)
- 내 입찰 금액, 입찰 결과 (합격/미선정)
- 경매 ID를 클릭하면 상세 페이지로 이동

#### 7.5.3 경매 상세 및 입찰 (/auction/bid)
- 경매 상세 정보 표시
- 현재 상위 입찰 현황 표시 (익명, 순위별 입찰 포인트만 표시)
- 수강 가능 인원만큼만 표시 (예: 5명이면 1위~5위만)
- 입찰 가능 조건:
    - 해당 분반을 수강꾸러미에 담았을 것
- 경매 기간 내일 것 (start_time ≤ 현재 ≤ end_time)
- 경매 상태가 ACTIVE일 것
- 아직 입찰하지 않았을 것
- 입찰 시 예상 순위 계산 및 표시
- "현재 예상 순위: X위" 메시지 제공
- 입찰 불가 시 구체적인 사유 표시
- 예: "수강꾸러미에 담은 분반만 입찰할 수 있습니다."

### 7.6 등록 조회

#### 7.6.1 나의 등록 조회 (/enrollment/list)
- 현재 수강신청이 완료된 강의 목록 표시
- 표시 정보:
    - 분반 ID, 강의코드, 분반 번호
- 강의명, 교수명, 강의실, 정원
- 신청 경로 (FROM_BASKET / FROM_AUCTION)
- 사용 포인트
- 신청 경로 설명:
    - FROM_BASKET: 수강꾸러미에서 자동 등록
- FROM_AUCTION: 경매 낙찰 후 등록

#### 7.6.2 포인트 사용 내역
- 각 강의별로 사용한 포인트 표시
- 일반 수강꾸러미: 0점 (포인트 사용 없음)
- 경매 낙찰: 입찰 금액만큼 포인트 차감

#### 7.6.3 등록 삭제
- 학생의 분반 등록 데이터 제거
- 삭제 성공 시 메시지 표시: "수강 등록을 취소했습니다."

---

## 사용 시나리오 예시

### 시나리오 1: 신규 회원가입 및 강의 조회
1. `/auth/signup` 접속
2. 학번: 20241234, 이름: 홍길동, 학과: 컴퓨터학부, 학년: 3, 비밀번호 입력
3. 회원가입 완료 → 자동으로 로그인 페이지로 이동
4. 로그인: 학번 20241234, 비밀번호 입력
5. 메인 페이지에서 "강의 조회" 클릭
6. 키워드에 "데이터베이스" 입력 후 검색
7. 원하는 분반의 "담기" 버튼 클릭
8. "분반을 수강꾸러미에 담았습니다." 메시지 확인

### 시나리오 2: 수강꾸러미 관리
1. 로그인 후 메인 페이지에서 "수강꾸러미" 클릭
2. 담은 강의 목록 확인
3. 총 학점 확인 (예: "현재 담긴 강의 수: 5과목 (15 / 18 학점)")
4. 불필요한 강의의 "취소" 버튼 클릭
5. "수강꾸러미에서 분반을 삭제했습니다." 메시지 확인

### 시나리오 3: 경매 참여
1. 로그인 후 메인 페이지에서 "경매" 클릭
2. 나의 경매 목록에서 원하는 경매 ID 클릭
3. 경매 상세 페이지에서 현재 상위 입찰 현황 확인
4. 입찰 포인트 입력 (예: 50점)
5. "입찰하기" 버튼 클릭
6. "입찰이 완료되었습니다. 현재 예상 순위: 3위" 메시지 확인

### 시나리오 4: 내 정보 관리
1. 로그인 후 우측 상단 "내 정보" 클릭
2. 기본 정보, 학점 정보, 보유 포인트 확인
3. 최근 활동 내역 확인 (로그인, 입찰 등)
4. "내 정보 수정" 버튼 클릭
5. 학과를 "소프트웨어학부"로 변경
6. "저장" 버튼 클릭
7. "프로필 업데이트에 성공했습니다." 메시지 확인

---

## 주의사항 및 알려진 이슈

### 9.1 데이터베이스 초기 데이터
- 본 시스템을 테스트하려면 Course, Section, Auction 테이블에 샘플 데이터가 있어야 합니다.
- insert_data.sql을 실행하세요.

### 9.2 경매 기간 설정
- Auction.start_time과 end_time이 현재 날짜를 포함해야 입찰 가능합니다.
- 테스트 시 적절한 날짜로 업데이트하세요.

### 9.3 포인트 시스템
- getCurrentPoints()는 Enrollment.points_used 기준으로 계산합니다.

### 9.4 BasketItem 상태
- 현재 새로 추가되는 BasketItem은 PENDING 상태로 유지됩니다.

### 9.5 동시 접속 테스트
- 여러 브라우저 또는 시크릿 모드를 사용하여 동시 접속 테스트 가능합니다.
- 입찰 시 시퀀스 중복이 발생하지 않는지 확인하세요.

---

## 문제 해결

### 1. JSP 파일이 404 에러
- **원인:** Web Resource Directory 설정 오류
- **해결:** 프로젝트 우클릭 → Properties → Web Project Settings 확인

### 2. ClassNotFoundException
- **원인:** `ojdbc11.jar` 라이브러리 누락
- **해결:**
1. `ojdbc11.jar`를 프로젝트의 `lib` 폴더 (혹은 기타 폴더)에 추가
2. 프로젝트 우클릭 → Properties → Java Build Path → Libraries → Add JARs


\* 참고: 프로젝트 기본 설정은 `<프로젝트 루트>/lib/ojdbc11.jar` 에 위치한다고 가정합니다.
<br> 만약 다른 위치에 추가한다면 위에 나온대로 설정을 해주세요.

### 3. Eclipse에서 Tomcat 서버 시작 실패
- **원인:** `tomcat10w.exe`에서 Tomcat이 이미 실행 중
- **해결:** `tomcat10w.exe`를 열어 **Stop** 클릭

### 4. DB 연결 실패
- **원인:** `db.properties` 파일 설정 오류 또는 데이터베이스 미실행
- **해결:**
1. `db.properties` 파일 존재 확인
2. Oracle SID/서비스명, 사용자명, 비밀번호 확인
3. Oracle Database가 실행 중인지 확인
4. `course_registration` 계정이 생성되어 있는지 확인

### 5. 테이블이 존재하지 않음 (ORA-00942)
- **원인:** 데이터베이스 스키마가 생성되지 않음
- **해결:** `create_schema.sql` 스크립트 실행 확인

### 6. 인코딩 문제 (한글 깨짐)
- **원인:** Eclipse 프로젝트 인코딩이 UTF-8이 아님
- **해결:**
1. 프로젝트 우클릭 → Properties → Resource → Text file encoding
2. **UTF-8** 선택 → Apply

---

참고: [Transaction Control](./docs/TRANSACTION.md)

---

**마지막 수정:** 2025-12-10
