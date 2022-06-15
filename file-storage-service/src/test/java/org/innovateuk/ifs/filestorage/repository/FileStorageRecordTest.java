package org.innovateuk.ifs.filestorage.repository;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class FileStorageRecordTest {

    @Test
    void equalsTest() {
        EqualsVerifier.simple().forClass(FileStorageRecord.class).verify();
    }

}