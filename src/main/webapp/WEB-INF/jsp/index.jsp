<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML>
<html lang="en">
   <head>
      <meta charset="UTF-8" />
      <title>Welcome</title>
      <link rel="stylesheet" type="text/css"
                href="${pageContext.request.contextPath}/css/style.css"/>
   </head>
<body>

<form method="post" action="${pageContext.request.contextPath}">
    <input type="checkbox" id="sub" name="sub-name" <c:if test="${subEnabled eq true}">checked=checked</c:if>>
    honeyramonaflowers sub event
    </input>
    <input type="submit" value="submit"/>
</form>

</body>
</html>



