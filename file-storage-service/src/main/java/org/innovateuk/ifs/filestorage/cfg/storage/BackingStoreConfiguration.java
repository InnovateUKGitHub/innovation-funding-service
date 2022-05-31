package org.innovateuk.ifs.filestorage.cfg.storage;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.filestorage.storage.local.LocalStorageProvider;
import org.innovateuk.ifs.filestorage.storage.s3.S3StorageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@EnableConfigurationProperties(BackingStoreConfigurationProperties.class)
public class BackingStoreConfiguration {

    @Autowired
    private BackingStoreConfigurationProperties backingConfig;

    @Bean
    @Profile(IfsProfileConstants.LOCAL_STORAGE)
    public LocalStorageProvider localStorageProvider() {
        return new LocalStorageProvider();
    }

    @Bean
    @Profile(IfsProfileConstants.NOT_LOCAL_STORAGE)
    public S3StorageProvider s3StorageProvider() {
        return new S3StorageProvider();
    }

    @Bean
    @Profile(IfsProfileConstants.NOT_LOCAL_STORAGE)
    public AmazonS3 amazonS3() {
        final BasicAWSCredentials basicAWSCredentials =
                new BasicAWSCredentials(backingConfig.getS3().getAwsAccessKey(), backingConfig.getS3().getAwsSecretKey());
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .withRegion(backingConfig.getS3().getAwsRegion())
                .build();
    }

}
