<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

	<jsp:useBean 
		id="preds"
		type="model.Predictions"
		class="model.Predictions"	
	></jsp:useBean>

	<%
		if(request.getMethod().equalsIgnoreCase("GET")){
			preds.setServletContext(application); // IF this is not donne then
												  // you cannot correctly access files 
												  //inside the generated Jar file!!!!!! 
			//out.println(preds.getPredictions());
		}
	
	%>
	


</body>
</html>