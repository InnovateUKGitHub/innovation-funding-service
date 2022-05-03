package org.innovateuk.ifs.filestorage.cfg;

import com.amazonaws.services.s3.AmazonS3;
import com.google.common.collect.ImmutableList;
import org.innovateuk.ifs.filestorage.cfg.storage.BackingStoreConfiguration;
import org.innovateuk.ifs.filestorage.cfg.virusscan.VirusScanConfiguration;
import org.innovateuk.ifs.filestorage.storage.gluster.GlusterStorageProvider;
import org.innovateuk.ifs.filestorage.storage.local.LocalStorageProvider;
import org.innovateuk.ifs.filestorage.storage.s3.S3StorageProvider;
import org.innovateuk.ifs.filestorage.util.TestHelper;
import org.innovateuk.ifs.filestorage.virusscan.clam.ClamAvScanProvider;
import org.innovateuk.ifs.filestorage.virusscan.stub.StubScanProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.annotation.UserConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.innovateuk.ifs.IfsProfileConstants.*;
import static org.innovateuk.ifs.filestorage.cfg.storage.BackingStoreConfigurationProperties.BACKING_STORE_CONFIG_PREFIX;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StorageServiceConfigurationTest {

    @Test
    @ResourceLock("LOCK")
    void testStubAvLocalStorage() {
        new ApplicationContextRunner()
            .withSystemProperties(
                TestHelper.activeProfilesString(ImmutableList.of(STUB_AV_SCAN, LOCAL_STORAGE))
            )
            .withConfiguration(
                    UserConfigurations.of(BackingStoreConfiguration.class, VirusScanConfiguration.class)
            ).run((context) -> {
                    assertThat(context.getBean(LocalStorageProvider.class), is(notNullValue()));
                    assertThat(context.getBean(StubScanProvider.class), is(notNullValue()));
                    assertThrows(BeansException.class, () -> context.getBean(ClamAvScanProvider.class));
                    assertThrows(BeansException.class, () -> context.getBean(S3StorageProvider.class));
                    assertThrows(BeansException.class, () -> context.getBean(GlusterStorageProvider.class));
                    assertThrows(BeansException.class, () -> context.getBean(AmazonS3.class));
            });
    }

    @Test
    @ResourceLock("LOCK")
    void testClamDefaultScannerLocalStorage() {
        new ApplicationContextRunner()
                .withSystemProperties(
                        TestHelper.activeProfilesString(ImmutableList.of(LOCAL_STORAGE))
                )
                .withConfiguration(
                        UserConfigurations.of(BackingStoreConfiguration.class, VirusScanConfiguration.class)
                ).run((context) -> {
                    assertThat(context.getBean(LocalStorageProvider.class), is(notNullValue()));
                    assertThat(context.getBean(ClamAvScanProvider.class), is(notNullValue()));
                    assertThrows(BeansException.class, () -> context.getBean(StubScanProvider.class));
                    assertThrows(BeansException.class, () -> context.getBean(S3StorageProvider.class));
                    assertThrows(BeansException.class, () -> context.getBean(GlusterStorageProvider.class));
                    assertThrows(BeansException.class, () -> context.getBean(AmazonS3.class));
                });
    }

    @Test
    @ResourceLock("LOCK")
    void testClamDefaultScannerS3AndGlusterStorage() {
        new ApplicationContextRunner()
                .withSystemProperties(
                        TestHelper.activeProfilesString(ImmutableList.of(GLUSTER_STORAGE, S3_STORAGE)),
                        BACKING_STORE_CONFIG_PREFIX + ".s3.awsAccessKey=123",
                        BACKING_STORE_CONFIG_PREFIX + ".s3.awsSecretKey=123",
                        BACKING_STORE_CONFIG_PREFIX + ".s3.awsRegion=eu-west-2"
                )
                .withConfiguration(
                        UserConfigurations.of(BackingStoreConfiguration.class, VirusScanConfiguration.class)
                ).run((context) -> {
                    assertThat(context.getBean(S3StorageProvider.class), is(notNullValue()));
                    assertThat(context.getBean(AmazonS3.class), is(notNullValue()));
                    assertThat(context.getBean(GlusterStorageProvider.class), is(notNullValue()));
                    assertThat(context.getBean(ClamAvScanProvider.class), is(notNullValue()));
                    assertThrows(BeansException.class, () -> context.getBean(LocalStorageProvider.class));
                });
    }

}