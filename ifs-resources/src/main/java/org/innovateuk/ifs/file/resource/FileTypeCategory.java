package org.innovateuk.ifs.file.resource;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

public enum FileTypeCategory {
    SPREADSHEET("spreadsheet", asList(
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.oasis.opendocument.spreadsheet"
    )),
    PDF("PDF", singletonList("application/pdf"));

    private String displayName;
    private List<String> mediaTypes;

    FileTypeCategory(String displayName, List<String> mediaTypes) {
        this.displayName = displayName;
        this.mediaTypes = mediaTypes;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getMediaTypes() {
        return mediaTypes;
    }

    public static FileTypeCategory fromDisplayName(String displayName) {
        return simpleFindFirst(
                FileTypeCategory.values(),
                category -> category.getDisplayName().equals(displayName)
        )
                .orElse(null);
    }
}
