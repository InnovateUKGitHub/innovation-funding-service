package org.innovateuk.ifs.competitionsetup.documents.service;

import org.innovateuk.ifs.competition.resource.DocumentResource;

import java.util.ArrayList;
import java.util.List;

public class DocumentsService {

    private List<DocumentResource> allDocuments;

    public DocumentsService() {
        allDocuments = new ArrayList<DocumentResource>();

        // TODO: remove dummy documents and fetch from database
        DocumentResource default1 = new DocumentResource();
        default1.setId(new Long(1));
        default1.setGuidance("Typey type type");
        default1.setTitle("hello");
        default1.setIncluded(Boolean.TRUE);
        default1.setPdfAccepted(Boolean.TRUE);
        DocumentResource default2 = new DocumentResource();
        default2.setId(new Long(2));
        default2.setTitle("second");

        allDocuments.add(default1);
        allDocuments.add(default2);
    }

    public List<DocumentResource> getAllDocuments() {
        return allDocuments;
    }

    public void setAllDocuments(List<DocumentResource> allDocuments) {
        this.allDocuments = allDocuments;
    }


    
    public DocumentResource getDocumentByID(Long Id) {
        for (DocumentResource document:
             allDocuments) {
            if(document.getId().equals(Id))
            {
                return document;
            }
        }
        return null;
    }
    
}
