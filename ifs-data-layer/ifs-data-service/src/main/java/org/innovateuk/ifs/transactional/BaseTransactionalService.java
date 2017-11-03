package org.innovateuk.ifs.transactional;

import org.innovateuk.ifs.address.repository.AddressTypeRepository;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.Question;
import org.innovateuk.ifs.application.domain.Section;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.QuestionRepository;
import org.innovateuk.ifs.application.repository.SectionRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.EligibilityWorkflowHandler;
import org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration.ViabilityWorkflowHandler;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_OPEN;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_OPEN_OR_LATER;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.SPEND_PROFILE_CANNOT_BE_GENERATED_UNTIL_ALL_ELIGIBILITY_APPROVED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.SPEND_PROFILE_CANNOT_BE_GENERATED_UNTIL_ALL_VIABILITY_APPROVED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.SPEND_PROFILE_HAS_ALREADY_BEEN_GENERATED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * This class represents the base class for transactional services.  Method calls within this service will have
 * transaction boundaries provided to allow for safe atomic operations and persistence cascading.
 */
public abstract class BaseTransactionalService extends RootTransactionalService {

    @Autowired
    protected CompetitionRepository competitionRepository;

    @Autowired
    protected ApplicationRepository applicationRepository;

    @Autowired
    protected ProjectRepository projectRepository;

    @Autowired
    protected SectionRepository sectionRepository;

    @Autowired
    protected OrganisationRepository organisationRepository;

    @Autowired
    protected AddressTypeRepository addressTypeRepository;

    @Autowired
    private ViabilityWorkflowHandler viabilityWorkflowHandler;

    @Autowired
    private EligibilityWorkflowHandler eligibilityWorkflowHandler;

    @Autowired
    private SpendProfileRepository spendProfileRepository;

    @Autowired
    private QuestionRepository questionRepository;


    protected Supplier<ServiceResult<Section>> section(final Long id) {
        return () -> getSection(id);
    }

    protected Supplier<ServiceResult<Application>> application(final Long id) {
        return () -> getApplication(id);
    }

    protected Supplier<ServiceResult<Project>> project(final Long id) {
        return () -> getProject(id);
    }

    protected ServiceResult<Project> getProject(final Long id) {
        return find(projectRepository.findOne(id), notFoundError(Project.class, id));
    }

    protected final Supplier<ServiceResult<Application>> openApplication(long applicationId) {
        return () -> getOpenApplication(applicationId);
    }

    protected final ServiceResult<Application> getOpenApplication(long applicationId) {
        return find(application(applicationId)).andOnSuccess(application -> {
                    if (application.getCompetition() != null && !OPEN.equals(application.getCompetition().getCompetitionStatus())) {
                        return serviceFailure(COMPETITION_NOT_OPEN);
                    } else {
                        return serviceSuccess(application);
                    }
                }
        );
    }

    protected final ServiceResult<Application> getOpenOrLaterApplication(long applicationId) {
        return find(application(applicationId)).andOnSuccess(application -> {
                    if (application.getCompetition() != null && application.getCompetition().getCompetitionStatus().ordinal() >= OPEN.ordinal()) {
                        return serviceSuccess(application);
                    } else {
                        return serviceFailure(COMPETITION_NOT_OPEN_OR_LATER);
                    }
                }
        );
    }

    protected ServiceResult<Void> canSpendProfileCanBeGenerated(Project project) {
        return (isViabilityApprovedOrNotApplicable(project))
                .andOnSuccess(() -> isEligibilityApprovedOrNotApplicable(project))
                .andOnSuccess(() -> isSpendProfileAlreadyGenerated(project));
    }

    private ServiceResult<Void> isViabilityApprovedOrNotApplicable(Project project) {

        List<PartnerOrganisation> partnerOrganisations = project.getPartnerOrganisations();

        Optional<PartnerOrganisation> existingReviewablePartnerOrganisation = simpleFindFirst(partnerOrganisations, partnerOrganisation ->
                ViabilityState.REVIEW == viabilityWorkflowHandler.getState(partnerOrganisation));

        if (!existingReviewablePartnerOrganisation.isPresent()) {
            return serviceSuccess();
        } else {
            return serviceFailure(SPEND_PROFILE_CANNOT_BE_GENERATED_UNTIL_ALL_VIABILITY_APPROVED);
        }
    }

    private ServiceResult<Void> isEligibilityApprovedOrNotApplicable(Project project) {

        List<PartnerOrganisation> partnerOrganisations = project.getPartnerOrganisations();

        Optional<PartnerOrganisation> existingReviewablePartnerOrganisation = simpleFindFirst(partnerOrganisations, partnerOrganisation ->
                EligibilityState.REVIEW == eligibilityWorkflowHandler.getState(partnerOrganisation));

        if (!existingReviewablePartnerOrganisation.isPresent()) {
            return serviceSuccess();
        } else {
            return serviceFailure(SPEND_PROFILE_CANNOT_BE_GENERATED_UNTIL_ALL_ELIGIBILITY_APPROVED);
        }
    }

    private ServiceResult<Void> isSpendProfileAlreadyGenerated(Project project) {
        List<PartnerOrganisation> partnerOrganisations = project.getPartnerOrganisations();

        Optional<PartnerOrganisation> partnerOrganisationWithSpendProfile = simpleFindFirst(partnerOrganisations, partnerOrganisation ->
                spendProfileRepository.findOneByProjectIdAndOrganisationId(project.getId(), partnerOrganisation.getOrganisation().getId()).isPresent());

        if (!partnerOrganisationWithSpendProfile.isPresent()) {
            return serviceSuccess();
        } else {
            return serviceFailure(SPEND_PROFILE_HAS_ALREADY_BEEN_GENERATED);
        }
    }

    protected ServiceResult<Application> getApplication(final Long id) {
        return find(applicationRepository.findOne(id), notFoundError(Application.class, id));
    }

    protected ServiceResult<Section> getSection(final Long id) {
        return find(sectionRepository.findOne(id), notFoundError(Section.class, id));
    }

    protected Supplier<ServiceResult<Competition>> competition(final Long id) {
        return () -> getCompetition(id);
    }

    protected ServiceResult<Competition> getCompetition(final Long id) {
        return find(competitionRepository.findOne(id), notFoundError(Competition.class, id));
    }

    protected Supplier<ServiceResult<Organisation>> organisation(Long id) {
        return () -> getOrganisation(id);
    }

    protected ServiceResult<Organisation> getOrganisation(Long id) {
        return find(organisationRepository.findOne(id), notFoundError(Organisation.class, id));
    }

    protected Supplier<ServiceResult<Question>> question(Long questionId) {
        return () -> getQuestion(questionId);
    }

    protected ServiceResult<Question> getQuestion(Long questionId) {
        return find(questionRepository.findOne(questionId), notFoundError(Question.class));
    }
}
