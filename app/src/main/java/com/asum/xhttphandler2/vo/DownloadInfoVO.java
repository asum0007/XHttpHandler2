package com.asum.xhttphandler2.vo;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import com.asum.xhttphandler2.enums.DownloadState;

/**
 * 下载项
 * 
 * @author Asum
 *
 */
@Table(name = "download", onCreated = "CREATE UNIQUE INDEX index_name ON download(fileSavePath)")
public class DownloadInfoVO {
	public DownloadInfoVO() {
	}

	@Column(name = "id", isId = true)
	private int id;

	@Column(name = "state")
	private DownloadState state = DownloadState.STOPPED;

	@Column(name = "url")
	private String url;

	@Column(name = "name")
	private String name;

	@Column(name = "fileSavePath")
	private String fileSavePath;

	@Column(name = "progress")
	private int progress;

	@Column(name = "fileLength")
	private long fileLength;

	@Column(name = "autoResume")
	private boolean autoResume;

	@Column(name = "autoRename")
	private boolean autoRename;

	public int getId() {
		return id;
	}

	public DownloadState getState() {
		return state;
	}

	public String getUrl() {
		return url;
	}

	public String getName() {
		return name;
	}

	public String getFileSavePath() {
		return fileSavePath;
	}

	public int getProgress() {
		return progress;
	}

	public long getFileLength() {
		return fileLength;
	}

	public boolean isAutoResume() {
		return autoResume;
	}

	public boolean isAutoRename() {
		return autoRename;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setState(DownloadState state) {
		this.state = state;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setFileSavePath(String fileSavePath) {
		this.fileSavePath = fileSavePath;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}

	public void setAutoResume(boolean autoResume) {
		this.autoResume = autoResume;
	}

	public void setAutoRename(boolean autoRename) {
		this.autoRename = autoRename;
	}
}
