package org.innovateuk.ifs.email.resource;

/**
 * Created by eamonnharrison on 14/12/2016.
 */
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
}
