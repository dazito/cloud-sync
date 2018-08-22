package com.dazito.cloudsync.engine.util;

import com.dazito.cloudsync.engine.model.Backup;

import javax.inject.Singleton;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class CacheService {

	private final Map<Path, Backup> backupCache = new ConcurrentHashMap<>();

	public void cacheBackup(Path path, Backup backup) {
		backupCache.put(path, backup);
	}

	public Optional<Backup> getBackup(Path path) {
		Backup backup = backupCache.get(path);
		return Optional.ofNullable(backup);
	}
}
