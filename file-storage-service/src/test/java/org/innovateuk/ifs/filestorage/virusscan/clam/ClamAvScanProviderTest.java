package org.innovateuk.ifs.filestorage.virusscan.clam;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.innovateuk.ifs.filestorage.exception.VirusDetectedException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {ClamAvScanProvider.class})
class ClamAvScanProviderTest {

    private ByteArrayInputStream stream = new ByteArrayInputStream("123123123123".getBytes(StandardCharsets.UTF_8));

    @Autowired
    private ClamAvScanProvider clamAvScanProvider;

    @MockBean
    private ClamavClient clamAVClient;

    @Test
    @Disabled("unable to mock kotlin lib")
    void scanFile() {
        when(clamAVClient.scan(stream))
                .thenReturn(ScanResult.OK.INSTANCE);
        clamAvScanProvider.scanFile(stream.readAllBytes());

        when(clamAVClient.scan(stream))
                .thenReturn(new ScanResult.VirusFound(ImmutableMap.of("Virus", ImmutableList.of("1"))));
        assertThrows(
                VirusDetectedException.class,
                () -> clamAvScanProvider.scanFile(stream.readAllBytes())
        );
    }

}