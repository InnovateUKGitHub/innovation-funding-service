package org.innovateuk.ifs.application.viewmodel.granttransfer;

import org.innovateuk.ifs.application.viewmodel.AbstractLeadOnlyViewModel;

public class GrantAgreementSummaryViewModel extends AbstractLeadOnlyViewModel {

    private final String filename;

    public GrantAgreementSummaryViewModel(Long questionId, Long applicationId, boolean closed, boolean complete, boolean canMarkAsComplete, boolean allReadOnly, String filename) {
        super(questionId, applicationId, closed, complete, canMarkAsComplete, allReadOnly);
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public boolean isSummary() {
        return true;
    }
}
