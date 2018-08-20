package com.dazito.cloudsync.engine.event;

import io.reactivex.Observable;

public interface CloudSyncRxBus {

	void setBackupEvent(BackupEvent backupEvent);
	Observable<BackupEvent> getBackupEventObservable();
}
