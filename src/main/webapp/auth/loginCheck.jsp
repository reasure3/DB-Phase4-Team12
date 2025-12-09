<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
    // 세션에서 studentId 확인
    Integer studentId = (Integer) session.getAttribute("studentId");

    // 로그인되어 있지 않으면 로그인 페이지로 리다이렉트
    if (studentId == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login");
        return;
    }
%>
