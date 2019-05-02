package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.application.summary.ApplicationSummaryData;
import org.innovateuk.ifs.form.resource.QuestionResource;

public class NewGrantAgreementSummaryViewModel extends AbstractQuestionSummaryViewModel implements ApplicationRowSummaryViewModel {

    private final String filename;

    public NewGrantAgreementSummaryViewModel(ApplicationSummaryData data, QuestionResource question, String filename) {
        super(data, question);
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public String getFragment() {
        return "grant-agreement";
    }
}
