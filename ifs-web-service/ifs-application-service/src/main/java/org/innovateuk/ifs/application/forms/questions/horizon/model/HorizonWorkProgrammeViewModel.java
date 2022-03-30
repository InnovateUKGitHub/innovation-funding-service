package org.innovateuk.ifs.application.forms.questions.horizon.model;

import org.innovateuk.ifs.horizon.resource.HorizonWorkProgramme;

import java.util.List;
import java.util.Map;

public class HorizonWorkProgrammeViewModel {

    private final String applicationName;
    private final Long applicationId;
    private final boolean complete;
    private final boolean open;
    private final boolean leadApplicant;
    private final Map<String, List<HorizonWorkProgramme>> readOnlyMap;
    private final boolean readOnly;
    private final String pageTitle;
    private final boolean isCallId;
    private final long questionId;
    private final boolean allReadOnly;
    private final String leadApplicantName;

    public HorizonWorkProgrammeViewModel(String applicationName,
                                          Long applicationId,
                                          String pageTitle,
                                          boolean isCallId,
                                          long questionId,
                                          boolean allReadOnly,
                                          String leadApplicantName,
                                          boolean complete,
                                          boolean open,
                                          boolean leadApplicant,
                                          boolean readOnly,
                                          Map<String, List<HorizonWorkProgramme>> readOnlyMap
    ) {
        this.applicationName = applicationName;
        this.applicationId = applicationId;
        this.pageTitle = pageTitle;
        this.isCallId = isCallId;
        this.questionId = questionId;
        this.allReadOnly = allReadOnly;
        this.leadApplicantName = leadApplicantName;
        this.complete = complete;
        this.open = open;
        this.leadApplicant = leadApplicant;
        this.readOnly = readOnly;
        this.readOnlyMap = readOnlyMap;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isLeadApplicant() {
        return leadApplicant;
    }

    public Map<String, List<HorizonWorkProgramme>> getReadOnlyMap() {
        return readOnlyMap;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isCallId() {
        return isCallId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public boolean isAllReadOnly() {
        return allReadOnly;
    }

    public String getLeadApplicantName() {
        return leadApplicantName;
    }

    public boolean showQuestionAssignedBanner() {
        return !leadApplicant && !complete;
    }
}
