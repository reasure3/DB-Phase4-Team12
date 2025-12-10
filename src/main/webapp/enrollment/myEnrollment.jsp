<%@ page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.team12.auction.model.dto.EnrollmentDetail"%>
<%@ include file="/auth/loginCheck.jsp"%>
<%
    request.setCharacterEncoding("UTF-8");
    String studentName = (String) session.getAttribute("studentName");

    @SuppressWarnings("unchecked")
    List<EnrollmentDetail> enrollments =
            (List<EnrollmentDetail>) request.getAttribute("enrollments");
    if (enrollments == null) {
        enrollments = new ArrayList<>();
    }

    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>나의 등록 - 수강신청 경매 시스템</title>
<link rel="stylesheet"
        href="<%=request.getContextPath()%>/assets/css/style.css">
</head>
<body class="main-page">
        <div class="container">
                <div class="header">
                        <h1>나의 등록 목록</h1>
                        <div class="user-info">
                                <span><strong><%= studentName %></strong>님</span> <a
                                        href="<%=request.getContextPath()%>/main.jsp" class="logout-btn">메인으로</a>
                                <a href="<%=request.getContextPath()%>/auth/logout" class="logout-btn">로그아웃</a>
                        </div>
                </div>

                <div class="content">
                        <% if (errorMessage != null) { %>
                        <div class="error-message"><%= errorMessage %></div>
                        <% } %>

                        <div class="page-actions">
                                <div>
                                <p class="summary-text">
                                                현재 등록된 총 건수: <strong><%= enrollments.size() %>건</strong>
                                        </p>
                                </div>
                                <div class="action-buttons">
                                        <a href="<%=request.getContextPath()%>/section/list" class="btn-secondary">강의 조회</a>
                                        <a href="<%=request.getContextPath()%>/basket/list" class="btn-secondary">수강꾸러미</a>
                                </div>
                        </div>

                        <table class="data-table">
                                <thead>
                                        <tr>
                                                <th>분반 ID</th>
                                                <th>강의코드</th>
                                                <th>분반</th>
                                                <th>강의명</th>
                                                <th>교수</th>
                                                <th>강의실</th>
                                                <th>정원</th>
                                                <th>신청 경로</th>
                                                <th>사용 포인트</th>
                                        </tr>
                                </thead>
                                <tbody>
                                        <% if (enrollments.isEmpty()) { %>
                                        <tr>
                                                <td colspan="9" style="text-align: center;">등록된 강의가 없습니다.</td>
                                        </tr>
                                        <% } else { %>
                                        <% for (EnrollmentDetail enrollment : enrollments) { %>
                                        <tr>
                                                <td><%= enrollment.getSectionId() %></td>
                                                <td><%= enrollment.getCourseId() %></td>
                                                <td><%= enrollment.getSectionNumber() %>분반</td>
                                                <td><%= enrollment.getCourseName() %></td>
                                                <td><%= enrollment.getProfessor() %></td>
                                                <td><%= enrollment.getClassroom() %></td>
                                                <td><%= enrollment.getCapacity() %>명</td>
                                                <td><%= enrollment.getEnrollmentSource() %></td>
                                                <td><%= enrollment.getPointsUsed() %>점</td>
                                        </tr>
                                        <% } %>
                                        <% } %>
                                </tbody>
                        </table>
                </div>
        </div>
</body>
</html>
