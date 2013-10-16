package com.sin.imclipboard;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Upload to server
 * @author RobinTang
 *
 */
public class Uploader {
	public static int BUF_LEN = 256;
	// public static String uploadUrl = "http://127.0.0.1:8080/upload/";
	public static String uploadUrl = "http://bigstorage.sinaapp.com/upload/";
	public static String fullUploadUrl = uploadUrl+"new";
	public static final String NL = "\r\n";
	public static final String BOUNDARY = "----WebKitFormBoundaryR7KMply17vWzgbKR";
	private UploaderCallback callback;

	enum Status {
		None, Uploadding, Uploadded, Failed
	}

	private Status status = Status.None;

	public Status getStatus() {
		return status;
	}

	public Uploader(UploaderCallback callback) {
		super();
		this.callback = callback;
	}

	public String uploadImage(String imgfile) {
		if(callback == null){
			callback = new UploaderCallback(){
				@Override
				public boolean updateStatsuChanged(long transize, long filesize, String filename) {
					return false;
				}

				@Override
				public boolean updateStatsuChangedString(String status) {
					return false;
				}
			};
		}
		try {
			status = Status.Uploadding;
			String filename = imgfile;
			String suffix = "jpg";
			int ix = imgfile.lastIndexOf('/');
			ix = ix < 0 ? imgfile.lastIndexOf('\\') : ix;
			if (ix >= 0) {
				filename = imgfile.substring(ix + 1);
			}
			ix = filename.lastIndexOf('.');
			if (ix >= 0 && ix < (filename.length() - 1)) {
				suffix = filename.substring(ix + 1);
			}
			URL url = new URL(fullUploadUrl);
			callback.updateStatsuChangedString("connect to server");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "keep-alive");
			conn.setRequestProperty("User-Agent", "IMClipboard");
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			callback.updateStatsuChangedString("begin send data");
			
			
			// --BOUND\r\n
			dos.write(String.format("--%s" + NL, BOUNDARY).getBytes());
			// Content-Disposition: form-data; name="Filename"\r\n
			dos.write(String.format("Content-Disposition: form-data; name=\"file\"; filename=\"%s\"" + NL, filename).getBytes());
			// Content-Type: image/jpg\r\n
			dos.write(String.format("Content-Type: image/%s" + NL, suffix).getBytes());
			// \r\n
			dos.write(NL.getBytes());
			// file
			FileInputStream fis = new FileInputStream(imgfile);
			byte[] buffer = new byte[BUF_LEN];
			long filesize = (new File(imgfile)).length();
			long transize = 0;
			int len;
			while ((len = fis.read(buffer)) > 0) {
				dos.write(buffer, 0, len);
				transize += len;
				if(callback != null){
					callback.updateStatsuChanged(transize, filesize, filename);
					callback.updateStatsuChangedString(String.format("sended %d%%", (int) (transize * 100 / filesize)));
				}
			}
			fis.close();
			// \r\n
			dos.write(NL.getBytes());

			// --BOUNDARY--
			dos.write(String.format("--%s--" + NL, BOUNDARY).getBytes());
			dos.flush();
			dos.close();
			callback.updateStatsuChangedString("send data success");
			callback.updateStatsuChangedString("wait response");
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			StringBuffer sb = new StringBuffer();
			callback.updateStatsuChangedString("begin read data");
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append(NL);
			}
			reader.close();
			callback.updateStatsuChangedString("read data success");
			status = Status.Uploadded;
			return sb.toString();
		} catch (Exception e) {
			status = Status.Failed;
			e.printStackTrace();
		}
		return null;
	}
}
