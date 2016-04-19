package com.asum.xhttphandler2.callback;

import com.asum.xhttphandler2.enums.DownloadState;
import com.asum.xhttphandler2.vo.DownloadInfoVO;

/**
 * 下载时的回调函数
 * 
 * @author Asum
 *
 */
public interface XDownLoadCallBack {
	public void excute(DownloadInfoVO downloadInfoVO, DownloadState state);

	public void loading(DownloadInfoVO downloadInfoVO, long total, long current, boolean isDownloading);
}
