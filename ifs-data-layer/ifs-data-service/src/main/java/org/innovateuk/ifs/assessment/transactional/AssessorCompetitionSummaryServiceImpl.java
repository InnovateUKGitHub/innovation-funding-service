package org.innovateuk.ifs.assessment.transactional;

import com.google.common.collect.Sets;
import org.innovateuk.ifs.assessment.domain.AssessmentApplicationAssessorCount;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.assessment.resource.AssessorAssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorCompetitionSummaryResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.*;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.REJECTED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

@Service
public class AssessorCompetitionSummaryServiceImpl implements AssessorCompetitionSummaryService {

    public static final Set<State> INVALID_ASSESSMENT_STATES = AssessmentState.getBackingStates(EnumSet.of(
            AssessmentState.WITHDRAWN,
            AssessmentState.REJECTED
    ));

    public static final Set<State> VALID_ASSESSMENT_STATES = AssessmentState.getBackingStates(EnumSet.of(
            AssessmentState.CREATED,
            AssessmentState.OPEN,
            AssessmentState.PENDING,
            AssessmentState.ACCEPTED,
            AssessmentState.DECIDE_IF_READY_TO_SUBMIT,
            AssessmentState.READY_TO_SUBMIT,
            AssessmentState.SUBMITTED
    ));

    public static final Set<State> ALL_ASSESSMENT_STATES = Sets.union(VALID_ASSESSMENT_STATES, INVALID_ASSESSMENT_STATES);

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
                            VALID_ASSESSMENT_STATES
                    );

                    List<AssessmentApplicationAssessorCount> counts = assessmentRepository
                            .getAssessorApplicationAssessmentCountsForStates(
                                    competition.getId(),
                                    assessorId,
                                    VALID_ASSESSMENT_STATES,
                                    ALL_ASSESSMENT_STATES
                            );

                    List<AssessorAssessmentResource> assessorAssessments = mapCountsToResource(counts)
                            .stream()
                            .collect(collectingAndThen(toCollection(()
                                            ->  new TreeSet<>(comparingLong(AssessorAssessmentResource::getApplicationId))),
                                    ArrayList::new))
                            .stream()
                            .sorted(comparingLong(AssessorAssessmentResource::getApplicationId))
                            .collect(toList());

                    return serviceSuccess(new AssessorCompetitionSummaryResource(
                            competition.getId(),
                            competition.getName(),
                            competition.getCompetitionStatus(),
                            assessorProfile,
                            allAssessmentCount,
                            assessorAssessments
                    ));
                })
        );
    }

    private List<AssessorAssessmentResource> mapCountsToResource(List<AssessmentApplicationAssessorCount> counts) {
        return simpleMap(counts, count -> {
            AssessmentRejectOutcomeValue assessmentRejectOutcomeValue = null;
            String comment = null;

            if (count.getAssessment().getActivityState() == REJECTED) {
                assessmentRejectOutcomeValue = count.getAssessment().getRejection().getRejectReason();
                comment = count.getAssessment().getRejection().getRejectComment();
            }

            Organisation leadOrganisation = organisationRepository.findOne(
                    count.getApplication().getLeadOrganisationId()
            );

            return new AssessorAssessmentResource(
                    count.getApplication().getId(),
                    count.getApplication().getName(),
                    leadOrganisation.getName(),
                    count.getAssessorCount(),
                    count.getAssessment().getActivityState(),
                    assessmentRejectOutcomeValue,
                    comment,
                    count.getAssessment().getId()
            );
        });
    }
}
