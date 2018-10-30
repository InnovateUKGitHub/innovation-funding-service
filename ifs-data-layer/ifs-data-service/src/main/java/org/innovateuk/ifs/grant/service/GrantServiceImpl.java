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
        grant.setParticipants(
                project.getOrganisations().stream()
                        .map(o -> toParticipant(project.getId(), o))
                        .collect(Collectors.toSet())
        );
        grantEndpoint.send(grant);
        return serviceSuccess();
    }

    private Participant toParticipant(long projectId, Organisation organisation) {
        Participant participant = new Participant();
        participant.setId(organisation.getId());
        Optional<SpendProfile> spendProfile = spendProfileRepository
                .findOneByProjectIdAndOrganisationId(projectId, organisation.getId());
        if (!spendProfile.isPresent()) {
            throw new IllegalStateException("Project " + projectId + " and organisation "
                    + organisation.getId() + " does not have a spend profile.  All organisations MUST "
                    + "have a spend profile to send grant");
        }
        participant.setForecasts(
                spendProfile.get().getSpendProfileFigures().getCosts().stream()
                .map(this::toForecast).collect(Collectors.toSet())
        );
        return participant;
    }

    private Forecast toForecast(Cost c) {
        Forecast forecast = new Forecast();
        forecast.setValue(c.getValue());
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
}
