package org.innovateuk.ifs.file.resource;

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
}
