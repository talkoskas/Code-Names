<%--
  Created by IntelliJ IDEA.
  User: talkaskas
  Date: 28/07/2024
  Time: 10:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Upload Files</title>
</head>
<body>
<h1>Upload XML and Dictionary Files</h1>
<form action="${pageContext.request.contextPath}/admin/file-upload" method="post" enctype="multipart/form-data">
    <!-- Hidden field for username -->
    <input type="hidden" name="username" value="${param.username}">

    <label for="xmlFile">Select XML File:</label>
    <input type="file" name="xml" id="xmlFile" required><br><br>

    <label for="dictionaryFile">Select Dictionary File:</label>
    <input type="file" name="dictionary" id="dictionaryFile" required><br><br>

    <label for="dictionaryName">Dictionary Name:</label>
    <input type="text" name="dictionaryName" id="dictionaryName" required><br><br>

    <label for="roomName">Room Name:</label>
    <input type="text" name="roomName" id="roomName" required><br><br>

    <button type="submit">Upload</button>
</form>
</body>
</html>