package com.asum.xhttphandler2.callback;

import com.asum.xhttphandler2.tools.XHttpHandler;

/**
 * 网络请求回调的函数
 * 
 * @author Asum
 * 
 */
public interface XHttpHandlerCallBack {
	/**
	 * 执行
	 * 
	 * @param resultType
	 *            执行状态
	 * @param returnString
	 *            执行返回值
	 */
	public void execute(XHttpHandler.Result resultType, String returnString);
}
