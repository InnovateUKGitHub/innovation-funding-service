package org.innovateuk.ifs.application.populator.forminput;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.application.viewmodel.forminput.EmptyInputViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;

/**
 * Populator for empty form inputs.
 */
@Component
public class EmptyInputPopulator extends AbstractFormInputPopulator<EmptyInputViewModel> {

    @Override
    public FormInputType type() {
        return FormInputType.EMPTY;
    }

    @Override
    protected void populate(AbstractApplicantResource resource, EmptyInputViewModel viewModel) {}

    @Override
    protected EmptyInputViewModel createNew() {
        return new EmptyInputViewModel();
    }
}
