package com.dazito.cloudsync.engine.event;

import lombok.Getter;
import lombok.ToString;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

@Getter
@ToString
public class BackupEvent {

	private final WatchEvent watchEvent;
	private final Path path;

	// The path to the backup folder
	private final Path backupPath;

	public BackupEvent(WatchEvent watchEvent, Path fullPath, Path backupPath) {
		this.watchEvent = watchEvent;
		this.path = fullPath;
		this.backupPath = backupPath;
	}
}
