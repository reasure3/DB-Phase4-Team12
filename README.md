# DB-Phase4-Team12

## 목차
1. [프로젝트 개요](#프로젝트-개요)
2. [사전 준비](#사전-준비)
3. [개발 환경 설정](#개발-환경-설정)
    - [3.1 JDK 설치](#31-jdk-설치)
    - [3.2 Apache Tomcat 설치](#32-apache-tomcat-설치)
    - [3.3 Eclipse IDE 설치](#33-eclipse-ide-설치)
4. [프로젝트 가져오기](#프로젝트-가져오기)
5. [데이터베이스 설정](#데이터베이스-설정)
6. [실행 방법](#실행-방법)
7. [문제 해결](#문제-해결)

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
4. 설치 성공 메시지 확인: `The service 'tomcat10' has been installed.`

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
   sqlplus course_registration/oracle@localhost:1521/orclpdb

   SQL> @create_schema.sql
   SQL> @insert_data.sql
   ```
> ! `create_schema.sql`와 `insert_data.sql`은 사용자가 가지고 있다고 가정합니다.

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

**마지막 수정:** 2025-12-08
