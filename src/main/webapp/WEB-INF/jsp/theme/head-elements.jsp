<%@ taglib prefix="rs" uri="http://www.jasig.org/resource-server" %>
<%-- this file should contain solely the stylesheet, meta tags, and javascript elements needed in the head --%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<rs:resourceURL var="mozillaStyle" value="/themes/uw-2011/css/mozilla2.css"/>
<script type="text/javascript">
<!--
if ($.browser.mozilla && (parseFloat($.browser.version) < 1.9)) {
	document.write('<link rel="stylesheet" href="${mozillaStyle}" type="text/css" media="screen">');
}
// -->
</script>
<rs:resourceURL var="mainStyle" value="/themes/uw-2011/css/main_one_column.css"/>
<link rel="stylesheet" href="${mainStyle}" type="text/css" media="all" />
<rs:resourceURL var="ie6" value="/themes/uw-2011/css/ie6.css"/>
<rs:resourceURL var="ie7" value="/themes/uw-2011/css/ie7.css"/>
<rs:resourceURL var="ie8" value="/themes/uw-2011/css/ie8.css"/>
<!--[if IE 6]>
<link rel="stylesheet" href="${ie6}" type="text/css" media="screen" />
<![endif]-->
<!--[if IE 7]>
<link rel="stylesheet" href="${ie7}" type="text/css" media="screen" />
<![endif]-->
<!--[if IE 8]>
<link rel="stylesheet" href="${ie8}" type="text/css" media="screen" />
<![endif]-->
<rs:resourceURL var="extras" value="/css/extras.css"/>
<link rel="stylesheet" href="${extras}" type="text/css" media="all" />