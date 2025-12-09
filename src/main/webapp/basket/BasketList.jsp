<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.team12.auction.model.dto.BasketItemDetail"%>
<%@ include file="/auth/loginCheck.jsp"%>
<%
    String studentName = (String) session.getAttribute("studentName");

    @SuppressWarnings("unchecked")
    List<BasketItemDetail> basketItems =
            (List<BasketItemDetail>) request.getAttribute("basketItems");
    if (basketItems == null) {
        basketItems = new ArrayList<>();
    }

    Integer totalCreditsObj = (Integer) request.getAttribute("totalCredits");
    int totalCredits = (totalCreditsObj != null) ? totalCreditsObj : 0;

    Integer maxCreditsObj = (Integer) request.getAttribute("maxCredits");
    int maxCredits = (maxCreditsObj != null) ? maxCreditsObj : 0;

    String successMessage = (String) request.getAttribute("successMessage");
    String errorMessage = (String) request.getAttribute("errorMessage");

    String currentUrl = request.getRequestURI();
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>수강꾸러미 - 수강신청 경매 시스템</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/assets/css/style.css">
</head>
<body class="main-page">
	<div class="container">
		<div class="header">
			<h1>수강꾸러미</h1>
			<div class="user-info">
				<span><strong><%= studentName %></strong>님</span> <a
					href="<%=request.getContextPath()%>/main.jsp" class="logout-btn">메인으로</a>
				<a href="<%=request.getContextPath()%>/auth/logout"
					class="logout-btn">로그아웃</a>
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
					<p class="summary-text">
						현재 담긴 강의 수: <strong><%=basketItems.size()%>과목</strong> ( <strong><%=totalCredits%></strong>
						/ <strong><%=maxCredits%></strong> 학점 )
					</p>
				</div>
				<div class="action-buttons">
					<a href="<%=request.getContextPath()%>/section/list"
						class="btn-secondary">강의 조회</a> <a
						href="<%=request.getContextPath()%>/auction/auctionList.jsp"
						class="btn-secondary">경매 조회</a>
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
						<th>정원</th>
						<th>담은 인원</th>
						<th>강의실</th>
						<th>상태</th>
						<th>관리</th>
					</tr>
				</thead>
				<tbody>
					<% if (basketItems.isEmpty()) { %>
					<tr>
						<td colspan="10" style="text-align: center;">수강꾸러미에 담긴 분반이
							없습니다.</td>
					</tr>
					<% } else { %>
					<% for (BasketItemDetail item : basketItems) { %>
					<tr>
						<td><%= item.getCourseId() %></td>
						<td><%= item.getSectionNumber() %>분반</td>
						<td><%= item.getCourseName() %></td>
						<td><%= item.getProfessor() %></td>
						<td><%= item.getCredits() %>학점</td>
						<td><%= item.getCapacity() %>명</td>
						<td>
							<% int basketCount = item.getBasketCount(); %> <% int capacity = item.getCapacity(); %>
							<% String countBadgeClass = basketCount < capacity ? "success" : (basketCount == capacity ? "warning" : "danger"); %>
							<span class="badge <%= countBadgeClass %>"><%= basketCount %>명</span>
						</td>
						<td><%= item.getClassroom() %></td>
						<td>
							<% String status = item.getStatus() != null ? item.getStatus() : "PENDING"; %>
							<% String badgeClass = "PENDING".equalsIgnoreCase(status) ? "warning" : ("SUCCESS".equalsIgnoreCase(status) ? "success" : "danger"); %>
							<span class="badge <%= badgeClass %>"><%= status %></span> <% if (item.getReason() != null && !item.getReason().isEmpty()) { %>
							<div class="helper-text"><%= item.getReason() %></div> <% } %>
						</td>
						<td>
							<form method="post"
								action="<%=request.getContextPath()%>/basket/remove"
								class="inline-form">
								<input type="hidden" name="sectionId"
									value="<%= item.getSectionId() %>"> <input
									type="hidden" name="returnUrl"
									value="<%= request.getContextPath() %>/basket/list">
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
