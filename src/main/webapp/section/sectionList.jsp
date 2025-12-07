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
        <div class="page-actions">
            <div class="filters">
                <input type="text" placeholder="강의명, 교수명 검색">
                <select>
                    <option value="">학과 전체</option>
                    <option>컴퓨터공학과</option>
                    <option>경영학과</option>
                    <option>경제학과</option>
                </select>
                <select>
                    <option value="">학년 전체</option>
                    <option>1학년</option>
                    <option>2학년</option>
                    <option>3학년</option>
                    <option>4학년</option>
                </select>
                <button class="btn-primary" type="button">검색</button>
            </div>
            <a href="<%=request.getContextPath()%>/basket/myBasket.jsp" class="btn-secondary">수강꾸러미 보기</a>
        </div>

        <table class="data-table">
            <thead>
            <tr>
                <th>강의코드</th>
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
            <tr>
                <td>CS101</td>
                <td>자료구조</td>
                <td>김교수</td>
                <td>컴퓨터공학과</td>
                <td>3학점</td>
                <td>50명</td>
                <td><span class="badge success">12</span></td>
                <td><button class="btn-primary" type="button">담기</button></td>
            </tr>
            <tr>
                <td>BUS210</td>
                <td>마케팅 원론</td>
                <td>이교수</td>
                <td>경영학과</td>
                <td>3학점</td>
                <td>60명</td>
                <td><span class="badge warning">5</span></td>
                <td><button class="btn-primary" type="button">담기</button></td>
            </tr>
            <tr>
                <td>ECON310</td>
                <td>계량경제학</td>
                <td>박교수</td>
                <td>경제학과</td>
                <td>3학점</td>
                <td>40명</td>
                <td><span class="badge danger">0</span></td>
                <td><button class="btn-disabled" type="button" disabled>마감</button></td>
            </tr>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
