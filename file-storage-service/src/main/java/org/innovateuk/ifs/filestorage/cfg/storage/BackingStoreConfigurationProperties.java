package org.innovateuk.ifs.filestorage.cfg.storage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.innovateuk.ifs.filestorage.cfg.storage.BackingStoreConfigurationProperties.BACKING_STORE_CONFIG_PREFIX;

@Getter
@Setter
@ConfigurationProperties(prefix = BACKING_STORE_CONFIG_PREFIX)
public class BackingStoreConfigurationProperties {

    public static final String BACKING_STORE_CONFIG_PREFIX = "ifs.filestorage.storage";

    private final LocalStorage localStorage = new LocalStorage();
    private final Gluster gluster = new Gluster();
    private final S3 s3 = new S3();

    @Getter
    @Setter
    public static class LocalStorage {

        private String rootFolderPath;

    }

    @Getter
    @Setter
    public static class Gluster {

        private String rootFolderPath;

    }

    @Getter
    @Setter
    public static class S3 {

        private String awsAccessKey;
        private String awsSecretKey;
        private String awsRegion;
        private String fileStoreS3Bucket;

    }
}
