package org.innovateuk.ifs.file.resource;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

public enum FileTypeCategory {
    SPREADSHEET("spreadsheet", asList(
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.oasis.opendocument.spreadsheet"
    ), asList(
            ".ods", ".xlr", ".xls", ".xlsx", ".xml"
    )),
    PDF("PDF",
        singletonList("application/pdf"),
        asList(".pdf")
    ),
    DOCUMENT("text document", asList(
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.oasis.opendocument.text"
    ), asList(
            ".odt", ".doc", ".docx"
    ));

    private String displayName;
    private List<String> mimeTypes;
    private List<String> mediaTypes;

    FileTypeCategory(String displayName, List<String> mimeTypes, List<String> mediaTypes) {
        this.displayName = displayName;
        this.mimeTypes = mimeTypes;
        this.mediaTypes = mediaTypes;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getMimeTypes() {
        return mimeTypes;
    }

    public List<String> getMediaTypes() {
        return mediaTypes;
    }

    public String getDisplayMediaTypes() {
        return mediaTypes.stream().collect(joining(", "));
    }

    public static FileTypeCategory fromDisplayName(String displayName) {
        return simpleFindFirst(
                FileTypeCategory.values(),
                category -> category.getDisplayName().equals(displayName)
        )
                .orElse(null);
    }
}
