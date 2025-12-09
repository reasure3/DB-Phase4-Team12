<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/auth/loginCheck.jsp" %>
<%
    String error = (String) request.getAttribute("error");

    HttpSession userSession = request.getSession(false);
    if (userSession == null || userSession.getAttribute("studentId") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>비밀번호 변경 - 수강신청 경매 시스템</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">

</head>
<body>
<div class="header">
    <h1>비밀번호 변경</h1>
    <div>
        <a href="mypage" class="btn-secondary">내 정보</a>
        <a href="main.jsp" class="btn-secondary">메인으로</a>
    </div>
</div>

<%
    String errorMessage = (String) request.getAttribute("errorMessage");
    if (errorMessage != null) {
%>
<div class="error-message">
    <%= errorMessage %>
</div>
<% } %>

<div class="container">
    <div class="form-card">
        <h2>비밀번호 변경</h2>

        <div class="info-box">
            <strong>비밀번호 규칙</strong>
            <ul>
                <li>최소 4자 이상</li>
                <li>현재 비밀번호와 달라야 합니다</li>
            </ul>
        </div>

        <% if (error != null) { %>
        <div class="error-message">
            <%= error %>
        </div>
        <% } %>

        <form method="post" action="changePassword">
            <div class="form-group">
                <label for="currentPassword">현재 비밀번호 *</label>
                <input type="password" id="currentPassword" name="currentPassword" required>
            </div>

            <div class="form-group">
                <label for="newPassword">새 비밀번호 *</label>
                <input type="password" id="newPassword" name="newPassword" required>
            </div>

            <div class="form-group">
                <label for="confirmPassword">새 비밀번호 확인 *</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required>
            </div>

            <div class="button-group">
                <button type="submit" class="btn btn-primary">변경</button>
                <a href="<%=request.getContextPath()%>/mypage" class="btn btn-secondary">취소</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>
