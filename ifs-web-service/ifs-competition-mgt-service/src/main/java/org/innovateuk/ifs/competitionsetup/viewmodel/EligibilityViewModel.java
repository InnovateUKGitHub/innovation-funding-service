package org.innovateuk.ifs.competitionsetup.viewmodel;

import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.competition.form.enumerable.ResearchParticipationAmount;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;

import java.util.List;

public class EligibilityViewModel extends CompetitionSetupViewModel {
    private ResearchParticipationAmount[] researchParticipationAmounts;
    private CollaborationLevel[] collaborationLevels;
    private List<OrganisationTypeResource> leadApplicantTypes;
    private String leadApplicantTypesText;
    private List<ResearchCategoryResource> researchCategories;
    private String researchCategoriesFormatted;

    public EligibilityViewModel(GeneralSetupViewModel generalSetupViewModel, ResearchParticipationAmount[] researchParticipationAmounts,
                                CollaborationLevel[] collaborationLevels, List<OrganisationTypeResource> leadApplicantTypes,
                                String leadApplicantTypesText, List<ResearchCategoryResource> researchCategories,
                                String researchCategoriesFormatted) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.researchParticipationAmounts = researchParticipationAmounts;
        this.collaborationLevels = collaborationLevels;
        this.leadApplicantTypes = leadApplicantTypes;
        this.leadApplicantTypesText = leadApplicantTypesText;
        this.researchCategories = researchCategories;
        this.researchCategoriesFormatted = researchCategoriesFormatted;
    }

    public ResearchParticipationAmount[] getResearchParticipationAmounts() {
        return researchParticipationAmounts;
    }

    public CollaborationLevel[] getCollaborationLevels() {
        return collaborationLevels;
    }

    public List<OrganisationTypeResource> getLeadApplicantTypes() {
        return leadApplicantTypes;
    }

    public String getLeadApplicantTypesText() {
        return leadApplicantTypesText;
    }

    public List<ResearchCategoryResource> getResearchCategories() {
        return researchCategories;
    }

    public String getResearchCategoriesFormatted() {
        return researchCategoriesFormatted;
    }
}
