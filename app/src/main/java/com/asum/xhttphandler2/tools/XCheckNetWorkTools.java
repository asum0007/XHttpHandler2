package com.asum.xhttphandler2.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

/**
 * 检查网络工具
 * 
 * @author Asum
 *
 */
public class XCheckNetWorkTools {
	public static boolean check(Context context) {
		boolean flag = false;
		// 得到网络连接信息
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 去进行判断网络是否连接
		if (manager.getActiveNetworkInfo() != null) {
			flag = manager.getActiveNetworkInfo().isAvailable();
		}

		return flag;
	}

	/**
	 * 网络已经连接，然后去判断是wifi连接还是GPRS连接 设置一些自己的逻辑调用
	 * 
	 * @return type 1代表wifi，2代表手机网络
	 */
	public static int networkAvailable(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		State gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (gprs == State.CONNECTED || gprs == State.CONNECTING) {
			return 2;
		}
		if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
			return 1;
		}
		return 0;
	}
}
