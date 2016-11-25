package com.worth.ifs.competition.resource;

import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;
import static java.util.Arrays.asList;

public enum ApplicantFormInputType {

    QUESTION("textarea"),
    FILE_UPLOAD("fileupload");

    private String title;

    ApplicantFormInputType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static ApplicantFormInputType getByTitle(String title) {
        return simpleFindFirst(asList(values()), applicantFormInputType -> applicantFormInputType.getTitle().equalsIgnoreCase(title))
            .orElseThrow(() -> new IllegalArgumentException("ApplicantFormInputType not found: " + title));
    }
}