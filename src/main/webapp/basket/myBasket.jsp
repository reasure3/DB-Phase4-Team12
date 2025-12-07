<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/auth/loginCheck.jsp" %>
<%
    String studentName = (String) session.getAttribute("studentName");
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
        <div class="page-actions">
            <div>
                <p class="summary-text">현재 담긴 강의: <strong>3과목</strong> / 총 학점: <strong>9학점</strong></p>
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
                <th>강의명</th>
                <th>교수</th>
                <th>학점</th>
                <th>시간/장소</th>
                <th>상태</th>
                <th>관리</th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td>CS101</td>
                <td>자료구조</td>
                <td>김교수</td>
                <td>3학점</td>
                <td>월/수 09:00 - 10:30 · 공학관 201</td>
                <td><span class="badge success">경매 진행중</span></td>
                <td>
                    <button class="btn-secondary" type="button">취소</button>
                    <button class="btn-primary" type="button">경매 참여</button>
                </td>
            </tr>
            <tr>
                <td>BUS210</td>
                <td>마케팅 원론</td>
                <td>이교수</td>
                <td>3학점</td>
                <td>화/목 10:00 - 11:30 · 경영관 402</td>
                <td><span class="badge warning">경매 대기</span></td>
                <td>
                    <button class="btn-secondary" type="button">취소</button>
                    <button class="btn-primary" type="button">경매 참여</button>
                </td>
            </tr>
            <tr>
                <td>ECON310</td>
                <td>계량경제학</td>
                <td>박교수</td>
                <td>3학점</td>
                <td>금 13:00 - 16:00 · 사회관 105</td>
                <td><span class="badge danger">마감</span></td>
                <td>
                    <button class="btn-secondary" type="button">취소</button>
                    <button class="btn-disabled" type="button" disabled>경매 종료</button>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
