package org.innovateuk.ifs.application.populator.forminput;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.application.viewmodel.forminput.TextInputViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;

@Component
public class TextInputViewModelPopulator extends AbstractFormInputPopulator<AbstractApplicantResource, TextInputViewModel> {

    @Override
    protected void populate(AbstractApplicantResource resource, TextInputViewModel viewModel) {
        //nothing to add.
    }

    @Override
    public FormInputType type() {
        return FormInputType.TEXTINPUT;
    }


    @Override
    protected TextInputViewModel createNew() {
        return new TextInputViewModel();
    }
}
