<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.team12.auction.model.dto.AuctionDetail"%>
<%@ page import="com.team12.auction.model.dto.BidDetail"%>
<%@ include file="/auth/loginCheck.jsp"%>
<%
    request.setCharacterEncoding("UTF-8");
    String studentName = (String) session.getAttribute("studentName");

    AuctionDetail auction = (AuctionDetail) request.getAttribute("auction");
    @SuppressWarnings("unchecked")
    List<BidDetail> topBids = (List<BidDetail>) request.getAttribute("topBids");
    if (topBids == null) {
        topBids = new ArrayList<>();
    }

    Boolean canBid = (Boolean) request.getAttribute("canBid");
    Boolean biddingWindowOpen = (Boolean) request.getAttribute("biddingWindowOpen");
    Boolean alreadyBid = (Boolean) request.getAttribute("alreadyBid");
    Boolean inBasket = (Boolean) request.getAttribute("inBasket");

    if (canBid == null) canBid = false;
    if (biddingWindowOpen == null) biddingWindowOpen = false;
    if (alreadyBid == null) alreadyBid = false;
    if (inBasket == null) inBasket = false;

    String errorMessage = (String) request.getAttribute("errorMessage");
    String successMessage = (String) request.getAttribute("successMessage");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>경매 입찰 - 수강신청 경매 시스템</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/style.css">
</head>
<body class="main-page">
    <div class="container">
        <div class="header">
            <h1>경매 입찰</h1>
            <div class="user-info">
                <span><strong><%= studentName %></strong>님</span>
                <a href="<%=request.getContextPath()%>/auction/list" class="logout-btn">나의 경매</a>
                <a href="<%=request.getContextPath()%>/auth/logout" class="logout-btn">로그아웃</a>
            </div>
        </div>

        <div class="content">
            <% if (errorMessage != null) { %>
                <div class="error-message"><%= errorMessage %></div>
            <% } %>
            <% if (successMessage != null) { %>
                <div class="success-message"><%= successMessage %></div>
            <% } %>

            <% if (auction != null) { %>
            <div class="page-actions">
                <div>
                    <p class="summary-text">
                        경매 ID: <strong><%= auction.getAuctionId() %></strong><br>
                        강의: <strong><%= auction.getCourseId() %> - <%= auction.getCourseName() %></strong> / <%= auction.getSectionNumber() %>분반 / 교수: <%= auction.getProfessor() %><br>
                        수강 가능 인원: <strong><%= auction.getAvailableSlots() %>명</strong><br>
                        입찰 기간: <%= auction.getStartTime() %> ~ <%= auction.getEndTime() %><br>
                        상태: <%= auction.getStatus() %>
                    </p>
                </div>
                <div class="action-buttons">
                    <a href="<%=request.getContextPath()%>/section/list" class="btn-secondary">강의 조회</a>
                    <a href="<%=request.getContextPath()%>/basket/list" class="btn-secondary">수강꾸러미</a>
                </div>
            </div>

            <h3>현재 상위 입찰 (익명, 최대 <%= auction.getAvailableSlots() %>명)</h3>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>순위</th>
                        <th>입찰 포인트</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        for (int i = 0; i < auction.getAvailableSlots(); i++) {
                            if (i < topBids.size()) {
                                BidDetail bid = topBids.get(i);
                    %>
                        <tr>
                            <td><%= (i + 1) %>위</td>
                            <td><%= bid.getBidAmount() %>점</td>
                        </tr>
                    <%  } else { %>
                        <tr>
                            <td><%= (i + 1) %>위</td>
                            <td>비어 있음</td>
                        </tr>
                    <%  }
                        }
                    %>
                </tbody>
            </table>

            <div class="form-card">
                <h3>입찰하기</h3>
                <p class="summary-text">
                    <% if (!inBasket) { %>
                        수강꾸러미에 담은 분반만 입찰할 수 있습니다.
                    <% } else if (!biddingWindowOpen) { %>
                        현재 입찰 가능 기간이 아닙니다.
                    <% } else if (!"ACTIVE".equalsIgnoreCase(auction.getStatus())) { %>
                        비활성화된 경매입니다.
                    <% } else if (alreadyBid) { %>
                        이미 입찰을 완료했습니다.
                    <% } else { %>
                        현재 시점 기준, 상위 <%= auction.getAvailableSlots() %>위 안에 들어야 입찰이 가능합니다.
                    <% } %>
                </p>

                <form method="post" action="<%=request.getContextPath()%>/auction/bid">
                    <input type="hidden" name="auctionId" value="<%= auction.getAuctionId() %>">
                    <label for="bidAmount">입찰 포인트</label>
                    <input type="number" id="bidAmount" name="bidAmount" min="1" required placeholder="포인트를 입력하세요" <%= canBid ? "" : "disabled" %>>
                    <button type="submit" class="btn-primary" <%= canBid ? "" : "disabled" %>>입찰하기</button>
                </form>
            </div>
            <% } %>
        </div>
    </div>
</body>
</html>
