<%@ page import="com.team12.auction.model.entity.Student" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/auth/loginCheck.jsp" %>
<%
    Student student = (Student) request.getAttribute("student");
    String error = (String) request.getAttribute("errorMessage");

    if (student == null) {
        response.sendRedirect("/auth/login");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>내 정보 수정 - 수강신청 경매 시스템</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
</head>
<body>
<div class="header">
    <h1>내 정보 수정</h1>
    <div>
        <a href="<%=request.getContextPath()%>/mypage" class="btn-secondary">내 정보</a>
        <a href="<%=request.getContextPath()%>/main.jsp" class="btn-secondary">메인으로</a>
    </div>
</div>

<div class="container">
    <div class="form-card">
        <h2>프로필 수정</h2>

        <% if (error != null) { %>
        <div class="error-message">
            <%= error %>
        </div>
        <% } %>

        <form method="post" action="editProfile">
            <div class="form-group">
                <label>학번</label>
                <input type="text" value="<%= student.getStudentId() %>" readonly>
                <div class="info-text">학번은 변경할 수 없습니다.</div>
            </div>

            <div class="form-group">
                <label for="name">이름 *</label>
                <input type="text" id="name" name="name" value="<%= student.getName() %>" required>
            </div>

            <div class="form-group">
                <label for="department">학과 *</label>
                <input type="text" id="department" name="department" value="<%= student.getDepartment() %>" required>
            </div>

            <div class="form-group">
                <label for="grade">학년 *</label>
                <select id="grade" name="grade" required>
                    <option value="1" <%= student.getGrade() == 1 ? "selected" : "" %>>1학년</option>
                    <option value="2" <%= student.getGrade() == 2 ? "selected" : "" %>>2학년</option>
                    <option value="3" <%= student.getGrade() == 3 ? "selected" : "" %>>3학년</option>
                    <option value="4" <%= student.getGrade() == 4 ? "selected" : "" %>>4학년</option>
                </select>
            </div>

            <div class="button-group">
                <button type="submit" class="btn btn-primary">저장</button>
                <a href="<%=request.getContextPath()%>/mypage" class="btn btn-secondary">취소</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>
