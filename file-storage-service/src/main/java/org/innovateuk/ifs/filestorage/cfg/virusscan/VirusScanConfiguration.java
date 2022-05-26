package org.innovateuk.ifs.filestorage.cfg.virusscan;

import com.diluv.clamchowder.ClamClient;
import com.diluv.clamchowder.Constants;
import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.filestorage.virusscan.VirusScanProvider;
import org.innovateuk.ifs.filestorage.virusscan.clam.ClamAvScanProvider;
import org.innovateuk.ifs.filestorage.virusscan.stub.StubScanProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@EnableConfigurationProperties(VirusScanConfigurationProperties.class)
public class VirusScanConfiguration {

    @Autowired
    private VirusScanConfigurationProperties virusScanConfigurationProperties;

    @Bean
    @Profile(IfsProfileConstants.NOT_STUB_AV_SCAN)
    public VirusScanProvider clamScanProvider() {
        return new ClamAvScanProvider();
    }

    @Bean
    @Profile(IfsProfileConstants.STUB_AV_SCAN)
    public VirusScanProvider stubScanProvider() {
        return new StubScanProvider();
    }

    @Bean
    @Profile(IfsProfileConstants.NOT_STUB_AV_SCAN)
    public ClamClient clamClient() {
        return new ClamClient(
                virusScanConfigurationProperties.getClamAv().getHost(),
                virusScanConfigurationProperties.getClamAv().getPort(),
                virusScanConfigurationProperties.getClamAv().getClientTimeoutMs(),
                virusScanConfigurationProperties.getClamAv().getScanChunkSizeBytes(),
                Constants.DEFAULT_READ_BUFFER_SIZE
        );
    }

}
