package org.innovateuk.ifs.application.forms.questions.applicationdetails.populator;

import org.innovateuk.ifs.application.forms.questions.applicationdetails.model.ApplicationDetailsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class ApplicationDetailsViewModelPopulator {

    @Autowired
    private QuestionStatusRestService questionStatusRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public ApplicationDetailsViewModel populate(ApplicationResource application, long questionId, UserResource user) {
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        OrganisationResource organisation = organisationRestService.getByUserAndApplicationId(user.getId(), application.getId()).getSuccess();

        boolean complete = isComplete(application, organisation, questionId);
        boolean open = application.isOpen() && competition.isOpen();

        return new ApplicationDetailsViewModel(application, competition, open, complete);
    }

    private boolean isComplete(ApplicationResource application, OrganisationResource organisation, long questionId) {
        try {
            return questionStatusRestService.getMarkedAsComplete(application.getId(), organisation.getId()).get().contains(questionId);
        } catch (InterruptedException | ExecutionException e) {
            throw new IFSRuntimeException(e);
        }
    }
}
