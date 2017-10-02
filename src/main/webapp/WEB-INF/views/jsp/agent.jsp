<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>Kontiki Saml Consumer Web Application - Agent</title>
<spring:url value="/resources/core/css/bootstrap.min.css" var="bootstrapCss" />
<link href="${bootstrapCss}" rel="stylesheet" />

<script language="JavaScript">
	function isIE() {
		return navigator.userAgent.indexOf("MSIE") != -1;
	}

	var gUrn = "${urn}";
	var theRealm = "${realm}";
	var theTicket = "${realmTicket}";

	//alert("theRealm: " + theRealm + "<==");
	//alert("theTicket: " + theTicket + "<==");
	//alert("theMoid: " + gUrn + "<==");

	var gAuthToken = "kontiki | *.kontiki.com | urn:kid:kontiki:";
	if (isIE()) {
		var gAuthSignature = "CCPKXGkrOKOQJPLP9LYrCBjaEY+CR1+HnfaSmLqUZxRCWIpeOTS0qt+lQzC3qDKs5rNOGHIaC66h2oIQAjGsvAa73qtiDVFwK4aEllV12nXTxuB9BoJLuX6tuplAE6QlUNUWq6o2AjtKmllvhvXd7jxka8DNh1n34FphjlLIREg=";
		var gObj = new ActiveXObject("KDX.SecureApi");
		gObj.authorize(gAuthToken, gAuthSignature);
	}

	function setAuthInfo() {

		if (isIE()) {
			try {
				theValue = gObj.setAuthInfo(theRealm, "authRealmTicket",
						theTicket);
			} catch (e) {
				alert("Error: setAuthInfo");
			}

			//load the moid and close the window
			gObj.load2(gUrn);
		} else {
			//Mac Client
			kontikiMacApi.setAuthInfo(theRealm, theTicket);
			kontikiMacApi.loadUrn(gUrn);
		}
	}

	//We stub this out due to the above function...
	function onStateChangeFunc(i_moid, i_event, i_data) {
		if (isIE()) {
			if (i_moid == gUrn) {
				if (i_event == 0) //NEW EVENT
				{
					var isStream = gObj.value(i_moid, "stream");
					gObj.fireEvent("doSamlPlay", i_moid);
					window.open('', '_self', '');
					window.close();
				} else if (i_event == 13) //KDX_STATE_ERROR
				{
					launchError(i_moid, i_data);
					window.open('', '_self', '');
					window.close();
				}
			}
		}
	}

	function launchError(urn, theEvent) {
		var URLParam = "urn=" + urn;
		var eventurl = eventData2UrlParam(theEvent);

		URLParam += "&" + eventurl;

		var param = "url=cache:km/kdmx_error.html?" + URLParam;
		param += "#uitype=3";
		param += "#width=400";
		param += "#height=140";
		param += "#position=1";
		param += "#visible=0";
		param += "#topmost=1";
		param += "#title=Authentication Error";
		gObj.startExternalWindow("error" + urn, param);

	}

	function eventData2UrlParam(data) {
		try {
			var m = data.toArray();
			var ret = "";
			for (var i = 0; i < m.length; i += 2) {
				ret += m[i];
				ret += "=";
				ret += m[i + 1];
				ret += "&";
			}

			return ret;
		} catch (e) {
			return "";
		}
	}
</script>
</head>


<body marginwidth="0" marginheight="0" leftmargin="0" topmargin="0"">
<div class="container">

	<div class="row">
		<div class="col-md-4">
			<table width=100% height=100%>
				<tr>
					<spring:url value="/resources/core/images/loading.gif"
						var="loadingImg" />
					<td align=center><img src="${loadingImg}">
					<p>Authenticating...</p></td>
				</tr>
			</table>
		</div>
	</div>


	<hr>
	<footer>
		<p>&copy; Kollective Inc. 2017</p>
	</footer>
</div>

	<script>
		//we do this so the page works in non-IE browsers
		if (isIE()) {
			document
					.write("<script>function gObj::OnStateChange(i_moid, i_event, i_data){onStateChangeFunc(i_moid, i_event, i_data);}<\/script>");
		}

		//Set the auth info
		setAuthInfo();
	</script>

<spring:url value="/resources/core/css/bootstrap.min.js" var="bootstrapJs" />

<script src="${bootstrapJs}"></script>

</body>
</html>
