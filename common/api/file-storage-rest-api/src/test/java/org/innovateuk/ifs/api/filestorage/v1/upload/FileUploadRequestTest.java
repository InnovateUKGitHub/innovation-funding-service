package org.innovateuk.ifs.api.filestorage.v1.upload;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class FileUploadRequestTest {

    @Test
    void testEquals() {
        EqualsVerifier.forClass(FileUploadRequest.class).verify();
    }
}