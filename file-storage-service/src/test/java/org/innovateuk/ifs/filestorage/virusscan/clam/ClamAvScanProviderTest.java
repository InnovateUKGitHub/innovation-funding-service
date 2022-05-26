package org.innovateuk.ifs.filestorage.virusscan.clam;

import com.diluv.clamchowder.ClamClient;
import com.diluv.clamchowder.ScanResult;
import org.innovateuk.ifs.filestorage.exception.VirusDetectedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {ClamAvScanProvider.class})
class ClamAvScanProviderTest {

    @Autowired
    private ClamAvScanProvider clamAvScanProvider;

    @MockBean
    private ClamClient clamClient;

    @Test
    void scanFile() throws IOException {
        byte[] bytes = "foo".getBytes(StandardCharsets.UTF_8);
        when(clamClient.scan(any(InputStream.class))).thenReturn(new ScanResult("stream: OK"));
        clamAvScanProvider.scanFile(bytes);

        when(clamClient.scan(any(InputStream.class))).thenReturn(new ScanResult("stream: XYZ virus FOUND"));
        VirusDetectedException virusDetectedException = assertThrows(
                VirusDetectedException.class,
                () -> clamAvScanProvider.scanFile(bytes)
        );
        assertThat(virusDetectedException.getStatus(), equalTo(HttpStatus.BAD_REQUEST));
        assertThat(virusDetectedException.getReason(), containsString("XYZ"));
    }

}