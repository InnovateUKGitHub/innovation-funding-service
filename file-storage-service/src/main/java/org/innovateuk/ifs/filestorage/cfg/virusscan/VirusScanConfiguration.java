package org.innovateuk.ifs.filestorage.cfg.virusscan;

import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.filestorage.virusscan.VirusScanProvider;
import org.innovateuk.ifs.filestorage.virusscan.clam.ClamAvScanProvider;
import org.innovateuk.ifs.filestorage.virusscan.stub.StubScanProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import xyz.capybara.clamav.ClamavClient;

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
    public ClamavClient clamAVClient() {
        return new ClamavClient(
                virusScanConfigurationProperties.getClamAv().getHost(),
                virusScanConfigurationProperties.getClamAv().getPort()
        );
    }

}
