package com.dazito.cloudsync.engine.cloud;

import com.dazito.cloudsync.engine.model.Backup;

import java.nio.file.Path;

public interface CloudStore {

    void createContainer(Backup backup);

    void uploadFile(Backup backup, Path p, Runnable onSuccess);

    void removeFile(Backup backup, Path p, Runnable onSuccess);
}
