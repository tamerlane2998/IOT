<%-- 
    Document   : HelloWorlf
    Created on : Nov 7, 2018, 10:23:51 PM
    Author     : ngoc
--%>
<%@ taglib prefix = "s" uri = "/struts-tags" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <s:form action="hello">
            <s:textfield name="username" label="Name"></s:textfield>
            <s:password name="userpass" label="Password"></s:password>
            <s:submit value="login"></s:submit>
        </s:form>
    </body>
</html>
