package org.innovateuk.ifs.filestorage.virusscan;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.innovateuk.ifs.api.filestorage.v1.upload.VirusScanStatus;

@Data
@AllArgsConstructor
public class VirusScanResult {

    private VirusScanStatus virusScanStatus;

    private String virusScanResultMessage;
}
