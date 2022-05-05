package org.innovateuk.ifs.filestorage.virusscan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.innovateuk.ifs.api.filestorage.v1.upload.VirusScanStatus;

@Data
@AllArgsConstructor
@Accessors(fluent = true)
public class VirusScanResult {

    private VirusScanStatus virusScanStatus;

    private String virusScanResultMessage;
}
