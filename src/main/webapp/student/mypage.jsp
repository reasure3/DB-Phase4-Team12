<%@ page import="com.team12.auction.model.entity.Student" %>
<%@ page import="com.team12.auction.model.entity.Log" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/auth/loginCheck.jsp" %>
<%
    Student student = (Student) request.getAttribute("student");
    Integer currentCredits = (Integer) request.getAttribute("currentCredits");
    Integer currentPoints = (Integer) request.getAttribute("currentPoints");
    List<Log> recentLogs = (List<Log>) request.getAttribute("recentLogs");

    if (student == null) {
        response.sendRedirect("auth/login.jsp");
        return;
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
%>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>내 정보 - 수강신청 경매 시스템</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
</head>
<body>
<div class="header">
    <h1>내 정보</h1>
    <a href="<%=request.getContextPath()%>/main.jsp" class="btn-secondary">메인으로</a>
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

<div class="container">
    <!-- Basic Information -->
    <div class="info-card">
        <h2>기본 정보</h2>
        <div class="info-grid">
            <div class="info-item">
                <div class="info-label">학번</div>
                <div class="info-value"><%= student.getStudentId() %>
                </div>
            </div>
            <div class="info-item">
                <div class="info-label">이름</div>
                <div class="info-value"><%= student.getName() %>
                </div>
            </div>
            <div class="info-item">
                <div class="info-label">학과</div>
                <div class="info-value"><%= student.getDepartment() %>
                </div>
            </div>
            <div class="info-item">
                <div class="info-label">학년</div>
                <div class="info-value"><%= student.getGrade() %>학년</div>
            </div>
        </div>

        <div class="action-buttons">
            <a href="<%=request.getContextPath()%>/editProfile" class="btn btn-primary">내 정보 수정</a>
            <a href="<%=request.getContextPath()%>/auth/changePassword" class="btn btn-warning">비밀번호 변경</a>
        </div>
    </div>

    <!-- Credits Information -->
    <div class="info-card">
        <h2>학점 정보</h2>
        <div class="info-grid">
            <div class="info-item">
                <div class="info-label">현재 수강 학점</div>
                <div class="info-value"><%= currentCredits %> / <%= student.getMaxCredits() %> 학점</div>
            </div>
            <div class="info-item">
                <div class="info-label">보유 포인트</div>
                <div class="info-value"><%= currentPoints %> / <%= student.getMaxPoint() %> P</div>
            </div>
        </div>
    </div>

    <!-- Activity Logs -->
    <div class="info-card">
        <h2>최근 활동 내역</h2>
        <% if (recentLogs != null && !recentLogs.isEmpty()) { %>
        <table class="log-table">
            <thead>
            <tr>
                <th>활동 유형</th>
                <th>상세 내용</th>
                <th>시간</th>
            </tr>
            </thead>
            <tbody>
            <% for (Log log : recentLogs) {
                String actionClass = "action-other";
                if (log.getActionType().contains("LOGIN")) {
                    actionClass = "action-login";
                } else if (log.getActionType().contains("LOGOUT")) {
                    actionClass = "action-logout";
                } else if (log.getActionType().contains("BID")) {
                    actionClass = "action-bid";
                } else if (log.getActionType().contains("ENROLL")) {
                    actionClass = "action-enroll";
                }
            %>
            <tr>
                <td>
                                <span class="action-type <%= actionClass %>">
                                    <%= log.getActionType() %>
                                </span>
                </td>
                <td><%= log.getDetails() != null ? log.getDetails() : "-" %>
                </td>
                <td><%= dateFormat.format(log.getTimestamp()) %>
                </td>
            </tr>
            <% } %>
            </tbody>
        </table>
        <% } else { %>
        <div class="no-logs">
            아직 활동 내역이 없습니다.
        </div>
        <% } %>
    </div>
</div>
</body>
</html>
