package com.asum.xhttphandler2.vo;

import org.xutils.common.Callback.Cancelable;

/**
 * 下载操作者
 * 
 * @author Asum
 *
 */
public class DownLoadCancelableVO {
	private int id;
	private Cancelable cancelable;

	public int getId() {
		return id;
	}

	public Cancelable getCancelable() {
		return cancelable;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCancelable(Cancelable cancelable) {
		this.cancelable = cancelable;
	}
}
