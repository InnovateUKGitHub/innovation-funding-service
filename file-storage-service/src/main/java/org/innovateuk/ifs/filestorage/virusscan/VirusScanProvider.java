package org.innovateuk.ifs.filestorage.virusscan;

import org.innovateuk.ifs.filestorage.exception.VirusDetectedException;

public interface VirusScanProvider {

    void scanFile(byte[] fileBytes) throws VirusDetectedException;

}
