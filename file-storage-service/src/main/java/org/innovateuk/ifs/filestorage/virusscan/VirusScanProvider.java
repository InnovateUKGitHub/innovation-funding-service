package org.innovateuk.ifs.filestorage.virusscan;

import java.io.IOException;

public interface VirusScanProvider {

    VirusScanResult scanFile(byte[] fileBytes) throws IOException;

}
