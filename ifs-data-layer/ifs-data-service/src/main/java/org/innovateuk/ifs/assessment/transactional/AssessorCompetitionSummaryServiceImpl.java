package org.innovateuk.ifs.assessment.transactional;

import com.google.common.collect.Sets;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentApplicationAssessorCount;
import org.innovateuk.ifs.assessment.domain.AssessmentRejectOutcome;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.assessment.resource.AssessorAssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorCompetitionSummaryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.REJECTED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Service
public class AssessorCompetitionSummaryServiceImpl implements AssessorCompetitionSummaryService {

    public static final Set<State> INVALID_ASSESSMENT_STATES = AssessmentStates.getBackingStates(EnumSet.of(
            AssessmentStates.WITHDRAWN,
            AssessmentStates.REJECTED
    ));

    public static final Set<State> VALID_ASSESSMENT_STATES = Sets.complementOf(INVALID_ASSESSMENT_STATES);

    public static final Set<State> INCLUDED_ASSESSMENT_STATES = Sets.union(VALID_ASSESSMENT_STATES,
            AssessmentStates.getBackingStates(EnumSet.of(AssessmentStates.REJECTED)));

    @Autowired
    private AssessorService assessorService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Transactional(readOnly = true)
    @Override
    public ServiceResult<AssessorCompetitionSummaryResource> getAssessorSummary(long assessorId, long competitionId) {
        return assessorService.getAssessorProfile(assessorId).andOnSuccess(assessorProfile ->
                competitionService.getCompetitionById(competitionId).andOnSuccess(competition -> {
                    long allAssessmentCount = assessmentRepository.countByParticipantUserIdAndActivityStateStateIn(
                            assessorProfile.getUser().getId(),
                            INCLUDED_ASSESSMENT_STATES
                    );

                    List<AssessmentApplicationAssessorCount> counts = assessmentRepository
                            .getAssessorApplicationAssessmentCountsForStates(
                                    competition.getId(),
                                    assessorId,
                                    INCLUDED_ASSESSMENT_STATES
                            );

                    return serviceSuccess(new AssessorCompetitionSummaryResource(
                            competition.getId(),
                            competition.getName(),
                            competition.getCompetitionStatus(),
                            assessorProfile,
                            allAssessmentCount,
                            mapCountsToResource(counts)
                    ));
                })
        );
    }

    private List<AssessorAssessmentResource> mapCountsToResource(List<AssessmentApplicationAssessorCount> counts) {
        return simpleMap(counts, count -> {
            AssessmentRejectOutcomeValue assessmentRejectOutcomeValue = null;
            String comment = null;
            int assessorCount = count.getAssessorCount();

            if (count.getAssessment().getActivityState() == REJECTED) {
                assessmentRejectOutcomeValue = count.getAssessment().getRejection().getRejectReason();
                comment = count.getAssessment().getRejection().getRejectComment();
                assessorCount--;
            }

            Organisation leadOrganisation = organisationRepository.findOne(
                    count.getApplication().getLeadOrganisationId()
            );

            return new AssessorAssessmentResource(
                    count.getApplication().getId(),
                    count.getApplication().getName(),
                    leadOrganisation.getName(),
                    assessorCount,
                    count.getAssessment().getActivityState(),
                    assessmentRejectOutcomeValue,
                    comment,
                    count.getAssessment().getId()
            );
        });
    }
}
