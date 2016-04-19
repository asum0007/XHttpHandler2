package com.asum.xhttphandler2.param;

import android.util.Log;

import org.xutils.http.RequestParams;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class XParam extends RequestParams {
	private String url;
	private boolean needCache;
	private boolean isUpload;
	private Map<String, File> files;
	private Map<String, String> values;

	public XParam(String url, boolean needCache, boolean isUpload) {
		super(url);
		this.needCache = needCache;
		this.isUpload = isUpload;
	}

	public void addBodyParameter(String key, File file, String contentType) {
		super.addBodyParameter(key, file, contentType);
		if (files == null) {
			files = new HashMap<String, File>();
		}
		files.put(key, file);
	}

	public void addBodyParameter(String name, String value) {
		super.addBodyParameter(name, value);
		if (values == null) {
			values = new HashMap<String, String>();
		}
		values.put(name, value);
	}

	public void addQueryParameter(String name, String value) {
		super.addQueryStringParameter(name, value);
		if (values == null) {
			values = new HashMap<String, String>();
		}
		values.put(name, value);
	}

	/**
	 * 打印参数
	 */
	public void printParams() {
		if (files != null) {
			for (String key : files.keySet()) {
				Log.i("XJW", key + "：文件--" + files.get(key).getName() + "，长度：" + files.get(key).length() + "，路径：" + files.get(key).getPath());
			}
		}
		if (values != null) {
			for (String key : values.keySet()) {
				Log.i("XJW", key + "：" + values.get(key));
			}
		}
	}

	/**
	 * 获取参数，注：无文件
	 * 
	 * @return
	 */
	public Map<String, String> getParamMap() {
		return values;
	}

	/**
	 * 获取访问链接
	 * 
	 * @return
	 */
	public String getURL() {
		return url;
	}

	/**
	 * 是否缓存
	 * 
	 * @return
	 */
	public boolean isCache() {
		return needCache;
	}

	/**
	 * 是否为上传请求
	 * 
	 * @return
	 */
	public boolean isUpload() {
		return isUpload;
	}
}
