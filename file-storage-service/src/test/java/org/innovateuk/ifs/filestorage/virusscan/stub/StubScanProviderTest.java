package org.innovateuk.ifs.filestorage.virusscan.stub;

import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.api.filestorage.v1.upload.VirusScanStatus;
import org.innovateuk.ifs.filestorage.cfg.virusscan.VirusScanConfiguration;
import org.innovateuk.ifs.filestorage.virusscan.VirusScanResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles(IfsProfileConstants.STUB_AV_SCAN)
@SpringBootTest(classes = VirusScanConfiguration.class)
class StubScanProviderTest {

    @Autowired
    private StubScanProvider stubScanProvider;

    @Test
    void scanFile() throws IOException {
        VirusScanResult virusScanResult = stubScanProvider.scanFile("sdfsdfsfd".getBytes(StandardCharsets.UTF_8));
        assertThat(virusScanResult.virusScanStatus(), equalTo(VirusScanStatus.VIRUS_FREE));

        virusScanResult = stubScanProvider.scanFile("X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*".getBytes(StandardCharsets.UTF_8));
        assertThat(virusScanResult.virusScanStatus(), equalTo(VirusScanStatus.VIRUS_DETECTED));
    }
}