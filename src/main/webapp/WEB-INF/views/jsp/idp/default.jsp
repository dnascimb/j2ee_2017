<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>KSAML</title>

<spring:url value="/resources/core/css/bootstrap.min.css" var="bootstrapCss" />
<link href="${bootstrapCss}" rel="stylesheet" />
</head>

<nav class="navbar navbar-inverse navbar-fixed-top">
  <div class="container">
    <div class="navbar-header">
      <a class="navbar-brand" href="#">KSAML</a>
    </div>
  </div>
</nav>

<div class="jumbotron">
  <div class="container">
    <h1>KSAML (IdP Endpoint)</h1>
  </div>
</div>

<div class="container">

  <div class="row">
    <div class="col-md-4">
      <h2>Current version : <c:import url="/version.txt"/></h2>
    </div>
  </div>
    <div class="row">
    <div class="col-md-4">
      <h3>${status}</h3>
    </div>
  </div>


  <hr>
  <footer>
    <p>&copy; Kollective Inc. 2017</p>
  </footer>
</div>

<spring:url value="/resources/core/js/jquery-3.2.1.min.js" var="jqueryJs" />
<spring:url value="/resources/core/js/bootstrap.min.js" var="bootstrapJs" />
<script src="${jqueryJs}"></script>
<script src="${bootstrapJs}"></script>

</body>
</html>