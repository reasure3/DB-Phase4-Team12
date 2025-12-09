# IntelliJ로 프로젝트 가져오기
해당 문서는 IntelliJ Ultimate 2025.2.5 버전을 기준으로 작성되었습니다.

해당 문서는 [종벌님의 티스토리 블로그](https://jong-bae.tistory.com/53)를 참고하여 작성되었습니다.

## 1. 프로젝트 임포트하기
1. Intellij에서 `DB-Phase4-Team12` 프로젝트를 열기
2. `File → New → Module from Existing Sources...`
3. 현재 프로젝트 경로를 선택 후 `Select Forlder`
4. `Import module from external model` 선택 후, `Eclipse` 선택, `NEXT`
5. `Keep project and module files in {프로젝트경로}` 선택 후, `NEXT`
6. 스캔된 프로젝트(`ProjectDBPhase4`) 체크 후, `Next`
7. `Use default project code style` 선택 후, `CREATE`

## 2. SDK 설정하기
1. `File → Project Structure... → Project`
2. `SDK:`에서 `JavaSE-21 Oracle OpenJDK` 선택 (혹은 기타 JDK21)

## 3. Library 추가
1. `File → Project Structure... → Libraries → '+' → Java`
2. 파일탐색기에서 `odjbc11.jar` 선택 (목록에 없는 경우만)

## 4. Dependencies 설정하기
1. `File → Project Structure... → Modules`
2. 현재 프로젝트(`ProjectDBPhase4`) 선택
3. `Dependencies` 버튼 항목 선택
4. 기존 Eclipse 전용 라이브러리 제거 (빨간색으로 표시됨)
   <br>각각 선택 후, `-` 버튼 클릭
   - `org.eclipse.jst.server.core.container/org.eclipse.jst.server.tomcat.runtimeTarget/Apache Tomcat v10.1`
   - `org.eclipse.jst.j2ee.internal.web.container`
   - `org.eclipse.jst.j2ee.internal.module.container`
5. Tomcat 추가 (목록에 없는 경우): `'+' → 2 Library... → Application Server Libraries`에서
   <br>(예)`Tomcat 10.1.48` 선택 후 `ADD SELECTED`
6. ojdbc11.jar 추가 (목록에 없는 경우): `'+' → 2 Library... → Project Libraries`에서
  <br>(예) `ojdbc11` 선택 후 `ADD SELECTED`
7. __6번에서 `Project Libraries` 또는 `ojdbc`가 없는 경우에만:__
  <br>`'+' → 1 JARs or Directories...` → 파일탐색기에서 `odjbc11.jar` 선택

## 5. Facet 추가
1. `File → Project Structure... → Facets → '+' → Web`
2. 현재 프로젝트(`ProjectDBPhase4`) 선택 → `OK`
3. 추가된 `Web (ProjectDBPhase4)` 선택
4. `Web Resource Directories → 기존거 선택 → 수정 버튼 (연필 모양)`
5. `Web Resource directory path:`를 `{프로젝트 경로}\src\main\webapp`
6. `Relative path in the deployment directory:`를 `/`로 설정

## 6. Artifacts 추가
1. `File → Project Structure... → Artifacts → '+'`
2. `Web Application: Exploded > → From Modules...`
3. 현재 프로젝트(`ProjectDBPhase4`) 선택 → `OK`

## 7. 리소스 폴더 설정
1. `File → Project Structure... → Modules`
2. 현재 프로젝트(`ProjectDBPhase4`) 선택
3. `Sources` 버튼 항목 선택
4. `src/main/resources`를 `Resources`로 설정

## 8. 프로젝트 빌드 (이후 실행때는 필요 없음)
1. `Build → Build Project`
2. `Build → Build Artifacts... → ProjectDBPhase4:war exploded > → Build`

## 9. (편의성) Run Configuration 추가
1. 실행 버튼 옆 `Edit Configurations...`
2. `'+'(Add New Configuration) → Tomcat Server → Local` 혹은 기존거 수정
3. 해당 configuration(예: `Tomcat 10.1.48`) 선택
4. `Deployment → '+' → Artifact...`
5. `Application context`를 `/ProjectDBPhase4`로 변경



---
마지막 수정: 2025-12-04
