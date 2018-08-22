package com.dazito.cloudsync.engine.util;

import com.dazito.cloudsync.engine.event.BackupEvent;
import com.dazito.cloudsync.engine.event.CloudSyncRxBus;
import com.dazito.cloudsync.engine.model.Backup;
import io.reactivex.Observable;
import lombok.extern.slf4j.Slf4j;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WatchDir {

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private final boolean recursive;
    private final CloudSyncRxBus cloudSyncRxBus;

    private ExecutorService watchService = Executors.newSingleThreadExecutor();

    /**
     * Creates a WatchService and registers the given directory
     */
    public WatchDir(List<Backup> backupList, boolean recursive, CloudSyncRxBus cloudSyncRxBus) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();
        this.recursive = recursive;
        this.cloudSyncRxBus = cloudSyncRxBus;

        if (recursive) {
            registerAll(backupList);
        } else {
            register(backupList);
        }

		watchService.submit(this::processEvents);
    }

	/**
	 * Overload method that will call {@link WatchDir#register(Path)} for each {@link Backup} in the list.
	 * @param backupList
	 * @throws IOException
	 */
	private void register(List<Backup> backupList) throws IOException {
    	for(Backup backup : backupList) {
    		register(backup.getRootDirectory());
		}
	}

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path path) throws IOException {
        WatchKey key = path.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keys.put(key, path);
    }

	/**
	 * Overload method that will call {@link WatchDir#registerAll(Path)} for each {@link Backup} in the list.
	 * @param backupList
	 * @throws IOException
	 */
	private void registerAll(final List<Backup>  backupList) throws IOException {
    	for(Backup backup : backupList) {
    		registerAll(backup.getRootDirectory());
		}
	}

	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 */
	private void registerAll(final Path start) throws IOException {
		// register directory and sub-directories
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				register(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

    private void processEvents() {
		log.debug("Process events on thread: {}", Thread.currentThread().toString());

		Observable<Long> interval = Observable.interval(1, 3, TimeUnit.SECONDS);

		interval.subscribe(timeTick -> {
			log.debug("Got a time tick on thread: {}", Thread.currentThread().toString());

			WatchKey key = watcher.poll();

			if(key == null) {
				return;
			}

			Path dir = keys.get(key);
			if (dir == null) {
				log.info("Dir is null!");
				return;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind kind = event.kind();

				if (kind == OVERFLOW) {
					log.info("Kind is overflow");
					continue;
				}

				Path name = (Path) event.context();
				Path fullPath = dir.resolve(name);

				// send event over to backup client to handle it appropriately
				cloudSyncRxBus.setBackupEvent(new BackupEvent(event, fullPath, dir));

				// if directory is created, and watching recursively, then
				// register it and its sub-directories
				if (recursive && (kind == ENTRY_CREATE)) {
					try {
						if (Files.isDirectory(fullPath, NOFOLLOW_LINKS)) {
							registerAll(fullPath);
						}
					} catch (IOException x) {
						log.error("Error while registering all, {}, x");
						// ignore to keep sample readable
					}
				}
			}

			// reset key and remove from set if directory no longer accessible
			boolean valid = key.reset();
			if (!valid) {
				keys.remove(key);

				// all directories are inaccessible
				if (keys.isEmpty()) {
					log.info("keys is empty");
				}
			}
		});
	}
}