package org.innovateuk.ifs.application.readonly.viewmodel;

public class GenericQuestionAnswerRowReadOnlyViewModel {
    private final String partnerName;
    private final boolean lead;
    private final String answer;
    private final boolean markedAsComplete;

    public GenericQuestionAnswerRowReadOnlyViewModel(String partnerName, boolean lead, String answer, boolean markedAsComplete) {
        this.partnerName = partnerName;
        this.lead = lead;
        this.answer = answer;
        this.markedAsComplete = markedAsComplete;
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

    public boolean isMarkedAsComplete() {
        return markedAsComplete;
    }
}
