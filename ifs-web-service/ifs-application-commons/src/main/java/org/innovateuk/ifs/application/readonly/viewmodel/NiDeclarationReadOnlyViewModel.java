package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.form.resource.QuestionResource;

public class NiDeclarationReadOnlyViewModel extends AbstractQuestionReadOnlyViewModel {

    public NiDeclarationReadOnlyViewModel(ApplicationReadOnlyData data,
                                         QuestionResource question) {
        super(data, question);
    }

    @Override
    public String getFragment() {
        return "ni-declaration";
    }
}