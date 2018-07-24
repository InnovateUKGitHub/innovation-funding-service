package org.innovateuk.ifs.application.overview.populator;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewAssignableViewModel;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewSectionViewModel;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewUserViewModel;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewViewModel;
import org.innovateuk.ifs.application.populator.AbstractApplicationModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationCompletedModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.ApplicationCompletedViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.form.resource.SectionType.FINANCE;


/**
 * view model for the application overview page
 */
@Component
public class ApplicationOverviewModelPopulator extends AbstractApplicationModelPopulator {

    private CompetitionRestService competitionRestService;
    private ProcessRoleService processRoleService;
    private OrganisationService organisationService;
    private ProjectService projectService;
    private SectionService sectionService;
    private QuestionService questionService;
    private ApplicationOverviewSectionModelPopulator applicationOverviewSectionModelPopulator;
    private ApplicationCompletedModelPopulator applicationCompletedModelPopulator;
    private ApplicationOverviewAssignableModelPopulator applicationOverviewAssignableModelPopulator;
    private ApplicationOverviewUserModelPopulator applicationOverviewUserModelPopulator;

    public ApplicationOverviewModelPopulator(CompetitionRestService competitionRestService,
                                             ProcessRoleService processRoleService,
                                             OrganisationService organisationService,
                                             SectionService sectionService,
                                             QuestionService questionService,
                                             ProjectService projectService,
                                             ApplicationOverviewSectionModelPopulator applicationOverviewSectionModelPopulator,
                                             ApplicationCompletedModelPopulator applicationCompletedModelPopulator,
                                             ApplicationOverviewAssignableModelPopulator applicationOverviewAssignableModelPopulator,
                                             ApplicationOverviewUserModelPopulator applicationOverviewUserModelPopulator) {
        super(sectionService, questionService);
        this.competitionRestService = competitionRestService;
        this.processRoleService = processRoleService;
        this.sectionService = sectionService;
        this.organisationService = organisationService;
        this.projectService = projectService;
        this.applicationOverviewSectionModelPopulator = applicationOverviewSectionModelPopulator;
        this.applicationCompletedModelPopulator = applicationCompletedModelPopulator;
        this.applicationOverviewAssignableModelPopulator = applicationOverviewAssignableModelPopulator;
        this.applicationOverviewUserModelPopulator = applicationOverviewUserModelPopulator;
    }

    public ApplicationOverviewViewModel populateModel(ApplicationResource application, Long userId, ApplicationForm form){
        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();
        List<ProcessRoleResource> userApplicationRoles = processRoleService.findProcessRolesByApplicationId(application.getId());
        Optional<OrganisationResource> userOrganisation = organisationService.getOrganisationForUser(userId, userApplicationRoles);
        ProjectResource projectResource = projectService.getByApplicationId(application.getId());
        boolean projectWithdrawn = (projectResource != null && projectResource.isWithdrawn());

        ApplicationOverviewUserViewModel userViewModel = applicationOverviewUserModelPopulator.populate(application, userId);
        ApplicationOverviewAssignableViewModel assignableViewModel = applicationOverviewAssignableModelPopulator.populate(application, userOrganisation, userId);
        ApplicationCompletedViewModel completedViewModel = applicationCompletedModelPopulator.populate(application, userOrganisation);
        ApplicationOverviewSectionViewModel sectionViewModel = applicationOverviewSectionModelPopulator.populate(competition, application, userId);
        Long yourFinancesSectionId = getYourFinancesSectionId(application);

        int completedQuestionsPercentage = application.getCompletion() == null ? 0 : application.getCompletion().intValue();

        return new ApplicationOverviewViewModel(
                application.getId(),
                application.getName(),
                application.getApplicationState(),
                application.isSubmitted(),
                projectWithdrawn,
                competition,
                userOrganisation.orElse(null),
                completedQuestionsPercentage,
                yourFinancesSectionId,
                userViewModel,
                assignableViewModel,
                completedViewModel,
                sectionViewModel);
    }

    private Long getYourFinancesSectionId(ApplicationResource application) {

        return sectionService.getAllByCompetitionId(application.getCompetition())
                .stream()
                .filter(section -> section.getType().equals(FINANCE))
                .findFirst()
                .map(SectionResource::getId)
                .orElse(null);
    }

}
