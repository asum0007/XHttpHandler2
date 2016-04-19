package com.asum.xhttphandler2.processor;

import com.asum.xhttphandler2.callback.XHttpHandlerCallBack;
import com.asum.xhttphandler2.param.XParam;
import com.asum.xhttphandler2.tools.XHttpHandler.Method;

import android.content.Context;

/**
 * 自定义网络处理类基类
 * 
 * @author Asum
 *
 */
public abstract class BaseProcessor {
	public abstract void start(Context context, Method method, XParam param, XHttpHandlerCallBack callBack);
}
