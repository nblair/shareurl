/*******************************************************************************
*  Copyright 2007-2010 The Board of Regents of the University of Wisconsin System.
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*******************************************************************************/
package edu.wisc.wisccal.shareurl.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Helper class intended for providing methods that can be exposed in the viewhelper tag library.
 * 
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: ViewHelper.java 1441 2009-12-17 19:09:21Z npblair $
 */
public class ViewHelper {

	private static final int HTTP_PORT = 80;
	private static final int HTTPS_PORT = 443;
	private static final String SEPARATOR = "://";
	
	private static Log LOG = LogFactory.getLog(ViewHelper.class);
	
	/**
	 * For a given {@link HttpServletRequest}, ferret out:
	 * <ul>
	 * <li>scheme (http or https)</li>
	 * <li>virtual server name (e.g. tools.wisccal.wisc.edu)</li>
	 * <li>server port, if non-standard</li>
	 * <ul>
	 * 
	 * The result of this method is a {@link String} that looks like:
	 * <pre>
	 * https://tools.wisccal.wisc.edu
	 * </pre>
	 * 
	 * @param request
	 * @return a formatted {@link String} representing the virtual server's address
	 */
	public static String getVirtualServerAddress(final HttpServletRequest request) {
		if(null == request) {
			LOG.warn("received null request to getVirtualServerAddress, returning empty string");
			return "";
		}
		StringBuilder virtualServer = new StringBuilder();
		virtualServer.append(request.getScheme());
		virtualServer.append(SEPARATOR);
		virtualServer.append(request.getServerName());
		int port = request.getServerPort();
		if(port == HTTP_PORT || port == HTTPS_PORT) {
			// skip the port, since agents know already
		} else {
			// the port isn't standard, make sure we add it
			virtualServer.append(":");
			virtualServer.append(port);
		}
		return virtualServer.toString();
	}
}
