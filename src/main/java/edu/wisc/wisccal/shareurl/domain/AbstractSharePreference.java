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
package edu.wisc.wisccal.shareurl.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Abstract implementation of {@link ISharePreference}, simply provides
 * implementations of {@link #equals(Object)}, {@link #hashCode()}, and {@link #toString()}.
 *  
 * @author Nicholas Blair, nblair@doit.wisc.edu
 * @version $Id: AbstractSharePreference.java 1694 2010-02-12 16:22:39Z npblair $
 */
public abstract class AbstractSharePreference implements ISharePreference, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53706L;

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof AbstractSharePreference)) {
			return false;
		}
		AbstractSharePreference rhs = (AbstractSharePreference) obj;
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(this.getType(), rhs.getType());
		builder.append(this.getKey(), rhs.getKey());
		builder.append(this.getValue(), rhs.getValue());
		return builder.isEquals();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(this.getType());
		builder.append(this.getKey());
		builder.append(this.getValue());
		return builder.toHashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		builder.append("type", this.getType());
		builder.append("key", this.getKey());
		builder.append("value", this.getValue());
		return builder.toString();
	}

	/**
	 * Default implementation does nothing.
	 * 
	 * @see edu.wisc.wisccal.shareurl.domain.ISharePreference#dispose()
	 */
	@Override
	public void dispose() {
	}
	
}
