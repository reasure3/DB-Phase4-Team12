<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="/auth/loginCheck.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>내 수강신청 - 수강신청 경매 시스템</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css' />">
</head>
<body class="main-page">
    <div class="container">
        <div class="header">
            <h1>내 수강신청</h1>
            <div class="user-info">
                <span><strong>${sessionScope.studentName}</strong>님</span>
                <a href="<c:url value='/main.jsp' />" class="logout-btn">메인으로</a>
                <a href="<c:url value='/auth/logout' />" class="logout-btn">로그아웃</a>
            </div>
        </div>

        <div class="content">
            <c:if test="${not empty sessionScope.successMessage}">
                <div class="success-message">${sessionScope.successMessage}</div>
                <c:remove var="successMessage" scope="session" />
            </c:if>
            <c:if test="${not empty sessionScope.errorMessage}">
                <div class="error-message">${sessionScope.errorMessage}</div>
                <c:remove var="errorMessage" scope="session" />
            </c:if>
            <c:if test="${not empty requestScope.errorMessage}">
                <div class="error-message">${requestScope.errorMessage}</div>
            </c:if>

            <table class="data-table">
                <thead>
                    <tr>
                        <th>강의코드</th>
                        <th>분반</th>
                        <th>강의명</th>
                        <th>교수</th>
                        <th>강의실</th>
                        <th>정원</th>
                        <th>신청 경로</th>
                        <th>사용 포인트</th>
                        <th>신청일</th>
                        <th>취소</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty myEnrollments}">
                            <tr>
                                <td colspan="10" style="text-align: center;">현재 수강중인 강의가 없습니다.</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="item" items="${myEnrollments}">
                                <tr>
                                    <td><c:out value="${item.courseId}" /></td>
                                    <td><c:out value="${item.sectionNumber}" />분반</td>
                                    <td><c:out value="${item.courseName}" /></td>
                                    <td><c:out value="${item.professor}" /></td>
                                    <td><c:out value="${item.classroom}" /></td>
                                    <td><c:out value="${item.capacity}" />명</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty item.enrollmentSource}">
                                                <c:out value="${item.enrollmentSource}" />
                                            </c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><c:out value="${item.pointsUsed}" />점</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty item.enrollmentTime}">
                                                <fmt:formatDate value="${item.enrollmentTime}" pattern="yyyy-MM-dd" />
                                            </c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <form method="post" action="<c:url value='/enrollment/cancel' />" class="inline-form">
                                            <input type="hidden" name="sectionId" value="${item.sectionId}">
                                            <input type="hidden" name="returnUrl" value="${returnUrl}">
                                            <button class="btn-secondary" type="submit">취소</button>
                                        </form>
                                </td>
                        </tr>
                        </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>
