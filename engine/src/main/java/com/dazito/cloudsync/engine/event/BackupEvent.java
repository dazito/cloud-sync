package com.dazito.cloudsync.engine.event;

import lombok.Getter;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

@Getter
public class BackupEvent {

	private final WatchEvent watchEvent;
	private final Path path;

	public BackupEvent(WatchEvent watchEvent, Path path) {
		this.watchEvent = watchEvent;
		this.path = path;
	}
}
