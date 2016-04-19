package com.asum.xhttphandler2.tools;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.asum.xhttphandler2.callback.XDownLoadCallBack;
import com.asum.xhttphandler2.enums.DownloadState;
import com.asum.xhttphandler2.vo.DownLoadCallBackVO;
import com.asum.xhttphandler2.vo.DownLoadCancelableVO;
import com.asum.xhttphandler2.vo.DownloadInfoVO;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.common.Callback.Cancelable;
import org.xutils.common.task.PriorityExecutor;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executor;

/**
 * 下载管理者
 * 
 * @author Asum
 *
 */
public class XDownLoadManager {
	private final static Executor executor = new PriorityExecutor(2, true);
	private static DbManager.DaoConfig daoConfig;
	private static DbManager db;

	// 下载监听器
	private static ArrayList<DownLoadCallBackVO> downLoadCallBackVOs;
	// 下载文件存放根目录
	private static String rootDir;
	// 同时下载最大数量
	private static int threadCount;
	// 正在下载的数量
	private static int loadingCount;
	// 下载队列
	private static ArrayList<DownloadInfoVO> downloadInfoVOQueues;
	// 实际的下载操作者
	private static ArrayList<DownLoadCancelableVO> downLoadCancelableVOs;

	/**
	 * 初始化
	 * 
	 * @param context
	 * @param flag
	 * @param rootDir
	 * @param threadCount
	 */
	public static void initialize(Context context, String flag, String rootDir, int threadCount) {
		XDownLoadManager.rootDir = rootDir;
		XDownLoadManager.threadCount = threadCount;

		if (rootDir == null) {
			rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/XDownload/";
		}

		FileTools.createFolder(rootDir);

		daoConfig = new DbManager.DaoConfig().setDbName("x_" + flag + "download.db").setDbVersion(1)//
				.setDbOpenListener(new DbManager.DbOpenListener() {
					public void onDbOpened(DbManager db) {
						// 开启WAL, 对写入加速提升巨大
						db.getDatabase().enableWriteAheadLogging();
					}
				});
		db = x.getDb(daoConfig);
	}

