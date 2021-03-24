package org.innovateuk.ifs.application.readonly.viewmodel;

public class GenericQuestionAnswerRowReadOnlyViewModel {
    private final String partnerName;
    private final boolean lead;
    private final String answer;
    private final boolean markedComplete;

    public GenericQuestionAnswerRowReadOnlyViewModel(String partnerName, boolean lead, String answer, boolean markedComplete) {
        this.partnerName = partnerName;
        this.lead = lead;
        this.answer = answer;
        this.markedComplete = markedComplete;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public boolean isLead() {
        return lead;
    }
    public String getAnswer() {
        return answer;
    }

    public boolean isMarkedComplete() {
        return markedComplete;
    }
}
