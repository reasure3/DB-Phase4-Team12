<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/auth/loginCheck.jsp"%>
<%
    String studentName = (String) session.getAttribute("studentName");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>메인 - 수강신청 경매 시스템</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/assets/css/style.css">
</head>
<body class="main-page">
<div class="container">
    <div class="header">
        <h1>수강신청 경매 시스템</h1>
        <div class="user-info">
            <span><strong><%= studentName %></strong>님 환영합니다!</span>
            <a href="<%=request.getContextPath()%>/mypage" class="logout-btn">내 정보</a>
            <a href="<%=request.getContextPath()%>/auth/logout" class="logout-btn">로그아웃</a>
        </div>
    </div>

		<div class="content">
			<div class="welcome-message">
				<h2>환영합니다!</h2>
				<p>수강신청 경매 시스템에 로그인하셨습니다. 아래 메뉴에서 원하는 기능을 선택하세요.</p>
			</div>

			<div class="menu-grid">
				<a href="<%=request.getContextPath()%>/section/list"
					class="menu-card">
					<h3>강의 조회</h3>
					<p>전체 강의를 조회하세요</p>
				</a> <a href="<%=request.getContextPath()%>/basket/list"
					class="menu-card">
					<h3>수강꾸러미</h3>
					<p>나의 수강꾸러미를 관리하세요</p>
                                </a> <a href="<%=request.getContextPath()%>/auction/list"
                                        class="menu-card">
                                        <h3>경매</h3>
                                        <p>경매를 관리하세요</p>
				</a> <a href="<%=request.getContextPath()%>/enrollment/myEnrollment.jsp"
					class="menu-card">
					<h3>등록 조회</h3>
					<p>내가 수강중인 강의들을 확인하세요</p>
				</a>
			</div>
		</div>
        </div>
</body>
</html>
