package org.innovateuk.ifs.string.resource;

public class StringResource {

    private String content;

    private StringResource() {}

    public StringResource(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
