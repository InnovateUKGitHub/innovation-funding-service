package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.NiDeclarationReadOnlyViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.util.Collections.singleton;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.NORTHERN_IRELAND_DECLARATION;

@Component
public class NiDeclarationReadOnlyPopulator implements QuestionReadOnlyViewModelPopulator<NiDeclarationReadOnlyViewModel> {

    @Override
    public NiDeclarationReadOnlyViewModel populate(QuestionResource question, ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        return new NiDeclarationReadOnlyViewModel(
                data,
                question
        );
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return singleton(NORTHERN_IRELAND_DECLARATION);
    }
}