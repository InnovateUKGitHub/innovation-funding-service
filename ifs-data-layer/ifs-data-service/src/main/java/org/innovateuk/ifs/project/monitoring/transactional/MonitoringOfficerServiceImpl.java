package org.innovateuk.ifs.project.monitoring.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.core.ProjectParticipantRole;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.mapper.ProjectMapper;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.monitoring.repository.MonitoringOfficerRepository;
import org.innovateuk.ifs.project.monitoring.resource.*;
import org.innovateuk.ifs.project.monitoringofficer.transactional.LegacyMonitoringOfficerService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.transactional.RootTransactionalService;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.resource.SimpleUserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    private MonitoringOfficerReviewNotificationService monitoringOfficerReviewNotificationService;

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
    public ServiceResult<MonitoringOfficerDashboardPageResource> filterMonitoringOfficerProjects(long userId, String keywordSearch, boolean projectInSetup, boolean previousProject, int pageIndex, int pageSize) {
        List<ProjectState> projectStates = applyProjectStatesFilter(projectInSetup, previousProject);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        Page<MonitoringOfficer> pagedMonitoringOfficers = getMonitoringOfficersFilteredProjects(userId, keywordSearch, projectStates, pageable);
        Page<MonitoringOfficer> NonPagedMonitoringOfficers = getMonitoringOfficersFilteredProjects(userId, keywordSearch, projectStates, null);

        List<ProjectResource> pagedProjectResourcesList =  pagedMonitoringOfficers.stream()
                .map(MonitoringOfficer::getProcess)
                .map(projectMapper::mapToResource)
                .collect(toList());

        List<ProjectResource> nonPagedProjectResourcesList =  NonPagedMonitoringOfficers.stream()
                .map(MonitoringOfficer::getProcess)
                .map(projectMapper::mapToResource)
                .collect(toList());

        MonitoringOfficerDashboardPageResource monitoringOfficerDashboardPageResource = new MonitoringOfficerDashboardPageResource();
        monitoringOfficerDashboardPageResource.setContent(pagedProjectResourcesList);
        monitoringOfficerDashboardPageResource.setNumber(pageable.getPageNumber());
        monitoringOfficerDashboardPageResource.setSize(pageable.getPageSize());
        monitoringOfficerDashboardPageResource.setTotalElements(nonPagedProjectResourcesList.size());
        monitoringOfficerDashboardPageResource.setTotalPages((nonPagedProjectResourcesList.size() + pageable.getPageSize() - 1) / pageable.getPageSize());
        return find(monitoringOfficerDashboardPageResource, notFoundError(MonitoringOfficerDashboardPageResource.class));
    }

    private Page<MonitoringOfficer> getMonitoringOfficersFilteredProjects(long userId, String keywordSearch, List<ProjectState> projectStates, Pageable pageable) {
        Page<MonitoringOfficer> monitoringOfficers;
        if (keywordSearch != null && !keywordSearch.isEmpty()) {
            monitoringOfficers = monitoringOfficerRepository.filterMonitoringOfficerProjectsByKeywordsByStates(userId, keywordSearch, projectStates, pageable);
        } else {
            monitoringOfficers = monitoringOfficerRepository.filterMonitoringOfficerProjectsByStates(userId, projectStates, pageable);
        }
        return monitoringOfficers;
    }

    private List<ProjectState> applyProjectStatesFilter(boolean projectInSetup, boolean previousProject) {
        List<ProjectState> projectStates = new ArrayList<>();

        if (!previousProject && !projectInSetup) {
            projectStates.addAll(Stream.of(ProjectState.values())
                    .collect(Collectors.toList()));
        }

        if (previousProject) {
            projectStates.addAll(ProjectState.COMPLETED_STATES);
        }

        if (projectInSetup) {
            projectStates.addAll(Stream.of(ProjectState.values())
                    .filter(projectState -> !projectState.isComplete())
                    .collect(Collectors.toList()));
        }

        return projectStates;
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

    @Override
    public ServiceResult<Void> sendDocumentReviewNotification(long projectId, long userId) {
              return getMonitoringOfficerUser(userId)
                        .andOnSuccess(user -> find(projectRepository.findById(projectId), notFoundError(Project.class))
                                .andOnSuccess(project -> (monitoringOfficerReviewNotificationService.sendDocumentReviewNotification(user, project.getId()))
                                .andOnSuccessReturnVoid())
                        );
            }
}