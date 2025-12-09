<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isErrorPage="true" %>
<%
    String error = (String) request.getAttribute("errorMessage");
    String errorMessage = (error != null) ? error : "알 수 없는 오류가 발생했습니다.";

    // Get exception details if available
    Exception ex = (Exception) request.getAttribute("exception");
    String exceptionMessage = (ex != null) ? ex.getMessage() : null;

    // Get referring page if available
    String referrer = request.getHeader("referer");

    HttpSession userSession = request.getSession(false);
    boolean isLoggedIn = (userSession != null && userSession.getAttribute("studentId") != null);
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>오류 - 수강신청 경매 시스템</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
</head>
<body>
<div class="header">
    <h1>⚠️ 오류가 발생했습니다</h1>
</div>

<div class="container">
    <div class="error-card">
        <div class="error-icon">❌</div>

        <h2 class="error-title">요청을 처리할 수 없습니다</h2>

        <p class="error-message">
            <%= errorMessage %>
        </p>

        <% if (exceptionMessage != null && !exceptionMessage.trim().isEmpty()) { %>
        <div class="error-details">
            <strong>오류 상세:</strong><br>
            <code><%= exceptionMessage %></code>
        </div>
        <% } %>

        <div class="button-group">
            <% if (referrer != null && !referrer.isEmpty()) { %>
            <a href="javascript:history.back()" class="btn btn-secondary">이전 페이지</a>
            <% } %>

            <% if (isLoggedIn) { %>
            <a href="<%=request.getContextPath()%>/main.jsp" class="btn btn-primary">메인으로</a>
            <a href="<%=request.getContextPath()%>/mypage" class="btn btn-secondary">내 정보</a>
            <% } else { %>
            <a href="<%=request.getContextPath()%>/auth/login" class="btn btn-primary">로그인</a>
            <% } %>
        </div>

        <div class="help-text">
            <h3>💡 문제 해결 방법</h3>
            <ul>
                <li>페이지를 새로고침해보세요</li>
                <li>로그인이 필요한 페이지인지 확인해보세요</li>
                <li>입력한 정보가 올바른지 확인해보세요</li>
                <li>문제가 계속되면 관리자에게 문의하세요</li>
            </ul>
        </div>
    </div>
</div>
</body>
</html>
