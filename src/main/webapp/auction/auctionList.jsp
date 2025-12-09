<%@ page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.team12.auction.model.dto.AuctionDetail"%>
<%@ page import="com.team12.auction.model.entity.Bid"%>
<%@ include file="/auth/loginCheck.jsp"%>
<%
    request.setCharacterEncoding("UTF-8");
    String studentName = (String) session.getAttribute("studentName");

    @SuppressWarnings("unchecked")
    Map<AuctionDetail, Bid> auctionBidMap =
            (Map<AuctionDetail, Bid>) request.getAttribute("auctionBidMap");
    if (auctionBidMap == null) {
        auctionBidMap = new LinkedHashMap<>();
    }

    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>나의 경매 - 수강신청 경매 시스템</title>
<link rel="stylesheet"
        href="<%=request.getContextPath()%>/assets/css/style.css">
</head>
<body class="main-page">
        <div class="container">
                <div class="header">
                        <h1>나의 경매</h1>
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
                                                참여한 경매 수: <strong><%= auctionBidMap.size() %>개</strong>
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
                                                <th>경매 ID</th>
                                                <th>강의코드</th>
                                                <th>분반</th>
                                                <th>강의명</th>
                                                <th>교수</th>
                                                <th>학점</th>
                                                <th>상태</th>
                                                <th>수강 가능 인원</th>
                                                <th>시작일</th>
                                                <th>종료일</th>
                                                <th>내 입찰 금액</th>
                                                <th>입찰 결과</th>
                                        </tr>
                                </thead>
                                <tbody>
                                        <% if (auctionBidMap.isEmpty()) { %>
                                        <tr>
                                                <td colspan="12" style="text-align: center;">참여한 경매가 없습니다.</td>
                                        </tr>
                                        <% } else { %>
                                        <% for (Map.Entry<AuctionDetail, Bid> entry : auctionBidMap.entrySet()) { %>
                                        <% AuctionDetail auction = entry.getKey(); %>
                                        <% Bid myBid = entry.getValue(); %>
                                        <% String status = auction.getStatus() != null ? auction.getStatus() : "UNKNOWN"; %>
                                        <% String statusClass = "ACTIVE".equalsIgnoreCase(status) ? "success"
                                                : ("COMPLETED".equalsIgnoreCase(status) ? "warning" : "danger"); %>
                                        <% int bidAmount = myBid != null ? myBid.getBidAmount() : 0; %>
                                        <% String bidResult = myBid != null && "Y".equalsIgnoreCase(myBid.getIsSuccessful()) ? "합격"
                                                : "미선정"; %>
                                        <% String bidClass = "합격".equals(bidResult) ? "success" : "danger"; %>
                                        <tr>
                                                <td><%= auction.getAuctionId() %></td>
                                                <td><%= auction.getCourseId() %></td>
                                                <td><%= auction.getSectionNumber() %>분반</td>
                                                <td><%= auction.getCourseName() %></td>
                                                <td><%= auction.getProfessor() %></td>
                                                <td><%= auction.getCredits() %>학점</td>
                                                <td><span class="badge <%= statusClass %>"><%= status %></span></td>
                                                <td><%= auction.getAvailableSlots() %>명</td>
                                                <td><%= auction.getStartTime() %></td>
                                                <td><%= auction.getEndTime() %></td>
                                                <td><%= bidAmount %>점</td>
                                                <td><span class="badge <%= bidClass %>"><%= bidResult %></span></td>
                                        </tr>
                                        <% } %>
                                        <% } %>
                                </tbody>
                        </table>
                </div>
        </div>
</body>
</html>
