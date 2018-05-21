package org.innovateuk.ifs.interview.model;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.interview.resource.InterviewApplicationSentInviteResource;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.interview.viewmodel.InterviewAssignmentApplicationsSentInviteViewModel;
import org.innovateuk.ifs.invite.resource.ApplicantInterviewInviteResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Optional.ofNullable;

@Component
public class InterviewApplicationSentInviteModelPopulator {

    private CompetitionRestService competitionRestService;
    private InterviewAssignmentRestService assignmentRestService;
    private ApplicationService applicationService;

    @Autowired
    public InterviewApplicationSentInviteModelPopulator(CompetitionRestService competitionRestService, InterviewAssignmentRestService assignmentRestService, ApplicationService applicationService) {
        this.competitionRestService = competitionRestService;
        this.assignmentRestService = assignmentRestService;
        this.applicationService = applicationService;
    }


    public InterviewAssignmentApplicationsSentInviteViewModel populate(long competitionId, long applicationId, String originQuery) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        InterviewApplicationSentInviteResource invite = assignmentRestService.getSentInvite(applicationId).getSuccess();
        ApplicationResource application = applicationService.getById(applicationId);
        ApplicantInterviewInviteResource emailTemplate = assignmentRestService.getEmailTemplate().getSuccess();
        FileEntryResource feedback = assignmentRestService.findFeedback(applicationId).getSuccess();
        OrganisationResource organisationResource = applicationService.getLeadOrganisation(applicationId);

        return new InterviewAssignmentApplicationsSentInviteViewModel(competitionId,
                competition.getName(),
                applicationId,
                application.getName(),
                organisationResource.getName(),
                invite.getAssigned(),
                ofNullable(feedback).map(FileEntryResource::getName).orElse(null),
                invite.getSubject(),
                emailTemplate.getContent(),
                invite.getContent(),
                originQuery);

    }
}