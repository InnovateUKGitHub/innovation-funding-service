package org.innovateuk.ifs.application.common.viewmodel;


import java.util.List;

public class ApplicationSubsidyBasisViewModel {

    private final List<ApplicationSubsidyBasisPartnerRowViewModel> partners;

    public ApplicationSubsidyBasisViewModel(List<ApplicationSubsidyBasisPartnerRowViewModel> partners) {
        this.partners = partners;
    }

    public boolean isSubsidyBasisCompletedByAllOrganisations(){
        return partners.stream().allMatch(partner -> partner.isQuestionnareMarkedAsComplete());
    }
}