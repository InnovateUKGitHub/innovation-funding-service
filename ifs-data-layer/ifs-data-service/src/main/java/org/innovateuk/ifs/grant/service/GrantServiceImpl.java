package org.innovateuk.ifs.grant.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.sil.grant.resource.Forecast;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.innovateuk.ifs.sil.grant.resource.Participant;
import org.innovateuk.ifs.sil.grant.service.GrantEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class GrantServiceImpl implements GrantService {
    private static final Log LOG = LogFactory.getLog(GrantServiceImpl.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SpendProfileRepository spendProfileRepository;

    @Autowired
    private GrantEndpoint grantEndpoint;

    @Override
    @Transactional
    public ServiceResult<Void> sendProject(Long applicationId) {
        LOG.info("Sending project : " + applicationId);
        Project project = projectRepository.findOneByApplicationId(applicationId);
        Grant grant = new Grant();
        grant.setId(project.getId());
        grant.setCompetitionCode(project.getApplication().getCompetition().getCode());
        grant.setTitle(project.getName());
        grant.setGrantOfferLetterDate(project.getOfferSubmittedDate());
        grant.setStartDate(project.getTargetStartDate());
        grant.setDuration(project.getDurationInMonths());
        Context context = new Context()
                .withProjectId(project.getId())
                .withStartDate(project.getTargetStartDate());
        grant.setParticipants(
                project.getOrganisations().stream()
                        .map(o -> toParticipant(context, o))
                        .collect(Collectors.toSet())
        );
        grantEndpoint.send(grant);
        return serviceSuccess();
    }

    private Participant toParticipant(Context context, Organisation organisation) {
        Participant participant = new Participant();
        participant.setId(organisation.getId());
        participant.setType(organisation.getOrganisationType().getName());
        participant.setSize(organisation.getUsers().size());
        Optional<SpendProfile> spendProfile = spendProfileRepository
                .findOneByProjectIdAndOrganisationId(context.getProjectId(), organisation.getId());
        if (!spendProfile.isPresent()) {
            throw new IllegalStateException("Project " + context.getProjectId() + " and organisation "
                    + organisation.getId() + " does not have a spend profile.  All organisations MUST "
                    + "have a spend profile to send grant");
        }
        participant.setForecasts(
                spendProfile.get().getSpendProfileFigures().getCosts().stream()
                .map(c -> toForecast(context, c)).collect(Collectors.toSet())
        );
        return participant;
    }

    private Forecast toForecast(Context context, Cost c) {
        Forecast forecast = new Forecast();
        forecast.setValue(c.getValue());
        forecast.setCostCategory(c.getCostCategory().getName());
        forecast.setStart(c.getCostTimePeriod().getStartDate(context.getStartDate()));
        forecast.setEnd(c.getCostTimePeriod().getEndDate(context.getStartDate()));
        return forecast;
    }

    @Override
    @Transactional
    public ServiceResult<Void> sendReadyProjects() {
        List<Project> readyProjects = projectRepository.findReadyToSend();
        LOG.info("Sending " + readyProjects.size() + " projects");
        readyProjects.forEach(it -> sendProject(it.getApplication().getId()));
        return serviceSuccess();
    }

    private static class Context {
        private long projectId;
        private LocalDate startDate;

        Context withProjectId(long projectId) {
            this.projectId = projectId;
            return this;
        }

        Context withStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        long getProjectId() {
            return projectId;
        }

        LocalDate getStartDate() {
            return startDate;
        }
    }
}
