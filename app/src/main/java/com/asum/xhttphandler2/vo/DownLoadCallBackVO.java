package com.asum.xhttphandler2.vo;

import com.asum.xhttphandler2.callback.XDownLoadCallBack;

/**
 * 下载监听
 * 
 * @author Asum
 *
 */
public class DownLoadCallBackVO {
	private int id;
	private XDownLoadCallBack callBack;

	public int getId() {
		return id;
	}

	public XDownLoadCallBack getCallBack() {
		return callBack;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setCallBack(XDownLoadCallBack callBack) {
		this.callBack = callBack;
	}
}
