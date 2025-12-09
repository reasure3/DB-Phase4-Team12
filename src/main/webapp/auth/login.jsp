<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>로그인 - 수강신청 경매 시스템</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/assets/css/style.css">
</head>
<body class="login-page">
	<div class="login-container">
		<div class="login-header">
			<h1>수강신청 경매</h1>
			<p>학번과 비밀번호를 입력하세요</p>
		</div>

		<%
    String successMessage = (String) session.getAttribute("successMessage");
        if (successMessage != null) {
        	session.removeAttribute("successMessage");
    %>
		<div class="success-message">
			<%= successMessage %>
		</div>
		<% } %>

		<%
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (errorMessage != null) {
    %>
		<div class="error-message">
			<%= errorMessage %>
		</div>
		<% } %>

		<form action="<%=request.getContextPath()%>/auth/login" method="post">
			<div class="form-group">
				<label for="studentId">학번</label> <input type="text" id="studentId"
					name="studentId" placeholder="학번을 입력하세요"
					value="<%= request.getAttribute("studentId") != null ? request.getAttribute("studentId") : "" %>"
					required autofocus>
			</div>

			<div class="form-group">
				<label for="password">비밀번호</label> <input type="password"
					id="password" name="password" placeholder="비밀번호를 입력하세요" required>
			</div>

			<button type="submit" class="btn-login">로그인</button>

			<div class="signup-link">
				계정이 없으신가요? <a href="<%=request.getContextPath()%>/auth/signup">회원가입</a>
			</div>
		</form>

		<div class="login-footer">
			<p>Team 12 Course Auction System</p>
		</div>
	</div>
</body>
</html>