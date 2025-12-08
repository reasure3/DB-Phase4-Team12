# DB-Phase4-Team12

## 프로젝트 개요

todo

## 개발환경에서 프로젝트 가져오기
이 프로젝트는 JSP/Servlet 기반의 Eclipse Dynamic Web Project 구조를 사용합니다.
Eclipse IDE를 사용하는 것을 권장합니다.

### 1. 사전 준비
- Oracle Database 21c
- JDK21 (eclipse: JavaSE-21)
- Apache Tomcat v10.1 (로컬 실행용)
- ojdbc11.jar (`C:/oracle/ojdbc11.jar` 에 설치 권장)
- Git

### 2. Eclipse로 가져오기
1. Eclipse를 열고 `File → Import...`
2. **Existing Projects into Workspace** 선택 → Next
3. `Select root directory`에서 프로젝트 루트 폴더 선택
4. 프로젝트가 목록에 나오면 체크하고 Finish

> 프로젝트가 .classpath, .project, .settings/ 를 가지고 있다면 Eclipse가 자동으로 설정을 읽어옵니다.

빌드 및 실행
- 프로젝트 위에서 우클릭 → Run As → Run on Server → Tomcat 선택

### 3. IntelliJ로 가져오기
해당 프로젝트는 Eclipse IDE를 사용하는 것을 권장합니다.

만약 IntelliJ를 사용하고 싶으면 [INTELLIJ_SETUP.md](./docs/INTELLIJ_SETUP.md)를 참고하세요.

### 4. 실행 후 접속
```http request
http://localhost:8080/ProjectDBPhase4/main.jsp
```

### 5. 데이터베이스 설정
1. `src/main/resources/db.properties.example` 파일을 복사하여 `db.properties` 생성
생성 후 모습: `src/main/resources/db.properties`
```bash
   cp src/main/resources/db.properties.example src/main/resources/db.properties
```
* 만약 `db.properties` 파일이 이미 존재하면 다음 단계로 넘어가주세요.

2. `db.properties` 파일을 본인의 Oracle 환경에 맞게 수정
```properties
   db.url=jdbc:oracle:thin:@localhost:1521:orclpdb
   db.user=course_registration
   db.password=oracle
```

- `orclpdb`: 본인의 Oracle SID 또는 서비스명 (예: `orcl`, `xe`, `orclpdb` 등)
- `course_registration`: 본인의 Oracle 사용자명
- `oracle`: 본인의 비밀번호

> **주의:** `db.properties` 파일은 `.gitignore`에 포함되어 Git에 업로드되지 않습니다. 각자 로컬 환경에 맞게 설정하세요.

### 5. 문제 해결 팁
- **JSP가 404/서버에서 보이지 않을 때:** Web Resource Directory가 올바르게 설정되었는지 확인
- **라이브러리 클래스가 없음(ClassNotFound):** `ojdbc11.jar` 경로 확인
- **Eclipse → IntelliJ 변환 후 빌드 에러:** 프로젝트 SDK/Language level 확인, 인코딩(UTF-8) 확인
- **DB 연결 실패:** `src/main/resources/db.properties` 파일이 존재하는지, Oracle SID/서비스명이 올바른지 확인

---
마지막 수정: 2025-12-04
