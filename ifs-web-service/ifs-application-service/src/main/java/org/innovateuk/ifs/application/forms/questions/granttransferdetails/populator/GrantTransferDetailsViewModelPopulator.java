package org.innovateuk.ifs.application.forms.questions.granttransferdetails.populator;

import org.innovateuk.ifs.application.forms.questions.granttransferdetails.viewmodel.GrantTransferDetailsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.granttransfer.resource.EuActionTypeResource;
import org.innovateuk.ifs.granttransfer.service.ActionTypeRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class GrantTransferDetailsViewModelPopulator {

    private final ApplicationRestService applicationRestService;

    private final QuestionStatusRestService questionStatusRestService;

    private final OrganisationRestService organisationRestService;

    private final CompetitionRestService competitionRestService;

    private final ActionTypeRestService euActionTypeRestService;

    public GrantTransferDetailsViewModelPopulator(ApplicationRestService applicationRestService, QuestionStatusRestService questionStatusRestService, OrganisationRestService organisationRestService, CompetitionRestService competitionRestService, ActionTypeRestService euActionTypeRestService) {
        this.applicationRestService = applicationRestService;
        this.questionStatusRestService = questionStatusRestService;
        this.organisationRestService = organisationRestService;
        this.competitionRestService = competitionRestService;
        this.euActionTypeRestService = euActionTypeRestService;
    }

    public GrantTransferDetailsViewModel populate(long applicationId, long questionId, long userId) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        OrganisationResource organisation = organisationRestService.getByUserAndApplicationId(userId, applicationId).getSuccess();
        List<EuActionTypeResource> actionTypes = euActionTypeRestService.findAll().getSuccess();

        boolean complete = isComplete(application, organisation, questionId);
        boolean open = application.isOpen() && competition.isOpen();
        return new GrantTransferDetailsViewModel(application.getId(), application.getName(), questionId,
                open, complete, actionTypes);
    }

    private boolean isComplete(ApplicationResource application, OrganisationResource organisation, long questionId) {
        try {
            return questionStatusRestService.getMarkedAsComplete(application.getId(), organisation.getId()).get().contains(questionId);
        } catch (InterruptedException | ExecutionException e) {
            throw new IFSRuntimeException(e);
        }
    }
}
