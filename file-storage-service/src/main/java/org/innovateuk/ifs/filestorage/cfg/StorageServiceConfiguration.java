package org.innovateuk.ifs.filestorage.cfg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.tika.Tika;
import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.filestorage.cfg.storage.BackingStoreConfiguration;
import org.innovateuk.ifs.filestorage.cfg.virusscan.VirusScanConfiguration;
import org.innovateuk.ifs.filestorage.messaging.UploadMessageListener;
import org.innovateuk.ifs.filestorage.storage.StorageService;
import org.innovateuk.ifs.filestorage.storage.StorageServiceHelper;
import org.innovateuk.ifs.filestorage.storage.validator.TikaFileValidator;
import org.innovateuk.ifs.filestorage.storage.validator.UploadValidator;
import org.innovateuk.ifs.starters.messaging.cfg.CommonQueues;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

@Configuration
@EnableConfigurationProperties(StorageServiceConfigurationProperties.class)
@Import({BackingStoreConfiguration.class, VirusScanConfiguration.class})
public class StorageServiceConfiguration {

    @Profile(IfsProfileConstants.AMQP_ENABLED)
    @Configuration
    public static class StorageServiceAmqpConfiguration {

        @Bean
        public UploadMessageListener uploadMessageListener() {
            return new UploadMessageListener();
        }

        @Bean
        public Queue uploadMessageQueue() {
            return new Queue(CommonQueues.FILE_UPLOAD_SERVICE_UPLOAD, false);
        }
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        JavaTimeModule module = new JavaTimeModule();
        return new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(module);
    }

    @Bean
    public StorageService storageService() {
        return new StorageService();
    }

    @Bean
    public StorageServiceHelper storageServiceHelper() {
        return new StorageServiceHelper();
    }

    @Bean
    public Tika tika() { return new Tika(); }

    @Bean
    public TikaFileValidator tikaFileValidator() { return new TikaFileValidator(); }

    @Bean
    public UploadValidator uploadValidator() { return new UploadValidator(); }
}
