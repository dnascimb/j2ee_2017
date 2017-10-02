<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
    	<title>IdP Authentication</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">    
	</head>
  
	<body>
		<form name="myRedirectForm" action="${destination}" method="post"> 
			<input name="SAMLResponse" type="hidden" value="${samlResponse}" />
			<input value="Submit" type="submit">
		</form> 

		<script type="text/javascript"> 
			document.myRedirectForm.submit();
		</script> 
	</body>
</html>
