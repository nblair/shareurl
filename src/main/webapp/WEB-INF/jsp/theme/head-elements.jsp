<%--

    Copyright 2012, Board of Regents of the University of
    Wisconsin System. See the NOTICE file distributed with
    this work for additional information regarding copyright
    ownership. Board of Regents of the University of Wisconsin
    System licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<%@ taglib prefix="rs" uri="http://www.jasig.org/resource-server"%>
<%-- this file should contain solely the stylesheet, meta tags, and javascript elements needed in the head --%>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<rs:resourceURL var="jQuery"
	value="/rs/jquery/1.4.2/jquery-1.4.2.min.js" />
<script type="text/javascript" src="${jQuery}"></script>
<rs:resourceURL var="mozillaStyle"
	value="/themes/uw-2011/css/mozilla2.css" />
<%-- 
<script type="text/javascript">
<!--
if ($.browser.mozilla && (parseFloat($.browser.version) < 1.9)) {
	document.write('<link rel="stylesheet" href="${mozillaStyle}" type="text/css" media="screen">');
}
// -->
</script>
--%>
<rs:resourceURL var="mainStyle"
	value="/themes/uw-2011/css/main_one_column.css" />
<link rel="stylesheet" href="${mainStyle}" type="text/css" media="all" />
<rs:resourceURL var="ie6" value="/themes/uw-2011/css/ie6.css" />
<rs:resourceURL var="ie7" value="/themes/uw-2011/css/ie7.css" />
<rs:resourceURL var="ie8" value="/themes/uw-2011/css/ie8.css" />
<!--[if IE 6]>
<link rel="stylesheet" href="${ie6}" type="text/css" media="screen" />
<![endif]-->
<!--[if IE 7]>
<link rel="stylesheet" href="${ie7}" type="text/css" media="screen" />
<![endif]-->
<!--[if IE 8]>
<link rel="stylesheet" href="${ie8}" type="text/css" media="screen" />
<![endif]-->
<rs:resourceURL var="extras" value="/css/extras.css" />
<link rel="stylesheet" href="${extras}" type="text/css" media="all" />