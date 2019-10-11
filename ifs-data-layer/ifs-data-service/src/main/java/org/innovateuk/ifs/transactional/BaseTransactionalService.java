package org.innovateuk.ifs.transactional;

import org.innovateuk.ifs.address.repository.AddressTypeRepository;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.domain.Section;
import org.innovateuk.ifs.form.repository.QuestionRepository;
import org.innovateuk.ifs.form.repository.SectionRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.PartnerOrganisationRepository;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_OPEN;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.COMPETITION_NOT_OPEN_OR_LATER;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.OPEN;
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
    protected PartnerOrganisationRepository partnerOrganisationRepository;

    @Autowired
    protected AddressTypeRepository addressTypeRepository;

    @Autowired
    private QuestionRepository questionRepository;


    protected Supplier<ServiceResult<Section>> section(long id) {
        return () -> getSection(id);
    }

    protected Supplier<ServiceResult<Application>> application(long id) {
        return () -> getApplication(id);
    }

    protected Supplier<ServiceResult<Project>> project(long id) {
        return () -> getProject(id);
    }

    protected ServiceResult<Project> getProject(long id) {
        return find(projectRepository.findById(id), notFoundError(Project.class, id));
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

    protected ServiceResult<Application> getApplication(long id) {
        return find(applicationRepository.findById(id), notFoundError(Application.class, id));
    }

    protected ServiceResult<Section> getSection(long id) {
        return find(sectionRepository.findById(id), notFoundError(Section.class, id));
    }

    protected Supplier<ServiceResult<Competition>> competition(long id) {
        return () -> getCompetition(id);
    }

    protected ServiceResult<Competition> getCompetition(long id) {
        return find(competitionRepository.findById(id), notFoundError(Competition.class, id));
    }

    protected Supplier<ServiceResult<Organisation>> organisation(long id) {
        return () -> getOrganisation(id);
    }

    protected ServiceResult<Organisation> getOrganisation(long id) {
        return find(organisationRepository.findById(id), notFoundError(Organisation.class, id));
    }

    protected ServiceResult<PartnerOrganisation> getPartnerOrganisation(long projectId, long organisationId) {
        return find(partnerOrganisationRepository.findOneByProjectIdAndOrganisationId(projectId, organisationId), notFoundError(PartnerOrganisation.class, projectId, organisationId));
    }

    protected Supplier<ServiceResult<Question>> question(long questionId) {
        return () -> getQuestion(questionId);
    }

    protected ServiceResult<Question> getQuestion(long questionId) {
        return find(questionRepository.findById(questionId), notFoundError(Question.class));
    }
}