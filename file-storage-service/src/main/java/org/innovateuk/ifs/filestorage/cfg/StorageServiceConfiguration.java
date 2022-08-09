package org.innovateuk.ifs.filestorage.cfg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.tika.Tika;
import org.innovateuk.ifs.filestorage.cfg.storage.BackingStoreConfiguration;
import org.innovateuk.ifs.filestorage.cfg.virusscan.VirusScanConfiguration;
import org.innovateuk.ifs.filestorage.storage.StorageService;
import org.innovateuk.ifs.filestorage.storage.StorageServiceHelper;
import org.innovateuk.ifs.filestorage.storage.validator.TikaFileValidator;
import org.innovateuk.ifs.filestorage.storage.validator.UploadValidator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableConfigurationProperties(StorageServiceConfigurationProperties.class)
@Import({BackingStoreConfiguration.class, VirusScanConfiguration.class})
public class StorageServiceConfiguration {

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
