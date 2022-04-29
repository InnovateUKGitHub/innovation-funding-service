package org.innovateuk.ifs.filestorage.cfg.storage;

import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.filestorage.storage.gluster.GlusterStorageProvider;
import org.innovateuk.ifs.filestorage.storage.local.LocalStorageProvider;
import org.innovateuk.ifs.filestorage.storage.s3.S3StorageProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

public class BackingStoreConfiguration {

    @Bean
    @Profile(IfsProfileConstants.LOCAL_STORAGE)
    public LocalStorageProvider localStorageProvider() {
        return new LocalStorageProvider();
    }

    @Bean
    @Profile(IfsProfileConstants.GLUSTER_STORAGE)
    public GlusterStorageProvider glusterStorageProvider() {
        return new GlusterStorageProvider();
    }

    @Bean
    @Profile(IfsProfileConstants.S3_STORAGE)
    public S3StorageProvider s3StorageProvider() {
        return new S3StorageProvider();
    }

}
