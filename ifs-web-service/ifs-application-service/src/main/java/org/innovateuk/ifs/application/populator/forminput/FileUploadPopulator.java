package org.innovateuk.ifs.application.populator.forminput;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.applicant.resource.ApplicantQuestionResource;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
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
        return FormInputType.FILEUPLOAD;
    }

    @Override
    protected void populate(AbstractApplicantResource resource, FileUploadInputViewModel viewModel) {
        viewModel.setViewmode(isReadOnlyViewMode(viewModel, resource) ? "readonly" : "edit");
        if (viewModel.getHasResponse()) {
            viewModel.setFilename(viewModel.getResponse().getFilename());
        }
        viewModel.setApplication(resource.getApplication());
    }

    private boolean isReadOnlyViewMode(FileUploadInputViewModel viewModel, AbstractApplicantResource resource) {
        return viewModel.isReadonly() || viewModel.isComplete()
                || !isAssignedToCurrentApplicant(viewModel.getApplicantQuestion(), resource.getCurrentApplicant())
                || !leadApplicantAndUnassigned(viewModel.getApplicantQuestion(), resource.getCurrentApplicant());

    }

    private boolean leadApplicantAndUnassigned(ApplicantQuestionResource applicantQuestion, ApplicantResource currentApplicant) {
        return !applicantQuestion.getApplicantQuestionStatuses().stream().noneMatch(status -> status.getAssignee() != null) && currentApplicant.isLead();
    }

    private boolean isAssignedToCurrentApplicant(ApplicantQuestionResource applicantQuestion, ApplicantResource currentApplicant) {
        return applicantQuestion.allAssignedStatuses().anyMatch(status -> status.getAssignee().isSameUser(currentApplicant));
    }

    @Override
    protected FileUploadInputViewModel createNew() {
        return new FileUploadInputViewModel();
    }
}
