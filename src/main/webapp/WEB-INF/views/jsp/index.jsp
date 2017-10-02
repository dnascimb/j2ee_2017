<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Kontiki Saml Consumer Web Application</title>

<spring:url value="/resources/core/css/bootstrap.min.css" var="bootstrapCss" />
<link href="${bootstrapCss}" rel="stylesheet" />
</head>

<nav class="navbar navbar-inverse navbar-fixed-top">
	<div class="container">
		<div class="navbar-header">
			<a class="navbar-brand" href="#">Kontiki Saml Consumer Web Application</a>
		</div>
	</div>
</nav>

<div class="jumbotron">
	<div class="container">
		<h1>Kontiki Saml Consumer Web Application</h1>
	</div>
</div>

<div class="container">

	<div class="row">
		<div class="col-md-4">
			<h2>Current version : <c:import url="/version.txt"/></h2>
		</div>
	</div>


	<hr>
	<footer>
		<p>&copy; Kollective Inc. 2017</p>
	</footer>
</div>

<spring:url value="/resources/core/css/bootstrap.min.js" var="bootstrapJs" />

<script src="${bootstrapJs}"></script>

</body>
</html>