package org.innovateuk.ifs.application.viewmodel.forminput;


import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputType;

public class ApplicationDetailsInputViewModel extends AbstractFormInputViewModel {

    //summary??
    //readonly is dependant on lead applicant ability.
    private ApplicationResource applicationResource;
    private CompetitionResource competitionResource;

    private String selectedResearchCategoryName;
    private String researchCategoryText; //model.selectedResearchCategoryName != null ? 'Change your research category' : 'Choose your research category'


    private boolean canSelectInnovationArea; //#sets.size(model.competition.innovationAreas) > 1
    private boolean innovationAreaHasBeenSelected; //See other view model.
    private boolean noInnovationAreaApplicable; //See other view model.
    private boolean selectedInnovationAreaName;
    private String innovationAreaText; // model.selectedInnovationAreaName != null ? 'Change your innovation area' : 'Choose your innovation area'

    private boolean footerUnless; //currentUser.id != questionAssignee?.assigneeUserId and questionAssignee?.assignee!=null) or (questionAssignee?.assignee==null and !userIsLeadApplicant)
    @Override
    protected FormInputType formInputType() {
        return FormInputType.APPLICATION_DETAILS;
    }

}
