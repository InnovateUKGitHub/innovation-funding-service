package org.innovateuk.ifs.file.resource;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

public enum FileTypeCategories {
    SPREADSHEET("Spreadsheet", "application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.oasis.opendocument.spreadsheet"),
    PDF("PDF", "application/pdf");

    private String displayName;
    private String mediaTypeString;

    FileTypeCategories(String displayName, String mediaTypeString) {
        this.displayName = displayName;
        this.mediaTypeString = mediaTypeString;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMediaTypeString() {
        return mediaTypeString;
    }

    public List<String> getMediaTypes() {
        return simpleMap(mediaTypeString.split(","), StringUtils::trim);
    }

    public static FileTypeCategories fromDisplayName(String displayName) {
        return simpleFindFirst(FileTypeCategories.values(), category -> category.getDisplayName().equals(displayName)).orElse(null);
    }
}
