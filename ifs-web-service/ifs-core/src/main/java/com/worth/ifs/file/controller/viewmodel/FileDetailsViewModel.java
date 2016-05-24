package com.worth.ifs.file.controller.viewmodel;

import com.worth.ifs.file.resource.FileEntryResource;

import java.math.BigDecimal;

import static java.math.BigDecimal.ROUND_HALF_EVEN;

/**
 * Small view model holding attributes around files
 */
public class FileDetailsViewModel {

    public static final BigDecimal ONE_THOUSAND = BigDecimal.valueOf(1000L);

    private String filename;
    private BigDecimal filesizeKbytes;

    public FileDetailsViewModel(FileEntryResource fileEntry) {
        this.filename = fileEntry.getName();
        this.filesizeKbytes = BigDecimal.valueOf(fileEntry.getFilesizeBytes()).divide(ONE_THOUSAND, 2, ROUND_HALF_EVEN);
    }

    public String getFilename() {
        return filename;
    }

    public BigDecimal getFilesizeKbytes() {
        return filesizeKbytes;
    }
}
