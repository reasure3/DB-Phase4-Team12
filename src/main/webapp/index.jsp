<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
    // 이미 로그인되어 있으면 main.jsp로 리다이렉트
    Integer studentId = (Integer) session.getAttribute("studentId");
    if (studentId != null) {
        response.sendRedirect(request.getContextPath() + "/main.jsp");
        return;
    }
    // 로그인되어 있지 않으면 login.jsp로 리다이렉트
    response.sendRedirect(request.getContextPath() + "/auth/login");
%>