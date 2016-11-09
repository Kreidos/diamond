/**
 * Created On Apr 14, 2011
 * Copyright 2011 by Primeleaf Consulting (P) Ltd.,
 * #29,784/785 Hendre Castle,
 * D.S.Babrekar Marg,
 * Gokhale Road(North),
 * Dadar,Mumbai 400 028
 * India
 * 
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Primeleaf Consulting (P) Ltd. ("Confidential Information").  
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Primeleaf Consulting (P) Ltd.
 */

package com.primeleaf.krystal.util;

import org.apache.commons.fileupload.ProgressListener;

/**
 * Author Rahul Kubadia
 */

public class FileUploadProgressListener implements ProgressListener {
	private volatile long bytesRead = 0L, contentLength = 0L, item = 0L;
	public void update(long bytesRead, long contentLength, int item) {
		this.bytesRead = bytesRead;
		this.contentLength = contentLength;
		this.item = item;
	}
	/**
	 * @return the bytesRead
	 */
	public long getBytesRead() {
		return bytesRead;
	}
	/**
	 * @return the contentLength
	 */
	public long getContentLength() {
		return contentLength;
	}
	/**
	 * @return the item
	 */
	public long getItem() {
		return item;
	}
	
}

