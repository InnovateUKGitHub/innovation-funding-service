package org.innovateuk.ifs.application.populator.forminput;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.populator.AssignButtonsPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.viewmodel.forminput.ApplicationDetailsInputViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ApplicationDetailsPopulator extends AbstractFormInputPopulator<AbstractApplicantResource, ApplicationDetailsInputViewModel> {

    @Autowired
    private AssignButtonsPopulator assignButtonsPopulator;

    @Override
    public FormInputType type() {
        return FormInputType.APPLICATION_DETAILS;
    }

    @Override
    protected void populate(AbstractApplicantResource resource, ApplicationDetailsInputViewModel viewModel) {
        viewModel.setReadonly(viewModel.isReadonly() || !resource.getCurrentApplicant().isLead());
        viewModel.setApplication(resource.getApplication());
        viewModel.setCompetition(resource.getCompetition());
        viewModel.setSelectedResearchCategoryName(resource.getApplication().getResearchCategory().getName());
        viewModel.setSelectedInnovationAreaName(resource.getApplication().getInnovationArea().getName());
        viewModel.setAssignButtonsViewModel(assignButtonsPopulator.populate(resource, viewModel.getApplicantQuestion(), viewModel.isComplete()));
    }

    @Override
    public void addToForm(ApplicationForm form, ApplicationDetailsInputViewModel viewModel) {
        Map<String, String> formInputs = form.getFormInput();
        ApplicationResource application = viewModel.getApplication();
        formInputs.put("application_details-title", application.getName());
        formInputs.put("application_details-duration", String.valueOf(application.getDurationInMonths()));
        if(application.getStartDate() == null){
            formInputs.put("application_details-startdate_day", "");
            formInputs.put("application_details-startdate_month", "");
            formInputs.put("application_details-startdate_year", "");
        }else{
            formInputs.put("application_details-startdate_day", String.valueOf(application.getStartDate().getDayOfMonth()));
            formInputs.put("application_details-startdate_month", String.valueOf(application.getStartDate().getMonthValue()));
            formInputs.put("application_details-startdate_year", String.valueOf(application.getStartDate().getYear()));
        }
        form.setFormInput(formInputs);
        form.setApplication(application);
    }

    @Override
    protected ApplicationDetailsInputViewModel createNew() {
        return new ApplicationDetailsInputViewModel();
    }
}
