package com.asum.xhttphandler2.processor;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.common.Callback.CancelledException;
import org.xutils.common.Callback.ProgressCallback;
import org.xutils.ex.HttpException;

import com.asum.xhttphandler2.callback.XHttpHandlerCallBack;
import com.asum.xhttphandler2.param.XParam;
import com.asum.xhttphandler2.tools.XHttpHandler.Method;
import com.asum.xhttphandler2.tools.XHttpHandler.Result;

import android.content.Context;
import android.util.Log;

public class XSimpleProcessor extends BaseProcessor {
	private String url;
	private XHttpHandlerCallBack callBack;

	private String result;
	private Result resultType;

	public void start(Context context, Method method, XParam param, XHttpHandlerCallBack callBack) {
		this.callBack = callBack;
		url = param.getUri();

		if (method == Method.GET) {
			if (param.isCache()) {
				x.http().get(param, cacheCallback);
			} else {
				x.http().get(param, progressCallback);
			}
		} else {
			if (param.isCache()) {
				x.http().post(param, cacheCallback);
			} else {
				x.http().post(param, progressCallback);
			}
		}
	}

	Callback.CacheCallback<String> cacheCallback = new Callback.CacheCallback<String>() {
		public void onSuccess(String result) {
			success(result);
		}

		public void onError(Throwable ex, boolean arg1) {
			error(ex);
		}

		public void onCancelled(CancelledException arg0) {
			cancel(arg0);
		}

		public boolean onCache(String cache) {
			resultType = Result.CACHE;
			result = cache;
			return true;
		}

		public void onFinished() {
			finish();
		}
	};

	ProgressCallback<String> progressCallback = new ProgressCallback<String>() {
		public void onSuccess(String result) {
			success(result);
		}

		public void onFinished() {
			finish();
		}

		public void onError(Throwable ex, boolean arg1) {
			error(ex);
		}

		public void onCancelled(CancelledException arg0) {
			cancel(arg0);
		}

		public void onWaiting() {
			resultType = Result.WAIT;
			if (callBack != null) {
				callBack.execute(resultType, result);
			}
		}

		public void onStarted() {
			resultType = Result.START;
			if (callBack != null) {
				callBack.execute(resultType, result);
			}
		}

		public void onLoading(long arg0, long arg1, boolean arg2) {
			resultType = Result.LOADING;
			if (callBack != null) {
				callBack.execute(resultType, arg0 + "," + arg1);
			}
		}
	};

	private void finish() {
		Log.d("XJW", "请求链接：" + url);
		Log.i("XJW", "返回值：" + result);

		if (callBack != null) {
			callBack.execute(resultType, result);
		}
	}

	private void error(Throwable ex) {
		if (ex instanceof HttpException) { // 网络错误
			HttpException httpEx = (HttpException) ex;
			int responseCode = httpEx.getCode();
			String responseMsg = httpEx.getMessage();
			String errorResult = httpEx.getResult();

			resultType = Result.NET_ERROR;
			result = "网络错误：(Code：" + responseCode + ")" + responseMsg + "  " + errorResult;
		} else {
			resultType = Result.FAIL;
			result = "其他错误";
		}
	}

	private void success(String result) {
		XSimpleProcessor.this.result = result;
		resultType = Result.SUCCESS;
	}

	private void cancel(CancelledException arg0) {
		resultType = Result.CANCEL;
		if (arg0 != null) {
			result = "请求取消：" + arg0.getMessage();
		} else {
			result = "请求取消：用户取消";
		}
	}
}
