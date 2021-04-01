package org.innovateuk.ifs.application.readonly.viewmodel;

public class GenericQuestionAnswerRowReadOnlyViewModel {
    private final String partnerName;
    private final boolean lead;
    private final String answer;

    public GenericQuestionAnswerRowReadOnlyViewModel(String partnerName, boolean lead, String answer) {
        this.partnerName = partnerName;
        this.lead = lead;
        this.answer = answer;
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

}
