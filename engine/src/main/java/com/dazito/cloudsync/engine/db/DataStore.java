package com.dazito.cloudsync.engine.db;

import com.dazito.cloudsync.engine.model.Backup;
import com.dazito.cloudsync.engine.model.LocalRecord;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public interface DataStore {

    List<Backup> getBackupList();

    LocalRecord getLocalRecord(Backup backup, Path p);

    void updateLocalRecord(Backup backup, LocalRecord record);

    Stream<LocalRecord> getBackupRecords(Backup backup);

    void deleteLocalRecord(LocalRecord record);
}
