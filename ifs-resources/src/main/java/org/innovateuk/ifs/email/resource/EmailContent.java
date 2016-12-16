package org.innovateuk.ifs.email.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class EmailContent {

    private String subject;
    private String plainText;
    private String htmlText;

    /**
     * For builder use only
     */
    public EmailContent() {
        // no-arg constructor
    }

    public EmailContent(String subject, String plainText, String htmlText) {
        this.subject = subject;
        this.plainText = plainText;
        this.htmlText = htmlText;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPlainText() {
        return plainText;
    }

    public void setPlainText(String plainText) {
        this.plainText = plainText;
    }

    public String getHtmlText() {
        return htmlText;
    }

    public void setHtmlText(String htmlText) {
        this.htmlText = htmlText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EmailContent that = (EmailContent) o;

        return new EqualsBuilder()
                .append(subject, that.subject)
                .append(plainText, that.plainText)
                .append(htmlText, that.htmlText)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(subject)
                .append(plainText)
                .append(htmlText)
                .toHashCode();
    }
}
