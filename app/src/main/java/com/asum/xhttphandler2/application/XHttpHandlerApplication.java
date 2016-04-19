package com.asum.xhttphandler2.application;

import android.app.Application;

import com.asum.xhttphandler2.tools.XHttpHandler;

public class XHttpHandlerApplication extends Application {
	public void onCreate() {
		super.onCreate();
		XHttpHandler.ini(this);
	}
}
