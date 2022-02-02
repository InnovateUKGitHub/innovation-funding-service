package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.common.viewmodel.ApplicationSubsidyBasisViewModel;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class NorthernIrelandDeclarationReadOnlyViewModel extends GenericQuestionReadOnlyViewModel {

    final ApplicationSubsidyBasisViewModel applicationSubsidyBasisViewModel;

    public NorthernIrelandDeclarationReadOnlyViewModel(ApplicationReadOnlyData data,
            GenericQuestionReadOnlyViewModel genericQuestionReadOnlyViewModel,
            ApplicationSubsidyBasisViewModel applicationSubsidyBasisViewModel) {
        super(data,
                genericQuestionReadOnlyViewModel.getQuestionResource(),
                genericQuestionReadOnlyViewModel.getDisplayName(),
                genericQuestionReadOnlyViewModel.getQuestion(),
                genericQuestionReadOnlyViewModel.isMultipleStatuses(),
                genericQuestionReadOnlyViewModel.getAnswer(),
                genericQuestionReadOnlyViewModel.getAnswers(),
                genericQuestionReadOnlyViewModel.isStatusDetailPresent(),
                genericQuestionReadOnlyViewModel.getAppendices(),
                genericQuestionReadOnlyViewModel.getTemplateFile(),
                genericQuestionReadOnlyViewModel.getTemplateDocumentTitle(),
                genericQuestionReadOnlyViewModel.getFeedback(),
                genericQuestionReadOnlyViewModel.getScores(),
                genericQuestionReadOnlyViewModel.getInScope(),
                genericQuestionReadOnlyViewModel.getTotalScope(),
                genericQuestionReadOnlyViewModel.hasScope(),
                genericQuestionReadOnlyViewModel.isLoanPartBEnabled()
        );
        this.applicationSubsidyBasisViewModel = applicationSubsidyBasisViewModel;
    }

    @Override
    public boolean shouldDisplayMarkAsComplete() {
        return isLead() && !applicationSubsidyBasisViewModel.isSubsidyBasisCompletedByLeadOrganisation();
    }

    @Override
    public boolean isComplete() {
        return applicationSubsidyBasisViewModel.isSubsidyBasisCompletedByAllOrganisations();
    }
}