	/**
	 * 关闭下载
	 */
	public static void close() {
		stopAllTask();
		if (db != null) {
			try {
				db.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 添加一个下载任务（注：并未执行下载）
	 * 
	 * @param id
	 *            任务ID
	 * @param url
	 *            任务下载地址
	 * @param name
	 *            任务显示名称
	 * @param autoResume
	 *            是否支持断点续传
	 */
	public static void addTask(int id, String url, String name, boolean autoResume) {
		DownloadInfoVO downloadInfoVO = createTask(id, url, name, rootDir + "/" + name, autoResume);

		try {
			db.save(downloadInfoVO);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加一个下载任务（注：并未执行下载，ID自动生成并返回）
	 * 
	 * @param url
	 *            任务下载地址
	 * @param name
	 *            任务显示名称
	 * @param autoResume
	 *            是否支持断点续传
	 * @return
	 */
	public static int addTask(String url, String name, String savePath, boolean autoResume) {
		DownloadInfoVO downloadInfoVO = createTask(-1, url, name, savePath, autoResume);

		try {
			db.save(downloadInfoVO);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			return db.selector(DownloadInfoVO.class).where("fileSavePath", "=", savePath).findAll().get(0).getId();
		} catch (DbException e) {
			e.printStackTrace();
		}

		return -1;
	}

	/**
	 * 开启所有的下载任务
	 */
	public static void startAllTask() {
		ArrayList<DownloadInfoVO> allTaskVOs = getAllTask();
		if (allTaskVOs != null) {
			for (DownloadInfoVO taskVO : allTaskVOs) {
				startTask(taskVO);
			}
		}
	}

	/**
	 * 开始下载某任务
	 * 
	 * @param downloadInfoVO
	 */
	public static void startTask(final DownloadInfoVO downloadInfoVO) {
		if (downloadInfoVOQueues == null) {
			downloadInfoVOQueues = new ArrayList<DownloadInfoVO>();
		}

		if (downloadInfoVO != null) {
			for (int i = 0; i < downloadInfoVOQueues.size(); i++) {
				if (downloadInfoVOQueues.get(i).getId() == downloadInfoVO.getId()) {
					return;
				}
			}
			downloadInfoVOQueues.add(downloadInfoVO);
			openTask(downloadInfoVO);
		} else {
			if (downloadInfoVOQueues.size() > 0) {
				openTask(downloadInfoVOQueues.get(0));
			}
		}
	}

	/**
	 * 下载指定ID的任务
	 * 
	 * @param id
	 */
	public static void startTaskById(int id) {
		try {
			startTask(db.selector(DownloadInfoVO.class).where("id", "=", id).findFirst());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 停止所有的下载任务
	 */
	public static void stopAllTask() {
		if (downloadInfoVOQueues != null) {
			downloadInfoVOQueues.clear();
			if (downLoadCancelableVOs != null) {
				for (DownLoadCancelableVO cancelableVO : downLoadCancelableVOs) {
					cancelableVO.getCancelable().cancel();
				}
			}
		}
	}

	/**
	 * 停止指定ID的下载任务
	 * 
	 * @param id
	 */
	public static void stopTaskById(int id) {
		if (downloadInfoVOQueues != null) {
			if (downLoadCancelableVOs != null) {
				for (DownLoadCancelableVO cancelableVO : downLoadCancelableVOs) {
					if (cancelableVO.getId() == id) {
						cancelableVO.getCancelable().cancel();
					}
				}
			}
		}
	}

	/**
	 * 重新下载指定ID的任务，并返回新ID
	 * 
	 * @param id
	 * @return
	 */
	public static int reDownTaskById(int id) {
		DownloadInfoVO downloadInfoVO = getTaskById(id);
		stopTaskById(id);
		removeFileById(id);
		removeTaskById(id);
		int newId = addTask(downloadInfoVO.getUrl(), downloadInfoVO.getName(), downloadInfoVO.getFileSavePath(), downloadInfoVO.isAutoResume());
		startTaskById(newId);
		return newId;
	}

	/**
	 * 获取所有任务的数量
	 * 
	 * @return
	 */
	public static int getAllTaskCount() {
		try {
			return db.findAll(DownloadInfoVO.class).size();
		} catch (DbException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取所有的下载任务
	 * 
	 * @return
	 */
	public static ArrayList<DownloadInfoVO> getAllTask() {
		try {
			return (ArrayList<DownloadInfoVO>) db.findAll(DownloadInfoVO.class);
		} catch (DbException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取指定ID的任务
	 * 
	 * @param id
	 * @return
	 */
	public static DownloadInfoVO getTaskById(int id) {
		try {
			return db.selector(DownloadInfoVO.class).where("id", "=", id).findFirst();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 监听指定ID的任务
	 * 
	 * @param id
	 * @param callBack
	 */
	public static void addCallBackById(int id, XDownLoadCallBack callBack) {
		DownLoadCallBackVO callBackVO = new DownLoadCallBackVO();
		callBackVO.setId(id);
		callBackVO.setCallBack(callBack);

		if (downLoadCallBackVOs == null) {
			downLoadCallBackVOs = new ArrayList<DownLoadCallBackVO>();
		}
		downLoadCallBackVOs.add(callBackVO);
	}

	/**
	 * 移除指定ID任务的监听
	 * 
	 * @param id
	 */
	public static void removeCallBackById(int id) {
		if (downLoadCallBackVOs != null) {
			for (DownLoadCallBackVO callBackVO : downLoadCallBackVOs) {
				if (callBackVO.getId() == id) {
					downLoadCallBackVOs.remove(callBackVO);
				}
			}
		}
	}

	/**
	 * 移除所有的监听
	 */
	public static void removeAllCallBack() {
		if (downLoadCallBackVOs != null) {
			downLoadCallBackVOs.clear();
		}
	}

	/**
	 * 移除所有的下载任务
	 */
	public static void removeAllTask() {
		try {
			db.delete(DownloadInfoVO.class);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 移除指定ID的下载任务
	 * 
	 * @param id
	 */
	public static void removeTaskById(int id) {
		try {
			db.delete(DownloadInfoVO.class, WhereBuilder.b("id", "=", id));
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 移除所有已下载的文件
	 */
	public static void removeAllFiles() {
		FileTools.removeAll(rootDir);
	}

	/**
	 * 移除指定ID的下载文件
	 * 
	 * @param id
	 */
	public static void removeFileById(int id) {
		DownloadInfoVO downloadInfoVO = getTaskById(id);
		if (downloadInfoVO != null) {
			FileTools.removeFile(downloadInfoVO.getFileSavePath());
		}
	}

	/**
	 * 获取所有下载文件的总大小
	 * 
	 * @return
	 */
	public static long getAllFileSize() {
		return FileTools.getAllFileSize(rootDir);
	}

	private static DownloadInfoVO createTask(int id, String url, String name, String savePath, boolean autoResume) {
		DownloadInfoVO downloadInfoVO = new DownloadInfoVO();
		if (id != -1) {
			downloadInfoVO.setId(id);
		}
		downloadInfoVO.setUrl(url);
		downloadInfoVO.setName(name);
		downloadInfoVO.setFileSavePath(savePath);
		downloadInfoVO.setAutoRename(false);
		downloadInfoVO.setAutoResume(autoResume);
		return downloadInfoVO;
	}

	private static void callBackState(DownloadInfoVO downloadInfoVO, DownloadState state) {
		if (downLoadCallBackVOs != null) {
			for (int i = 0; i < downLoadCallBackVOs.size(); i++) {
				if (downLoadCallBackVOs.get(i).getId() == downloadInfoVO.getId()) {
					downLoadCallBackVOs.get(i).getCallBack().excute(downloadInfoVO, state);
				}
			}
		}
	}

	private static void callBackIsLoading(DownloadInfoVO downloadInfoVO, long total, long current, boolean isDownloading) {
		if (downLoadCallBackVOs != null) {
			for (int i = 0; i < downLoadCallBackVOs.size(); i++) {
				if (downLoadCallBackVOs.get(i).getId() == downloadInfoVO.getId()) {
					downLoadCallBackVOs.get(i).getCallBack().loading(downloadInfoVO, total, current, isDownloading);
				}
			}
		}
	}

	private static void update(DownloadInfoVO downloadInfoVO) {
		try {
			db.update(downloadInfoVO);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void openTask(final DownloadInfoVO downloadInfoVO) {
		if (threadCount > loadingCount) {
			callBackState(downloadInfoVO, DownloadState.WAITING);

			RequestParams params = new RequestParams(downloadInfoVOQueues.get(0).getUrl());
			params.setAutoResume(downloadInfoVOQueues.get(0).isAutoResume());
			params.setAutoRename(downloadInfoVOQueues.get(0).isAutoRename());
			params.setSaveFilePath(downloadInfoVOQueues.get(0).getFileSavePath());
			params.setExecutor(executor);
			params.setCancelFast(true);

			Cancelable cancelable = x.http().get(params, new Callback.ProgressCallback<File>() {
				public void onCancelled(CancelledException arg0) {
					downloadInfoVO.setState(DownloadState.STOPPED);
					update(downloadInfoVO);

					callBackState(downloadInfoVO, DownloadState.STOPPED);
				}

				public void onError(Throwable ex, boolean arg1) {
					if (ex instanceof HttpException) { // 网络错误
						HttpException httpEx = (HttpException) ex;
						int responseCode = httpEx.getCode();
						String responseMsg = httpEx.getMessage();
						String errorResult = httpEx.getResult();
						Log.i("XJW", "网络错误：(Code：" + responseCode + ")" + responseMsg + "  " + errorResult);
					} else { // 其他错误
						Log.i("XJW", "下载的其他错误");
					}

					downloadInfoVOQueues.remove(downloadInfoVO);
					downloadInfoVO.setState(DownloadState.ERROR);
					update(downloadInfoVO);

					callBackState(downloadInfoVO, DownloadState.ERROR);
				}

				public void onFinished() {
					loadingCount--;
					downloadInfoVOQueues.remove(downloadInfoVO);
					startTask(null);
					Log.i("XJW", "下载中-1，剩余：" + loadingCount);
				}

				public void onSuccess(File arg0) {
					downloadInfoVO.setState(DownloadState.STARTED);
					update(downloadInfoVO);

					callBackState(downloadInfoVO, DownloadState.FINISHED);
				}

				public void onLoading(long total, long current, boolean isDownloading) {
					downloadInfoVO.setFileLength(total);
					downloadInfoVO.setProgress((int) (current * 100 / total));
					downloadInfoVO.setState(DownloadState.STARTED);
					update(downloadInfoVO);

					callBackIsLoading(downloadInfoVO, total, current, isDownloading);
				}

				public void onStarted() {
					loadingCount++;
					Log.i("XJW", "下载中+1，剩余：" + loadingCount);
					callBackState(downloadInfoVO, DownloadState.STARTED);
				}

				public void onWaiting() {
					callBackState(downloadInfoVO, DownloadState.WAITING);
				}
			});

			if (downLoadCancelableVOs == null) {
				downLoadCancelableVOs = new ArrayList<DownLoadCancelableVO>();
			}

			DownLoadCancelableVO cancelableVO = new DownLoadCancelableVO();
			cancelableVO.setId(downloadInfoVO.getId());
			cancelableVO.setCancelable(cancelable);
			downLoadCancelableVOs.add(cancelableVO);
		}
	}
}
