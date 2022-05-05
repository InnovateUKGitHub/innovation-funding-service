package org.innovateuk.ifs.api.filestorage.v1.upload;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class FileUploadResponseTest {

    @Test
    void equalsTest() {
        EqualsVerifier.forClass(FileUploadResponse.class).verify();
    }
}