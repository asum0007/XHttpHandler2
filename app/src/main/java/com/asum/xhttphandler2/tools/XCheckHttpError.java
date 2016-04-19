package com.asum.xhttphandler2.tools;

import org.apache.http.conn.ConnectTimeoutException;

import com.asum.xhttphandler2.tools.XHttpHandler.Result;

import android.content.Context;

@SuppressWarnings("deprecation")
public class XCheckHttpError {
	public static Result getResultByException(Context context, Exception e) {
		boolean haveNetWork = XCheckNetWorkTools.check(context);
		if (!haveNetWork) {
			return Result.NO_NETWORK;
		} else {
			try {
				if (e.getCause().getClass().getSimpleName().equals(ConnectTimeoutException.class.getSimpleName())) {
					return Result.TIME_OUT;
				} else {
					return Result.FAIL;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return Result.FAIL;
			}
		}
	}
}
