package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.mapper.ApplicationAssessorMapper;
import org.innovateuk.ifs.application.mapper.ApplicationAssessorPageMapper;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.application.resource.ApplicationAvailableAssessorPageResource;
import org.innovateuk.ifs.application.resource.ApplicationAvailableAssessorResource;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.util.EncodingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;

import java.text.Collator;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.ASSESSOR;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.springframework.data.domain.Sort.Direction.ASC;

/**
 * Service for retrieving {@link ApplicationAssessmentSummaryResource}s.
 */
@Service
public class ApplicationAssessmentSummaryServiceImpl extends BaseTransactionalService implements ApplicationAssessmentSummaryService {

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private AssessmentParticipantRepository assessmentParticipantRepository;

    @Autowired
    private ApplicationAssessorMapper applicationAssessorMapper;

    @Autowired
    private ApplicationAssessorPageMapper applicationAssessorPageMapper;

    @Override
    public ServiceResult<ApplicationAvailableAssessorPageResource> getAvailableAssessors(long applicationId, int pageIndex, int pageSize, String assessorNameFilter, ApplicationAvailableAssessorResource.Sort sort) {

        return find(applicationRepository.findById(applicationId), notFoundError(Application.class, applicationId)).andOnSuccessReturn(application -> {
                    Pageable pageable = PageRequest.of(pageIndex, pageSize, getSort(sort));

                    Page<ApplicationAvailableAssessorResource> result = assessmentParticipantRepository.findAvailableAssessorsForApplication(
                            application.getCompetition().getId(),
                            applicationId,
                            EncodingUtils.urlDecode(assessorNameFilter),
                            pageable);
                    return new ApplicationAvailableAssessorPageResource(result.getTotalElements(), result.getTotalPages(), result.getContent(), result.getNumber(), result.getSize());
                }
        );
    }

    @Override
    public ServiceResult<List<Long>> getAvailableAssessorIds(long applicationId, String assessorNameFilter) {
        return find(applicationRepository.findById(applicationId), notFoundError(Application.class, applicationId)).andOnSuccessReturn(application ->
            assessmentParticipantRepository.findAvailableAssessorIdsForApplication(
                    application.getCompetition().getId(),
                    applicationId,
                    EncodingUtils.urlDecode(assessorNameFilter)
            )
        );
    }

    private Sort getSort(ApplicationAvailableAssessorResource.Sort sort) {
        switch(sort) {
            case ASSESSOR:
                return new Sort(ASC, "user.firstName", "user.lastName");
            case SKILL_AREAS:
                return new Sort(ASC, "profile.skillsAreas");
            case TOTAL_APPLICATIONS:
                return JpaSort.unsafe(ASC, AssessmentParticipantRepository.totalApplications);
            case ASSIGNED:
                return JpaSort.unsafe(ASC, AssessmentParticipantRepository.assigned);
            case SUBMITTED:
                return JpaSort.unsafe(ASC, AssessmentParticipantRepository.submitted);
        }
        throw new IFSRuntimeException("Unknown sort type " + sort.name());
    }

    @Override
    public ServiceResult<List<ApplicationAssessorResource>> getAssignedAssessors(long applicationId) {
        return find(applicationRepository.findById(applicationId), notFoundError(Application.class, applicationId)).andOnSuccessReturn(application ->
                simpleMap(assessmentParticipantRepository.findParticipantsWithAssessments(application.getCompetition().getId(), ASSESSOR, ParticipantStatus.ACCEPTED, applicationId),
                        competitionParticipant -> {
                            Optional<Assessment> mostRecentAssessment = getMostRecentAssessment(competitionParticipant, applicationId);
                            return applicationAssessorMapper.mapToResource(competitionParticipant, mostRecentAssessment);
                        })
        );
    }

    @Override
    public ServiceResult<ApplicationAssessmentSummaryResource> getApplicationAssessmentSummary(long applicationId) {
        return getApplication(applicationId).andOnSuccessReturn(application -> {
            Competition competition = application.getCompetition();
            return new ApplicationAssessmentSummaryResource(application.getId(),
                    application.getName(),
                    getInnovationArea(application),
                    competition.getId(),
                    competition.getName(),
                    competition.getCompetitionStatus(),
                    getLeadOrganisationName(application),
                    getPartnerOrganisationNames(application));
        });
    }

    private String getInnovationArea(Application application) {
        return application.getInnovationArea() != null ? application.getInnovationArea().getName() : null;
    }

    private List<String> getPartnerOrganisationNames(Application application) {
        Optional<Long> leadOrgId = getLeadOrganisationId(application);
        return application.getProcessRoles().stream()
                .filter(ProcessRole::isCollaborator)
                .filter(processRole -> !isLeadOrgId(leadOrgId, processRole))
                .map(ProcessRole::getOrganisationId)
                .distinct()
                .map(orgId -> organisationRepository.findById(orgId).get().getName())
                .sorted(Collator.getInstance())
                .collect(toList());
    }

    private boolean isLeadOrgId(Optional<Long> leadOrgId, ProcessRole processRole) {
        return leadOrgId.map(idValue -> idValue.equals(processRole.getOrganisationId())).orElse(false);
    }

    private Optional<Long> getLeadOrganisationId(Application application){
        return application.getProcessRoles().stream()
                .filter(ProcessRole::isLeadApplicant)
                .findFirst()
                .map(ProcessRole::getOrganisationId);
    }

    private String getLeadOrganisationName(Application application) {
        return getLeadOrganisationId(application)
                .map(orgId -> organisationRepository.findById(orgId).get().getName())
                .orElse("");
    }

    private Optional<Assessment> getMostRecentAssessment(CompetitionParticipant competitionParticipant, Long applicationId) {
        return assessmentRepository.findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(competitionParticipant.getUser().getId(), applicationId);
    }
}
