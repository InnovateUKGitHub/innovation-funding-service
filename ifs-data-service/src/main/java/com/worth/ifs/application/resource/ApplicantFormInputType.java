package com.worth.ifs.application.resource;

import static java.util.Arrays.stream;

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
        return stream(values())
                .filter(applicantFormInputType -> applicantFormInputType.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("ApplicantFormInputType not found: " + title));
    }
}