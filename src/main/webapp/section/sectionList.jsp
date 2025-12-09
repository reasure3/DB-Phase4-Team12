<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="com.team12.auction.dao.SectionDAO" %>
<%@ page import="com.team12.auction.model.dto.SectionSearchResult" %>
<%@ include file="/auth/loginCheck.jsp" %>
<%
    request.setCharacterEncoding("UTF-8");
    String studentName = (String) session.getAttribute("studentName");
    String keyword = request.getParameter("keyword");
    String department = request.getParameter("department");

    String successMessage = (String) session.getAttribute("successMessage");
    String errorMessage = (String) session.getAttribute("errorMessage");
    if (successMessage != null) session.removeAttribute("successMessage");
    if (errorMessage != null) session.removeAttribute("errorMessage");

    List<SectionSearchResult> sections = new ArrayList<>();
    try {
        SectionDAO sectionDAO = new SectionDAO();
        sections = sectionDAO.searchSections(keyword, department);
    } catch (SQLException e) {
        e.printStackTrace();
        errorMessage = "강의 목록을 불러오는 중 오류가 발생했습니다.";
    }

    String currentUrl = request.getRequestURI();
    if (request.getQueryString() != null) {
        currentUrl += "?" + request.getQueryString();
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>강의 조회 - 수강신청 경매 시스템</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
</head>
<body class="main-page">
<div class="container">
    <div class="header">
        <h1>강의 조회</h1>
        <div class="user-info">
            <span><strong><%= studentName %></strong>님</span>
            <a href="<%=request.getContextPath()%>/main.jsp" class="logout-btn">메인으로</a>
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

        <form class="page-actions" method="get" action="<%=request.getRequestURI()%>">
            <div class="filters">
                <input type="text" name="keyword" placeholder="강의명, 교수명 검색" value="<%= keyword != null ? keyword : "" %>">
                <select name="department">
                    <option value="" <%= (department == null || department.isEmpty()) ? "selected" : "" %>>학과 전체</option>
                    <option value="컴퓨터공학과" <%= "컴퓨터공학과".equals(department) ? "selected" : "" %>>컴퓨터공학과</option>
                    <option value="경영학과" <%= "경영학과".equals(department) ? "selected" : "" %>>경영학과</option>
                    <option value="경제학과" <%= "경제학과".equals(department) ? "selected" : "" %>>경제학과</option>
                </select>
                <button class="btn-primary" type="submit">검색</button>
            </div>
            <a href="<%=request.getContextPath()%>/basket/myBasket.jsp" class="btn-secondary">수강꾸러미 보기</a>
        </form>

        <table class="data-table">
            <thead>
            <tr>
                <th>강의코드</th>
                <th>분반</th>
                <th>강의명</th>
                <th>교수</th>
                <th>학과</th>
                <th>학점</th>
                <th>정원</th>
                <th>잔여석</th>
                <th>수강꾸러미</th>
            </tr>
            </thead>
            <tbody>
            <% if (sections.isEmpty()) { %>
                <tr>
                    <td colspan="9" style="text-align: center;">조건에 맞는 강의가 없습니다.</td>
                </tr>
            <% } else { %>
                <% for (SectionSearchResult item : sections) { %>
                    <tr>
                        <td><%= item.getCourseId() %></td>
                        <td><%= item.getSectionNumber() %>분반</td>
                        <td><%= item.getCourseName() %></td>
                        <td><%= item.getProfessor() %></td>
                        <td><%= item.getDepartment() %></td>
                        <td><%= item.getCredits() %>학점</td>
                        <td><%= item.getCapacity() %>명</td>
                        <td>
                            <% int remaining = item.getRemainingSeats(); %>
                            <% String badgeClass = remaining > 5 ? "success" : (remaining > 0 ? "warning" : "danger"); %>
                            <span class="badge <%= badgeClass %>"><%= remaining %></span>
                        </td>
                        <td>
                            <form method="post" action="<%=request.getContextPath()%>/basket/add" class="inline-form">
                                <input type="hidden" name="sectionId" value="<%= item.getSectionId() %>">
                                <input type="hidden" name="returnUrl" value="<%= currentUrl %>">
                                <button class="<%= item.getRemainingSeats() > 0 ? "btn-primary" : "btn-disabled" %>" type="submit" <%= item.getRemainingSeats() == 0 ? "disabled" : "" %>>
                                    <%= item.getRemainingSeats() == 0 ? "마감" : "담기" %>
                                </button>
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
