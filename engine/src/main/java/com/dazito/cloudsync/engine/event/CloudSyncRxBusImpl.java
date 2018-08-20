package com.dazito.cloudsync.engine.event;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class CloudSyncRxBusImpl implements CloudSyncRxBus{

	private PublishSubject<BackupEvent> subject = PublishSubject.create();

	@Override
	public void setBackupEvent(BackupEvent backupEvent) {
		subject.onNext(backupEvent);
	}

	@Override
	public Observable<BackupEvent> getBackupEventObservable() {
		return subject;
	}
}
