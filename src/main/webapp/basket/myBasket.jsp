<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="com.team12.auction.dao.BasketDAO" %>
<%@ page import="com.team12.auction.model.dto.BasketItemDetail" %>
<%@ include file="/auth/loginCheck.jsp" %>
<%
    request.setCharacterEncoding("UTF-8");
    String studentName = (String) session.getAttribute("studentName");
    int studentId = (Integer) session.getAttribute("studentId");

    String successMessage = (String) session.getAttribute("successMessage");
    String errorMessage = (String) session.getAttribute("errorMessage");
    if (successMessage != null) session.removeAttribute("successMessage");
    if (errorMessage != null) session.removeAttribute("errorMessage");

    BasketDAO basketDAO = new BasketDAO();
    List<BasketItemDetail> basketItems = new ArrayList<>();
    try {
        basketDAO.ensureBasketExists(studentId);
        basketItems = basketDAO.getMyBasket(studentId);
    } catch (SQLException e) {
        e.printStackTrace();
        errorMessage = "수강꾸러미 정보를 불러오는 중 오류가 발생했습니다.";
    }

    int totalCredits = 0;
    for (BasketItemDetail item : basketItems) {
        totalCredits += item.getCredits();
    }

    String currentUrl = request.getRequestURI();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>수강꾸러미 - 수강신청 경매 시스템</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
</head>
<body class="main-page">
<div class="container">
    <div class="header">
        <h1>수강꾸러미</h1>
        <div class="user-info">
            <span><strong><%= studentName %></strong>님</span>
            <a href="<%=request.getContextPath()%>/section/sectionList.jsp" class="logout-btn">강의조회</a>
            <a href="<%=request.getContextPath()%>/auth/logout" class="logout-btn">로그아웃</a>
        </div>
    </div>

    <div class="content">
        <% if (successMessage != null) { %>
            <div class="success-message"><%= successMessage %></div>
        <% } %>
        <% if (errorMessage != null) { %>
            <div class="error-message"><%= errorMessage %></div>
        <% } %>

        <div class="page-actions">
            <div>
                <p class="summary-text">현재 담긴 강의: <strong><%= basketItems.size() %>과목</strong> / 총 학점: <strong><%= totalCredits %>학점</strong></p>
            </div>
            <div class="action-buttons">
                <a href="<%=request.getContextPath()%>/auction/auctionList.jsp" class="btn-primary">경매로 이동</a>
                <a href="<%=request.getContextPath()%>/main.jsp" class="btn-secondary">메인으로</a>
            </div>
        </div>

        <table class="data-table">
            <thead>
            <tr>
                <th>강의코드</th>
                <th>분반</th>
                <th>강의명</th>
                <th>교수</th>
                <th>학점</th>
                <th>강의실</th>
                <th>상태</th>
                <th>관리</th>
            </tr>
            </thead>
            <tbody>
            <% if (basketItems.isEmpty()) { %>
                <tr>
                    <td colspan="8" style="text-align:center;">수강꾸러미에 담긴 분반이 없습니다.</td>
                </tr>
            <% } else { %>
                <% for (BasketItemDetail item : basketItems) { %>
                    <tr>
                        <td><%= item.getCourseId() %></td>
                        <td><%= item.getSectionNumber() %>분반</td>
                        <td><%= item.getCourseName() %></td>
                        <td><%= item.getProfessor() %></td>
                        <td><%= item.getCredits() %>학점</td>
                        <td><%= item.getClassroom() %></td>
                        <td>
                            <% String status = item.getStatus() != null ? item.getStatus() : "PENDING"; %>
                            <% String badgeClass = "PENDING".equalsIgnoreCase(status) ? "warning" : ("SUCCESS".equalsIgnoreCase(status) ? "success" : "danger"); %>
                            <span class="badge <%= badgeClass %>"><%= status %></span>
                            <% if (item.getReason() != null && !item.getReason().isEmpty()) { %>
                                <div class="helper-text"><%= item.getReason() %></div>
                            <% } %>
                        </td>
                        <td>
                            <form method="post" action="<%=request.getContextPath()%>/basket/remove" class="inline-form">
                                <input type="hidden" name="sectionId" value="<%= item.getSectionId() %>">
                                <input type="hidden" name="returnUrl" value="<%= currentUrl %>">
                                <button class="btn-secondary" type="submit">취소</button>
                            </form>
                        </td>
                    </tr>
                <% } %>
            <% } %>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
