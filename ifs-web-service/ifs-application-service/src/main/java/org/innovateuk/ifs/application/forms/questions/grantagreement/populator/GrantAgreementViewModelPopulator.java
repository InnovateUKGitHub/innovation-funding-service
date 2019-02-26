package org.innovateuk.ifs.application.forms.questions.grantagreement.populator;

import org.innovateuk.ifs.application.forms.questions.grantagreement.model.GrantAgreementViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.granttransfer.service.EuGrantTransferRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Component
public class GrantAgreementViewModelPopulator {

    private final ApplicationRestService applicationRestService;

    private final EuGrantTransferRestService euGrantTransferRestService;

    private final QuestionStatusRestService questionStatusRestService;

    private final OrganisationRestService organisationRestService;

    private final CompetitionRestService competitionRestService;

    public GrantAgreementViewModelPopulator(ApplicationRestService applicationRestService, EuGrantTransferRestService euGrantTransferRestService, QuestionStatusRestService questionStatusRestService, OrganisationRestService organisationRestService, CompetitionRestService competitionRestService) {
        this.applicationRestService = applicationRestService;
        this.euGrantTransferRestService = euGrantTransferRestService;
        this.questionStatusRestService = questionStatusRestService;
        this.organisationRestService = organisationRestService;
        this.competitionRestService = competitionRestService;
    }

    public GrantAgreementViewModel populate(long applicationId, long questionId, long userId) {
        ApplicationResource application = applicationRestService.getApplicationById(applicationId).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        Optional<FileEntryResource> file = euGrantTransferRestService.findGrantAgreement(applicationId).getOptionalSuccessObject();
        OrganisationResource organisation = organisationRestService.getByUserAndApplicationId(userId, applicationId).getSuccess();
        boolean complete = isComplete(application, organisation, questionId);
        boolean open = application.isOpen() && competition.isOpen();
        return new GrantAgreementViewModel(application.getId(), application.getName(), questionId, file.map(FileEntryResource::getName).orElse(null),
                open, complete);
    }

    private boolean isComplete(ApplicationResource application, OrganisationResource organisation, long questionId) {
        try {
            return questionStatusRestService.getMarkedAsComplete(application.getId(), organisation.getId()).get().contains(questionId);
        } catch (InterruptedException | ExecutionException e) {
            throw new IFSRuntimeException(e);
        }
    }
}
