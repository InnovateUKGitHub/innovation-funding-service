package org.innovateuk.ifs.file.resource;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

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

    public static FileTypeCategories fromDisplayName(String displayName) {
        return simpleFindFirst(FileTypeCategories.values(), category -> category.getDisplayName().equals(displayName)).orElse(null);
    }
}
