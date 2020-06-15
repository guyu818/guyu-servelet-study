<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
    <head>
        <title>文件上传</title>
    </head>
</html>
<%--通过表单上传文件--%>
<%--get：上传文件有大小限制--%>
<%--post：上传文件没有大小限制--%>
<%--${pageContext.request.contextPath}获取服务器的路径--%>
<form action="${pageContext.request.contextPath}/upload.do" enctype="multipart/form-data" method="post">
    上传用户：<input type="text" name="username"><br/>
    上传文件1<input type="file" name="file1"><br/>
    上传文件2<input type="file" name="file2"><br/>

    <p><input type="submit" value="提交"> | <input type="reset" value="取消"></p>
</form>