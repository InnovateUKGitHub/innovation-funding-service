package org.innovateuk.ifs.file.resource;

import java.util.Set;

import static com.google.common.collect.Sets.union;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.joining;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;

public enum FileTypeCategory {
    SPREADSHEET("spreadsheet",
                union(MimeTypes.MS_SPREADSHEET, MimeTypes.OPEN_SPREADSHEET),
                FileExtensions.SPREADSHEET
    ),
    PDF("PDF",
            MimeTypes.PDF,
            FileExtensions.PDF
    ),
    DOCUMENT("text document",
            union(MimeTypes.MS_DOCUMENT, MimeTypes.OPEN_DOCUMENT),
            FileExtensions.DOCUMENT
    ),
    OPEN_DOCUMENT(MimeTypes.OPEN_DOCUMENT),
    OPEN_SPREADSHEET(MimeTypes.OPEN_SPREADSHEET);

    private String displayName;
    private Set<String> mimeTypes;
    private Set<String> fileExtensions;


    FileTypeCategory(String displayName, Set<String> mimeTypes, Set<String> fileExtensions) {
        this.displayName = displayName;
        this.mimeTypes = mimeTypes;
        this.fileExtensions = fileExtensions;
    }

    FileTypeCategory(Set<String> mimeTypes) {
        this(null, mimeTypes, emptySet());
    }

    public String getDisplayName() {
        return displayName;
    }

    public Set<String> getMimeTypes() {
        return mimeTypes;
    }

    public Set<String> getFileExtensions() {
        return fileExtensions;
    }

    public String getDisplayMediaTypes() {
        return fileExtensions.stream().collect(joining(", "));
    }

    public static class MimeTypes {
        private MimeTypes() {}
        public static final Set<String> PDF = singleton("application/pdf");

        private static final Set<String> MS_SPREADSHEET = asSet("application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        private static final Set<String> OPEN_SPREADSHEET = singleton("application/vnd.oasis.opendocument.spreadsheet");

        private static final Set<String> MS_DOCUMENT =  asSet("application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        private static final Set<String> OPEN_DOCUMENT = singleton("application/vnd.oasis.opendocument.text");
    }

    public static class FileExtensions {
        private FileExtensions() {}
        public static final Set<String> PDF = singleton(".pdf");

        private static final Set<String> SPREADSHEET = asSet(".ods", ".xls", ".xlsx");
        private static final Set<String> DOCUMENT =  asSet(".odt", ".doc", ".docx");
    }
}
