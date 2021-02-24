package org.innovateuk.ifs.application.readonly.populator;

import org.innovateuk.ifs.application.common.populator.ApplicationSubsidyBasisModelPopulator;
import org.innovateuk.ifs.application.common.populator.ApplicationSubsidyBasisPartnerModelPopulator;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.viewmodel.SubsidyBasisReadOnlyViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.util.Collections.singleton;

@Component
public class SubsidyBasisReadOnlyViewModelPopulator implements QuestionReadOnlyViewModelPopulator<SubsidyBasisReadOnlyViewModel> {

    @Autowired
    ApplicationSubsidyBasisPartnerModelPopulator applicationSubsidyBasisParnterPopulator;

    @Autowired
    ApplicationSubsidyBasisModelPopulator applicationSubsidyBasisPopulator;

    @Override
    public SubsidyBasisReadOnlyViewModel populate(CompetitionResource competition, QuestionResource question, ApplicationReadOnlyData data, ApplicationReadOnlySettings settings) {
        return new SubsidyBasisReadOnlyViewModel(
                data,
                question,
                applicationSubsidyBasisParnterPopulator.populate(),
                applicationSubsidyBasisPopulator.populate(data.getApplication().getId(), question.getId()));
    }

    @Override
    public Set<QuestionSetupType> questionTypes() {
        return singleton(QuestionSetupType.SUBSIDY_BASIS);
    }
    }