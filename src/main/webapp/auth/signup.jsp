<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>회원가입 - 수강신청 경매 시스템</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/assets/css/style.css">
</head>
<body class="login-page">
	<div class="signup-container">
		<div class="login-header">
			<h1>회원가입</h1>
			<p>정보를 입력하여 가입하세요</p>
		</div>

		<%
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (errorMessage != null) {
    %>
		<div class="error-message">
			<%= errorMessage %>
		</div>
		<% } %>

		<form action="<%=request.getContextPath()%>/auth/signup" method="post">
			<div class="form-group">
				<label for="studentId">학번 *</label> <input type="text"
					id="studentId" name="studentId" placeholder="학번을 입력하세요"
					value="<%= request.getAttribute("studentId") != null ? request.getAttribute("studentId") : "" %>"
					required autofocus>
			</div>

			<div class="form-group">
				<label for="name">이름 *</label> <input type="text" id="name"
					name="name" placeholder="이름을 입력하세요"
					value="<%= request.getAttribute("name") != null ? request.getAttribute("name") : "" %>"
					required>
			</div>

			<div class="form-group">
				<label for="department">학과 *</label> <input type="text"
					id="department" name="department" placeholder="학과를 입력하세요"
					value="<%= request.getAttribute("department") != null ? request.getAttribute("department") : "" %>"
					required>
			</div>

			<div class="form-group">
				<label for="grade">학년 *</label> <select id="grade" name="grade"
					required>
					<option value="">선택하세요</option>
					<option value="1"
						<%= "1".equals(request.getAttribute("grade")) ? "selected" : "" %>>1학년</option>
					<option value="2"
						<%= "2".equals(request.getAttribute("grade")) ? "selected" : "" %>>2학년</option>
					<option value="3"
						<%= "3".equals(request.getAttribute("grade")) ? "selected" : "" %>>3학년</option>
					<option value="4"
						<%= "4".equals(request.getAttribute("grade")) ? "selected" : "" %>>4학년</option>
				</select>
			</div>

			<div class="form-group">
				<label for="password">비밀번호 *</label> <input type="password"
					id="password" name="password" placeholder="비밀번호를 입력하세요" required>
			</div>

			<div class="form-group">
				<label for="passwordConfirm">비밀번호 확인 *</label> <input
					type="password" id="passwordConfirm" name="passwordConfirm"
					placeholder="비밀번호를 다시 입력하세요" required>
			</div>

			<button type="submit" class="btn-login">회원가입</button>

			<div class="signup-link">
				이미 계정이 있으신가요? <a href="<%=request.getContextPath()%>/auth/login">로그인</a>
			</div>
		</form>

		<div class="login-footer">
			<p>2025 Team 12 Course Auction System</p>
		</div>
	</div>
</body>
</html>