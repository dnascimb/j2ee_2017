<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Kollective</title>

<spring:url value="/resources/core/css/bootstrap.min.css" var="bootstrapCss" />
<link href="${bootstrapCss}" rel="stylesheet" />
</head>

<div class="container">

  <div class="row">
    <div class="col-md-4">
      <p>We cannot identify the company you're associated with. Please log into your MediaCenter and go to Analytics from there. You only need to do this once.</p>
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
