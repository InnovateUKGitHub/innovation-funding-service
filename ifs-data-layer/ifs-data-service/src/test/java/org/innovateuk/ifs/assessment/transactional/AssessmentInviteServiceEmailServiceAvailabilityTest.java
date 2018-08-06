package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.assessment.domain.AssessmentInvite;
import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.assessment.repository.AssessmentInviteRepository;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.sil.AbstractEmailServiceAvailabilityIntegrationTest;
import org.innovateuk.ifs.testdata.services.TestService;
import org.innovateuk.ifs.testutil.DatabaseTestHelper;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Consumer;

import static org.innovateuk.ifs.invite.builder.AssessorInviteSendResourceBuilder.newAssessorInviteSendResource;

/**
 * Tests that this Service will roll back its work if the email service is not available for sending out emails
 */
public class AssessmentInviteServiceEmailServiceAvailabilityTest extends AbstractEmailServiceAvailabilityIntegrationTest {

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

        withNewAssessmentInvite(invite ->

            withServiceUnavailableFromEmailService(() -> {

                testService.doWithinTransaction(this::loginCompAdmin);

                return databaseTestHelper.assertingNoDatabaseChangesOccur(() -> {

                    AssessorInviteSendResource inviteContent = newAssessorInviteSendResource().
                            withSubject("A subject").
                            withContent("Some content").
                            build();

                    return assessmentInviteService.resendInvite(invite.getId(), inviteContent);
                });
            })
        );
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
