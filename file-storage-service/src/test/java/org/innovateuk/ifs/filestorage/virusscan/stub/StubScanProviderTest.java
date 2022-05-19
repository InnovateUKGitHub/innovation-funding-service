package org.innovateuk.ifs.filestorage.virusscan.stub;

import org.innovateuk.ifs.IfsProfileConstants;
import org.innovateuk.ifs.filestorage.cfg.virusscan.VirusScanConfiguration;
import org.innovateuk.ifs.filestorage.exception.VirusDetectedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles(IfsProfileConstants.STUB_AV_SCAN)
@SpringBootTest(classes = VirusScanConfiguration.class)
class StubScanProviderTest {

    @Autowired
    private StubScanProvider stubScanProvider;

    @Test
    void scanFile() {
        stubScanProvider.scanFile("sdfsdfsfd".getBytes(StandardCharsets.UTF_8));
        byte[] eicar = "X5O!P%@AP[4\\PZX54(P^)7CC)7}$EICAR-STANDARD-ANTIVIRUS-TEST-FILE!$H+H*"
                .getBytes(StandardCharsets.UTF_8);
        assertThrows(
                VirusDetectedException.class,
                () -> stubScanProvider
                        .scanFile(eicar)
        );
    }

}