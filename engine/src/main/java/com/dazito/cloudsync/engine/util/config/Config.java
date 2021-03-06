package com.dazito.cloudsync.engine.util.config;

import com.dazito.cloudsync.engine.model.Backup;
import lombok.Getter;
import net.jmob.guice.conf.core.BindConfig;
import net.jmob.guice.conf.core.InjectConfig;
import net.jmob.guice.conf.core.Syntax;
import com.dazito.cloudsync.engine.BackupEngineModule;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Config class is populated by the config.json file found in the root directory. It is installed in
 * {@link BackupEngineModule}. This makes use of Guice Configuration, and can
 * parse in complex objects as well as simple config details. Refer to http://github.com/yyvess/gconf
 */
@BindConfig(value = "config", syntax = Syntax.JSON)
public class Config {

    @InjectConfig
    @Getter
    private String azureAccountName;

    @InjectConfig
    @Getter
    private String azureAccountKey;
    
    @InjectConfig
    @Getter
    private String awsRegion;
    
    @InjectConfig
    @Getter
    private String awsAccessKey;
    
    @InjectConfig
    @Getter
    private String awsSecretKey;
    
    @InjectConfig
    @Getter
    private String awsBucket;

    @InjectConfig("backups")
    private List<Map<String, String>> backups;

    public Stream<BackupConfig> getBackupConfig() {
        return backups.stream().map(BackupConfig::new);
    }


    public static class BackupConfig {
        private final Map<String,String> map;

        private BackupConfig(Map<String,String> map) {
            this.map = map;
        }

        public String getName() {
            return map.get("name");
        }

        public String getRoot() {
            return map.get("root");
        }

        public static boolean match(BackupConfig config, Backup backup) {
            return backup.getBackupName().equals(config.getName())
                    && backup.getRootDirectoryString().equals(config.getRoot());
        }
    }
}
