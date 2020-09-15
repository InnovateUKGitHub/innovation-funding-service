package org.innovateuk.ifs.application.forms.sections.financesoverview.populator;

import org.innovateuk.ifs.application.finance.populator.ApplicationFinanceSummaryViewModelPopulator;
import org.innovateuk.ifs.application.finance.populator.ApplicationFundingBreakdownViewModelPopulator;
import org.innovateuk.ifs.application.forms.sections.financesoverview.viewmodel.FinancesOverviewViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.application.service.SectionStatusRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

import static com.google.common.base.Strings.isNullOrEmpty;

@Component
public class FinancesOverviewModelPopulator {

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private SectionRestService sectionRestService;

    @Autowired
    private SectionStatusRestService sectionStatusRestService;

    @Autowired
    private ApplicationFinanceSummaryViewModelPopulator applicationFinanceSummaryViewModelPopulator;

    @Autowired
    private ApplicationFundingBreakdownViewModelPopulator applicationFundingBreakdownViewModelPopulator;

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private MessageSource messageSource;

    public FinancesOverviewViewModel populate(long applicationId, long sectionId, UserResource user) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        Optional<QuestionResource> question = questionRestService.getQuestionsBySectionIdAndType(sectionId, QuestionType.GENERAL).getSuccess()
                .stream()
                .filter(q -> !isNullOrEmpty(q.getDescription()))
                .findFirst();
        Double researchParticipationPercentage = applicationFinanceRestService.getResearchParticipationPercentage(applicationId).getSuccess();
        return new FinancesOverviewViewModel(
                applicationId,
                competition.getName(),
                application.getName(),
                researchParticipationPercentage,
                competition.getMaxResearchRatio(),
                competition.getFundingType(),
                getHint(application),
                question.map(QuestionResource::getDescription).orElse(null),
                applicationFinanceSummaryViewModelPopulator.populate(applicationId, user),
                applicationFundingBreakdownViewModelPopulator.populate(applicationId, user)
        );
    }

    private String getHint(ApplicationResource application) {
        return application.isCollaborativeProject() ?
                messageSource.getMessage("ifs.section.financesOverview.collaborative.description", null,
                        Locale.getDefault()) :
                messageSource.getMessage("ifs.section.financesOverview.description", null, Locale.getDefault());
    }
}
