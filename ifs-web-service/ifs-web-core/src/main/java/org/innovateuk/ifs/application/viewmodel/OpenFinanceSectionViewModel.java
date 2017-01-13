package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;
import java.util.Optional;

/**
 * ViewModel for Finance open sections
 */
public class OpenFinanceSectionViewModel extends BaseSectionViewModel {

    private List<SectionResource> fundingSections;

    public OpenFinanceSectionViewModel(NavigationViewModel navigationViewModel, SectionResource currentSection,
                                       Boolean hasFinanceSection, Long financeSectionId, UserResource currentUser,
                                       Boolean subFinanceSection) {
        this.navigationViewModel = navigationViewModel;
        this.currentSection = currentSection;
        this.hasFinanceSection = hasFinanceSection;
        this.financeSectionId = financeSectionId;
        this.currentUser = currentUser;
        this.subFinanceSection = subFinanceSection;
    }

    public void setFundingSections(List<SectionResource> fundingSections) {
        this.fundingSections = fundingSections;
    }

    public Boolean getOrganisationSizeAlert() {
        if(null == fundingSections) {
            return Boolean.FALSE;
        }

        Optional<SectionResource> fundingFinance = fundingSections.stream()
                .filter(sectionResource -> sectionsMarkedAsComplete.contains(sectionResource.getId()))
                .findAny();

        return fundingFinance.isPresent();
    }
}
