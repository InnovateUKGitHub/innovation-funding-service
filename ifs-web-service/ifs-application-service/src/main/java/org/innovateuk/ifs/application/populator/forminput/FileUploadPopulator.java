package org.innovateuk.ifs.application.populator.forminput;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.application.populator.AssignButtonsPopulator;
import org.innovateuk.ifs.application.viewmodel.forminput.FileUploadInputViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileUploadPopulator extends AbstractFormInputPopulator<AbstractApplicantResource, FileUploadInputViewModel> {

    @Autowired
    private AssignButtonsPopulator assignButtonsPopulator;

    @Override
    public FormInputType type() {
        return FormInputType.TEXTAREA;
    }

    @Override
    protected void populate(AbstractApplicantResource resource, FileUploadInputViewModel viewModel) {
        viewModel.setFilename(viewModel.getResponse().getFilename());
        viewModel.getDownloadUrl(viewModel.getResponse().getFilename());

        //${isCompManagementDownload} ? @{${currentApplication.id + '/forminput/' + formInput.id + '/download'}} : @{${'/application/' + currentApplication.id + '/form/question/' + question.id + '/forminput/' + formInput.id + '/download'}},


    }

    @Override
    protected FileUploadInputViewModel createNew() {
        return new FileUploadInputViewModel();
    }
}
