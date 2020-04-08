package org.innovateuk.ifs.docusign.resource;

import org.innovateuk.ifs.file.service.FileAndContents;

public class DocusignRequest {

    private final long recipientUserId;
    private final String name;
    private final String email;
    private final String documentName;
    private final FileAndContents fileAndContents;
    private final DocusignType docusignType;
    private final String redirectUrl;

    public DocusignRequest(long recipientUserId, String name, String email, String documentName, FileAndContents fileAndContents, DocusignType docusignType, String redirectUrl) {
        this.recipientUserId = recipientUserId;
        this.name = name;
        this.email = email;
        this.documentName = documentName;
        this.fileAndContents = fileAndContents;
        this.docusignType = docusignType;
        this.redirectUrl = redirectUrl;
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

    public String getDocumentName() {
        return documentName;
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
}
