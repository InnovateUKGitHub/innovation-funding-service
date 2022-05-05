package org.innovateuk.ifs.api.filestorage.v1.download;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class FileDownloadResponseTest {

    @Test
    void equalsTest() {
        EqualsVerifier.forClass(FileDownloadResponse.class).verify();
    }
}