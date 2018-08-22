package com.dazito.cloudsync.engine.model;

import lombok.Data;

import javax.persistence.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Entity
@Data
@Table(name = "record")
public class LocalRecord implements Comparable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Backup backup;

    private String filePath;
    private long lastModifiedTime;
    private long size;

    protected LocalRecord() { }

    public static Optional<LocalRecord> create(Backup backup, Path p) {
        try {
            LocalRecord localRecord = new LocalRecord();
            configure(localRecord, backup, p);
            return Optional.of(localRecord);
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public void update(Backup backup, Path path) {
        try {
            configure(this, backup, path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean matches(Path path) {
        try {
            return path != null &&
                    path.toString().equals(filePath) &&
                    Files.getLastModifiedTime(path).toMillis() == lastModifiedTime &&
                    Files.size(path) == size;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void configure(LocalRecord localRecord, Backup backup, Path path) throws IOException {
        localRecord.backup = backup;
        localRecord.filePath = path.toString();
        localRecord.lastModifiedTime = Files.getLastModifiedTime(path).toMillis();
        localRecord.size = Files.size(path);
    }

    public Path getPath() {
        return Paths.get(filePath);
    }

    @Override
    public int compareTo(Object o) {
        return filePath.compareTo(((LocalRecord)o).filePath);
    }
}
