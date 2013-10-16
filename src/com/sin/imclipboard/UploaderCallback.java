package com.sin.imclipboard;


/**
 * Callback for Uploader
 * @author RobinaTang
 *
 */
public interface UploaderCallback {
	public boolean updateStatsuChanged(long transize, long filesize, String filename);
	public boolean updateStatsuChangedString(String status);
}
