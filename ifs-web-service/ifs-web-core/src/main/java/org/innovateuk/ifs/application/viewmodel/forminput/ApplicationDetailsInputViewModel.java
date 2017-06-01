package org.innovateuk.ifs.application.viewmodel.forminput;


import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.viewmodel.AssignButtonsViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputType;

/**
 * View model for application details form input.
 */
public class ApplicationDetailsInputViewModel extends AbstractFormInputViewModel {

    private ApplicationResource application;
    private CompetitionResource competition;
    private String selectedResearchCategoryName;
    private String selectedInnovationAreaName;
    private AssignButtonsViewModel assignButtonsViewModel;

    @Override
    protected FormInputType formInputType() {
        return FormInputType.APPLICATION_DETAILS;
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public void setApplication(ApplicationResource application) {
        this.application = application;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public void setCompetition(CompetitionResource competition) {
        this.competition = competition;
    }

    public String getSelectedResearchCategoryName() {
        return selectedResearchCategoryName;
    }

    public void setSelectedResearchCategoryName(String selectedResearchCategoryName) {
        this.selectedResearchCategoryName = selectedResearchCategoryName;
    }

    public String getSelectedInnovationAreaName() {
        return selectedInnovationAreaName;
    }

    public void setSelectedInnovationAreaName(String selectedInnovationAreaName) {
        this.selectedInnovationAreaName = selectedInnovationAreaName;
    }

    public AssignButtonsViewModel getAssignButtonsViewModel() {
        return assignButtonsViewModel;
    }

    public void setAssignButtonsViewModel(AssignButtonsViewModel assignButtonsViewModel) {
        this.assignButtonsViewModel = assignButtonsViewModel;
    }

    /* view model methods */
    public String getResearchCategoryText() {
        return selectedResearchCategoryName != null ? "Change your research category" : "Choose your research category";
    }
    public boolean isResearchCategoryHasBeenSelected() {
        return selectedResearchCategoryName != null;
    }

    public String getInnovationAreaText() {
        return  selectedInnovationAreaName != null ? "Change your innovation area" : "Choose your innovation area";
    }

    public boolean isCanSelectInnovationArea() {
        return competition.getInnovationAreas().size() > 1;
    }

    public boolean isInnovationAreaHasBeenSelected() {
        return application.getNoInnovationAreaApplicable() || selectedInnovationAreaName != null;
    }

    public boolean isNoInnovationAreaApplicable() {
        return application.getNoInnovationAreaApplicable();
    }

}
