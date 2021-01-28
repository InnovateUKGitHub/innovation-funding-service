package org.innovateuk.ifs.project.monitoring.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.ProjectParticipantRole;
import org.innovateuk.ifs.project.core.mapper.ProjectMapper;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.monitoring.repository.MonitoringOfficerRepository;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerAssignedProjectResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerAssignmentResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerUnassignedProjectResource;
import org.innovateuk.ifs.project.monitoringofficer.transactional.LegacyMonitoringOfficerService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.transactional.RootTransactionalService;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.resource.SimpleUserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.Role.KNOWLEDGE_TRANSFER_ADVISER;
import static org.innovateuk.ifs.user.resource.Role.MONITORING_OFFICER;
import static org.innovateuk.ifs.user.resource.UserStatus.ACTIVE;
import static org.innovateuk.ifs.user.resource.UserStatus.PENDING;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class MonitoringOfficerServiceImpl extends RootTransactionalService implements MonitoringOfficerService {

    private static final Log LOG = LogFactory.getLog(MonitoringOfficerInviteService.class);

    @Autowired
    private MonitoringOfficerRepository monitoringOfficerRepository;
    @Autowired
    private MonitoringOfficerInviteService monitoringOfficerInviteService;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private LegacyMonitoringOfficerService legacyMonitoringOfficerService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public ServiceResult<List<SimpleUserResource>> findAll() {
        return serviceSuccess(userRepository.findDistinctByRolesInAndStatusIn(
                EnumSet.of(MONITORING_OFFICER, KNOWLEDGE_TRANSFER_ADVISER), EnumSet.of(PENDING, ACTIVE))
                .stream()
                .map(user -> new SimpleUserResource(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail()))
                .collect(Collectors.toList()));
    }

    @Override
    public ServiceResult<List<SimpleUserResource>> findAllKtp() {
        return serviceSuccess(userRepository.findDistinctByRolesInAndStatusIn(
                EnumSet.of(KNOWLEDGE_TRANSFER_ADVISER), EnumSet.of(PENDING, ACTIVE))
                .stream()
                .map(user -> new SimpleUserResource(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail()))
                .collect(Collectors.toList()));
    }

    @Override
    public ServiceResult<List<SimpleUserResource>> findAllNonKtp() {
        return serviceSuccess(userRepository.findDistinctByRolesInAndStatusIn(
                EnumSet.of(MONITORING_OFFICER), EnumSet.of(PENDING, ACTIVE))
                .stream()
                .map(user -> new SimpleUserResource(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail()))
                .collect(Collectors.toList()));
    }

    private MonitoringOfficerAssignmentResource mapToProjectMonitoringOfficerResource(User user) {
        List<MonitoringOfficerUnassignedProjectResource> unassignedProjects = new ArrayList<>();
        List<MonitoringOfficerAssignedProjectResource> assignedProjects = new ArrayList<>();

        if (user.hasRole(MONITORING_OFFICER) && user.hasRole(KNOWLEDGE_TRANSFER_ADVISER)) {
            unassignedProjects = monitoringOfficerRepository.findAllUnassignedProjects();
        assignedProjects = monitoringOfficerRepository.findAllAssignedProjects(user.getId());
        } else if (user.hasRole(MONITORING_OFFICER)) {
            unassignedProjects = monitoringOfficerRepository.findUnassignedNonKTPProjects();
            assignedProjects = monitoringOfficerRepository.findAssignedNonKTPProjects(user.getId());
        } else if (user.hasRole(KNOWLEDGE_TRANSFER_ADVISER)) {
            unassignedProjects = monitoringOfficerRepository.findUnassignedKTPProjects();
            assignedProjects = monitoringOfficerRepository.findAssignedKTPProjects(user.getId());
        }

        return new MonitoringOfficerAssignmentResource(user.getId(),
                user.getFirstName(),
                user.getLastName(),
                unassignedProjects,
                assignedProjects);
    }

    @Override
    @Transactional
    public ServiceResult<MonitoringOfficerAssignmentResource> getProjectMonitoringOfficer(long userId) {
        return getMonitoringOfficerUser(userId)
                .andOnSuccessReturn(this::mapToProjectMonitoringOfficerResource);
    }

    @Override
    @Transactional
    public ServiceResult<Void> assignProjectToMonitoringOfficer(long userId, long projectId) {
        return getMonitoringOfficerUser(userId)
                .andOnSuccess(user -> find(projectRepository.findById(projectId), notFoundError(Project.class))
                        .andOnSuccess(project -> (monitoringOfficerInviteService.inviteMonitoringOfficer(user, project))
                                .andOnSuccessReturnVoid(() -> monitoringOfficerRepository.save(new MonitoringOfficer(user, project)))
                        )
                );
    }

    @Override
    @Transactional
    public ServiceResult<Void> unassignProjectFromMonitoringOfficer(long userId, long projectId) {
        monitoringOfficerRepository.deleteByUserIdAndProjectId(userId, projectId);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<List<ProjectResource>> getMonitoringOfficerProjects(long userId) {
        List<MonitoringOfficer> monitoringOfficers = monitoringOfficerRepository.findByUserId(userId);
        return serviceSuccess(monitoringOfficers.stream()
                .map(MonitoringOfficer::getProcess)
                .map(projectMapper::mapToResource)
                .collect(toList()));
    }

    @Override
    public ServiceResult<MonitoringOfficerResource> findMonitoringOfficerForProject(long projectId) {
        Optional<MonitoringOfficer> monitoringOfficer = monitoringOfficerRepository.findOneByProjectIdAndRole(projectId, ProjectParticipantRole.MONITORING_OFFICER);
        if (monitoringOfficer.isPresent()) {
            return toMonitoringOfficerResource(monitoringOfficer.get(), projectId);
        } else {
            return legacyMonitoringOfficer(projectId);
        }
    }

    @Override
    public ServiceResult<Boolean> isMonitoringOfficerOnProject(long projectId, long userId) {
        return serviceSuccess(monitoringOfficerRepository.existsByProjectIdAndUserId(projectId, userId));
    }

    @Override
    public ServiceResult<Boolean> isMonitoringOfficer(long userId) {
        return serviceSuccess(monitoringOfficerRepository.existsByUserId(userId));
    }

    private ServiceResult<MonitoringOfficerResource> toMonitoringOfficerResource(MonitoringOfficer monitoringOfficer, long projectId) {
        return serviceSuccess(new MonitoringOfficerResource(monitoringOfficer.getId(),
                monitoringOfficer.getUser().getFirstName(),
                monitoringOfficer.getUser().getLastName(),
                monitoringOfficer.getUser().getEmail(),
                monitoringOfficer.getUser().getPhoneNumber(),
                projectId));
    }

    private ServiceResult<MonitoringOfficerResource> legacyMonitoringOfficer(long projectId) {
        return legacyMonitoringOfficerService.getMonitoringOfficer(projectId)
                .andOnSuccessReturn(legacyMonitoringOfficer -> new MonitoringOfficerResource(legacyMonitoringOfficer.getId(),
                        legacyMonitoringOfficer.getFirstName(),
                        legacyMonitoringOfficer.getLastName(),
                        legacyMonitoringOfficer.getEmail(),
                        legacyMonitoringOfficer.getPhoneNumber(),
                        legacyMonitoringOfficer.getProject()));
    }

    private ServiceResult<User> getMonitoringOfficerUser(long userId) {
        return find(userRepository.findById(userId)
                        .filter(u -> u.hasRole(KNOWLEDGE_TRANSFER_ADVISER) || u.hasRole(MONITORING_OFFICER)),
                notFoundError(User.class, userId));
    }

}