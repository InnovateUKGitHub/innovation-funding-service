package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.common.populator.ApplicationSubsidyBasisModelPopulator;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.NorthernIrelandDeclarationReadOnlyViewModel;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.singleton;

@Component
public class NorthernIrelandDeclarationReadOnlyViewModelPopulator implements QuestionReadOnlyViewModelPopulator<NorthernIrelandDeclarationReadOnlyViewModel> {

    @Autowired
    GenericQuestionReadOnlyViewModelPopulator genericQuestionReadOnlyViewModelPopulator;

    @Autowired
    ApplicationSubsidyBasisModelPopulator applicationSubsidyBasisPopulator;

    @Override
    public NorthernIrelandDeclarationReadOnlyViewModel populate(QuestionResource question, ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        return new NorthernIrelandDeclarationReadOnlyViewModel(data,
                genericQuestionReadOnlyViewModelPopulator.populate(question, data, settings),
                applicationSubsidyBasisPopulator.populate(question, data.getApplication().getId()));
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return singleton(QuestionSetupType.NORTHERN_IRELAND_DECLARATION);
    }
}