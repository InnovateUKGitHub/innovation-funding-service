package org.innovateuk.ifs.application.forms.questions.horizon.model;

import lombok.Getter;
import lombok.Setter;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgramme;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class HorizonWorkProgrammeViewModel {

    private final String applicationName;
    private final Long applicationId;
    private final boolean complete;
    private final boolean open;
    private final boolean leadApplicant;
    private Map<String, List<HorizonWorkProgramme>> readOnlyMap;
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

    public boolean isCallId() {
        return isCallId;
    }

    public boolean showQuestionAssignedBanner() {
        return !leadApplicant && !complete;
    }
}
