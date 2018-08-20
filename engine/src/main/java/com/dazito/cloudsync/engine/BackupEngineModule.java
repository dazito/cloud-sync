package com.dazito.cloudsync.engine;

import com.dazito.cloudsync.engine.cloud.CloudStore;
import com.dazito.cloudsync.engine.cloud.aws.AwsCloudStore;
import com.dazito.cloudsync.engine.db.DataStore;
import com.dazito.cloudsync.engine.db.jpa.JPADataStore;
import com.dazito.cloudsync.engine.event.CloudSyncRxBus;
import com.dazito.cloudsync.engine.event.CloudSyncRxBusImpl;
import com.dazito.cloudsync.engine.util.config.Config;
import com.google.inject.AbstractModule;
import net.jmob.guice.conf.core.ConfigurationModule;

import java.io.File;

public class BackupEngineModule extends AbstractModule {

    @Override protected void configure() {
        // loading the config.json file into the Config class, which can then be injected into relevant places
        install(new ConfigurationModule().fromPath(new File("./")));
        requestInjection(Config.class);

//        bind(CloudStore.class).to(AzureCloudStore.class);
        bind(CloudStore.class).to(AwsCloudStore.class);
        bind(DataStore.class).to(JPADataStore.class);
        bind(CloudSyncRxBus.class).to(CloudSyncRxBusImpl.class);
    }
}