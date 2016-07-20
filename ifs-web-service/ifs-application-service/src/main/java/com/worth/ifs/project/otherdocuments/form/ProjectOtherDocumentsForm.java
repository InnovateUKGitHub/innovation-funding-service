package com.worth.ifs.project.otherdocuments.form;

import com.worth.ifs.controller.BaseBindingResultTarget;
import org.springframework.web.multipart.MultipartFile;

/**
 * Form backing the Other Documents page
 */
public class ProjectOtherDocumentsForm extends BaseBindingResultTarget {

    private MultipartFile collaborationAgreement;
    private MultipartFile exploitationPlan;

    public MultipartFile getCollaborationAgreement() {
        return collaborationAgreement;
    }

    public void setCollaborationAgreement(MultipartFile collaborationAgreement) {
        this.collaborationAgreement = collaborationAgreement;
    }

    public MultipartFile getExploitationPlan() {
        return exploitationPlan;
    }

    public void setExploitationPlan(MultipartFile exploitationPlan) {
        this.exploitationPlan = exploitationPlan;
    }
}
