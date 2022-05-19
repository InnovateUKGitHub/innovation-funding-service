package org.innovateuk.ifs.filestorage.virusscan.clam;

import fi.solita.clamav.ClamAVClient;
import org.innovateuk.ifs.filestorage.cfg.virusscan.VirusScanConfiguration;
import org.innovateuk.ifs.filestorage.exception.VirusDetectedException;
import org.innovateuk.ifs.filestorage.storage.StorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(VirusScanConfiguration.class)
class ClamAvScanProviderTest {

    private byte[] testString = "123123123123".getBytes(StandardCharsets.UTF_8);

    @Autowired
    private ClamAvScanProvider clamAvScanProvider;

    @MockBean
    private ClamAVClient clamAVClient;

    @MockBean
    private StorageService storageService;

    @Test
    void scanFile() throws IOException {
        when(clamAVClient.scan(testString))
                .thenReturn("OK no virus".getBytes(StandardCharsets.US_ASCII));
        clamAvScanProvider.scanFile(testString);

        when(clamAVClient.scan(testString))
                .thenReturn("FOUND virus (not really just a test)".getBytes(StandardCharsets.US_ASCII));
        assertThrows(
                VirusDetectedException.class,
                () -> clamAvScanProvider.scanFile(testString)
        );
    }

}