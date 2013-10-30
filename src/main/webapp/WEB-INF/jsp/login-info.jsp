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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<div id="logininfo">
	<p id="loginline">
		You are logged in as&nbsp;<span class="userid"><security:authentication
				property="principal.activeDisplayName" /></span>.&nbsp;&nbsp;&nbsp; <a
			href="<c:url value="/my-shares"/>">My Shares</a>&nbsp;
		<security:authorize ifAllGranted="ROLE_DELEGATE_ACCOUNT">
			<a href="<c:url value="/delegate_switch_exit"/>">Log out as
				Resource&raquo;</a>
		</security:authorize>
		<security:authorize ifNotGranted="ROLE_DELEGATE_ACCOUNT">
			<a href="<c:url value="/logout.html"/>">Log out&raquo;</a>
		</security:authorize>
	</p>
</div>