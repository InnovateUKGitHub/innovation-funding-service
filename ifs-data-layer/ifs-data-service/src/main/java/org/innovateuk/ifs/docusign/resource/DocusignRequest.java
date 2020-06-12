package org.innovateuk.ifs.docusign.resource;

import org.innovateuk.ifs.file.service.FileAndContents;

public class DocusignRequest {

    private final long recipientUserId;
    private final String name;
    private final String email;
    private final String subject;
    private final FileAndContents fileAndContents;
    private final DocusignType docusignType;
    private final String redirectUrl;
    private final String emailBody;

    public DocusignRequest(long recipientUserId, String name, String email, String subject, FileAndContents fileAndContents, DocusignType docusignType, String redirectUrl, String emailBody) {
        this.recipientUserId = recipientUserId;
        this.name = name;
        this.email = email;
        this.subject = subject;
        this.fileAndContents = fileAndContents;
        this.docusignType = docusignType;
        this.redirectUrl = redirectUrl;
        this.emailBody = emailBody;
    }

    public long getRecipientUserId() {
        return recipientUserId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getSubject() {
        return subject;
    }

    public DocusignType getDocusignType() {
        return docusignType;
    }

    public FileAndContents getFileAndContents() {
        return fileAndContents;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public static final class DocusignRequestBuilder {
        private long recipientUserId;
        private String name;
        private String email;
        private String subject;
        private FileAndContents fileAndContents;
        private DocusignType docusignType;
        private String redirectUrl;
        private String emailBody;

        private DocusignRequestBuilder() {
        }

        public static DocusignRequestBuilder aDocusignRequest() {
            return new DocusignRequestBuilder();
        }

        public DocusignRequestBuilder withRecipientUserId(long recipientUserId) {
            this.recipientUserId = recipientUserId;
            return this;
        }

        public DocusignRequestBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public DocusignRequestBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public DocusignRequestBuilder withSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public DocusignRequestBuilder withFileAndContents(FileAndContents fileAndContents) {
            this.fileAndContents = fileAndContents;
            return this;
        }

        public DocusignRequestBuilder withDocusignType(DocusignType docusignType) {
            this.docusignType = docusignType;
            return this;
        }

        public DocusignRequestBuilder withRedirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
            return this;
        }

        public DocusignRequestBuilder withEmailBody(String emailBody) {
            this.emailBody = emailBody;
            return this;
        }

        public DocusignRequest build() {
            return new DocusignRequest(recipientUserId, name, email, subject, fileAndContents, docusignType, redirectUrl, emailBody);
        }
    }
}
