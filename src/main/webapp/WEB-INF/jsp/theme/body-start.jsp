<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%@ taglib prefix="rs" uri="http://www.jasig.org/resource-server" %>
<div class="wrap">
  <div id="home">
    <div id="header">
      <div class="skip"><a href="#content" accesskey="S">Skip to main content</a></div>

	  <rs:resourceURL var="wordmark" value="/themes/uw-2011/images/wordmark.gif"/>
      <a id="uwhome" href="http://www.wisc.edu"><img src="${wordmark }" alt="University of Wisconsin-Madison" width="260" height="11" /></a>
	  <rs:resourceURL var="crest" value="/themes/uw-2011/images/crest.png"/>
      <a id="crest" href="http://www.wisc.edu"><img src="${crest }" alt="UW-Madison crest" width="70" height="106" /></a>

      <div id="siteTitle">
        <h1><a href="<c:url value="/"/>"><span>WiscCal ShareURL</span></a></h1>
      </div>

      <ul id="globalnav">
        <li id="uwsearch"><a href="http://www.wisc.edu/search/">UW Search</a></li><li><a href="http://my.wisc.edu">My UW</a></li><li><a href="http://map.wisc.edu">Map</a></li><li><a href="http://www.today.wisc.edu">Events</a></li><li id="last_tool"><a title="Log in to WiscMail/WiscCal web client" href="https://wiscmail.wisc.edu/">WiscMail/WiscCal</a></li>

      </ul>

	  <!--  
      <form id="search" action="post">
        <div>
          <label for="searchstring">Search this site: </label> <input name="searchstring" id="searchstring" type="text" value="" /><input name="submit" id="submit" type="submit" value="Go!" />
        </div>
      </form>
      -->
    </div>

    <div id="shell">