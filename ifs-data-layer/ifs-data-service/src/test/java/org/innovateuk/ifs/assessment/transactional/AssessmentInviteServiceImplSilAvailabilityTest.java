package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.assessment.domain.AssessmentInvite;
import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.assessment.repository.AssessmentInviteRepository;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.sil.AbstractSilAvailabilityIntegrationTest;
import org.innovateuk.ifs.testdata.services.TestService;
import org.innovateuk.ifs.testutil.DatabaseTestHelper;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Consumer;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.NOTIFICATIONS_UNABLE_TO_SEND_SINGLE;
import static org.innovateuk.ifs.commons.service.ServiceFailureTestHelper.assertThatServiceFailureIs;
import static org.innovateuk.ifs.invite.builder.AssessorInviteSendResourceBuilder.newAssessorInviteSendResource;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

/**
 * TODO DW - document this class
 */
public class AssessmentInviteServiceImplSilAvailabilityTest extends AbstractSilAvailabilityIntegrationTest {

    @Autowired
    private AssessmentInviteService assessmentInviteService;

    @Autowired
    private AssessmentInviteRepository assessmentInviteRepository;

    @Autowired
    private InnovationAreaRepository innovationAreaRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private AssessmentParticipantRepository assessmentParticipantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseTestHelper databaseTestHelper;

    @Autowired
    private TestService testService;

    @Test
    public void resendInvite() {

        withNewAssessmentInvite(invite -> {

            withMockSilEmailRestTemplate(mockEmailSilRestTemplate -> {

                setupServiceUnavailableResponseExpectationsFromSendEmailCall(mockEmailSilRestTemplate);

                testService.doWithinTransaction(this::loginCompAdmin);

                databaseTestHelper.assertingNoDatabaseChangesOccur(() -> {

                    AssessorInviteSendResource inviteContent = newAssessorInviteSendResource().
                            withSubject("A subject").
                            withContent("Some content").
                            build();

                    ServiceResult<Void> result = assessmentInviteService.resendInvite(invite.getId(), inviteContent);

                    assertThatServiceFailureIs(result, new Error(NOTIFICATIONS_UNABLE_TO_SEND_SINGLE, SERVICE_UNAVAILABLE));

                    verifyServiceUnavailableResponseExpectationsFromSendEmailCall(mockEmailSilRestTemplate);
                });
            });
        });
    }

    private void withNewAssessmentInvite(Consumer<AssessmentInvite> consumer) {

        AssessmentInvite invite = null;
        Long competitionId = null;

        try {
            Competition competition = competitionRepository.findAll().get(0);
            competitionId = competition.getId();

            InnovationArea innovationArea = innovationAreaRepository.findAll().get(0);

            invite = testService.doWithinTransaction(() -> {
                AssessmentInvite newInvite = new AssessmentInvite("Invite name", "asdf@example.com", "hash", competition, innovationArea);
                User compAdmin = testService.doWithinTransaction(() -> userRepository.findOne(getCompAdmin().getId()));
                newInvite.send(compAdmin, ZonedDateTime.now());

                AssessmentParticipant participant = new AssessmentParticipant(newInvite);
                assessmentInviteRepository.save(newInvite);
                assessmentParticipantRepository.save(participant);

                return newInvite;
            });

            // call the method under test
            consumer.accept(invite);

        } finally {

            Long compId = competitionId;

            testService.doWithinTransaction(() -> {
                List<AssessmentParticipant> participantRecords = assessmentParticipantRepository.getByCompetitionIdAndRole(compId, CompetitionParticipantRole.ASSESSOR);
                assessmentParticipantRepository.delete(participantRecords);
            });

            assessmentInviteRepository.delete(invite);
        }
    }
}
