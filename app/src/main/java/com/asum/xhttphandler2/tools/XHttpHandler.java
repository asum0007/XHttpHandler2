package com.asum.xhttphandler2.tools;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.asum.xhttphandler2.callback.XHttpHandlerCallBack;
import com.asum.xhttphandler2.param.XParam;
import com.asum.xhttphandler2.processor.BaseProcessor;
import com.asum.xhttphandler2.processor.XSimpleProcessor;

import org.xutils.x;

/**
 * Http请求操作类
 * 
 * @author Asum
 * 
 */
public class XHttpHandler {
	/**
	 * 初始化
	 * 
	 * @param application
	 */
	public static void ini(Application application) {
		x.Ext.init(application);
		x.Ext.setDebug(true);
	}

	/**
	 * 清空Session
	 * 
	 * @param context
	 */
	public static void clear(Context context) {
	}

	/**
	 * 请求（使用默认的处理器）
	 * 
	 * @param context
	 *            上下文对象
	 * @param method
	 *            方式
	 * @param param
	 *            请求参数
	 * @param callBack
	 *            回调函数
	 */
	public void start(Context context, Method method, XParam param, XHttpHandlerCallBack callBack) {
		try {
			BaseProcessor processor = (BaseProcessor) XSimpleProcessor.class.newInstance();
			processor.start(context, method, param, callBack);
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("XJW", "访问" + param.getUri() + "出现了问题");
		}
	}

	/**
	 * 请求（使用指定的处理器）
	 * 
	 * @param context
	 *            上下文对象
	 * @param method
	 *            方式
	 * @param param
	 *            请求参数
	 * @param callBack
	 *            回调函数
	 * @param process
	 *            指定处理器
	 */
	public void start(Context context, Method method, XParam param, XHttpHandlerCallBack callBack, Class<?> process) {
		try {
			BaseProcessor processor = (BaseProcessor) process.newInstance();
			processor.start(context, method, param, callBack);
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("XJW", "访问" + param.getUri() + "出现了问题");
		}
	}

	public enum Method {
		/**
		 * GET方法
		 */
		GET,

		/**
		 * POST方法
		 */
		POST
	}

	public enum Result {
		/**
		 * 成功访问
		 */
		SUCCESS {
			public String getName() {
				return "SUCCESS";
			}
		},

		/**
		 * 失败访问
		 */
		FAIL {
			public String getName() {
				return "FAIL";
			}
		},

		/**
		 * 取消访问
		 */
		CANCEL {
			public String getName() {
				return "CANCEL";
			}
		},

		/**
		 * 网络错误
		 */
		NET_ERROR {
			public String getName() {
				return "NET_ERROR";
			}
		},

		/**
		 * 暂停
		 */
		WAIT {
			public String getName() {
				return "WAIT";
			}
		},

		/**
		 * 开始
		 */
		START {
			public String getName() {
				return "START";
			}
		},

		/**
		 * 执行中
		 */
		LOADING {
			public String getName() {
				return "LOADING";
			}
		},

		/**
		 * 无网络
		 */
		NO_NETWORK {
			public String getName() {
				return "NO_NETWORK";
			}
		},

		/**
		 * 超时
		 */
		TIME_OUT {
			public String getName() {
				return "TIME_OUT";
			}
		},

		/**
		 * 缓存
		 */
		CACHE {
			public String getName() {
				return "CACHE";
			}
		};

		public abstract String getName();
	}
}
