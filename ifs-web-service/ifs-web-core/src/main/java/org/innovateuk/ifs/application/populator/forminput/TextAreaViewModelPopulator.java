package org.innovateuk.ifs.application.populator.forminput;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.application.populator.AssignButtonsPopulator;
import org.innovateuk.ifs.application.viewmodel.forminput.TextAreaInputViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Populator for text area form inputs.
 */
@Component
public class TextAreaViewModelPopulator extends AbstractFormInputPopulator<TextAreaInputViewModel> {

    @Autowired
    private AssignButtonsPopulator assignButtonsPopulator;

    @Override
    public FormInputType type() {
        return FormInputType.TEXTAREA;
    }

    @Override
    protected void populate(AbstractApplicantResource resource, TextAreaInputViewModel viewModel, boolean readOnly) {
        viewModel.setAssignButtonsViewModel(assignButtonsPopulator.populate(resource, viewModel.getApplicantQuestion(), viewModel.isComplete()));
        viewModel.setReadonly(viewModel.isReadonly() || !viewModel.getAssignButtonsViewModel().isAssignedToCurrentUser());
        viewModel.setApplication(resource.getApplication());
    }

    @Override
    protected TextAreaInputViewModel createNew() {
        return new TextAreaInputViewModel();
    }
}
