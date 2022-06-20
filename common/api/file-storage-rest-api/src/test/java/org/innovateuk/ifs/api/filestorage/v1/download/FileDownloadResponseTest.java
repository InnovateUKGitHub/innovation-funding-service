package org.innovateuk.ifs.api.filestorage.v1.download;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class FileDownloadResponseTest {

    @Test
    void equalsTest() {
        EqualsVerifier.forClass(FileDownloadResponse.class).suppress(Warning.NONFINAL_FIELDS).verify();
    }
}