package org.innovateuk.ifs.knowledgebase.resourse;

public enum KnowledgeBaseType {

    RTO("Research and technology organisation (RTO)","Organisations which solely promote and conduct collaborative research and innovation."),
    UNIVERSITY("University","These consist of both, further education and higher education institutions."),
    CATAPULT("Catapult","Technology centres that work toward improving the UK’s innovation capabilities.");

    private String text;
    private String description;


    KnowledgeBaseType(String text, String description) {
        this.text = text;
        this.description = description;
    }

    public String getText() {
        return text;
    }

    public String getDescription() {
        return description;
    }
}
