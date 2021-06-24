package org.innovateuk.ifs.project.monitoringofficer.populator;

import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerSummaryViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MonitoringOfficerSummaryViewModelPopulator {

    @Autowired
    private MonitoringOfficerRestService monitoringOfficerRestService;

    public MonitoringOfficerSummaryViewModelPopulator() {
    }

    public MonitoringOfficerSummaryViewModel populate(UserResource user) {
        List<ProjectResource> projects = monitoringOfficerRestService.getProjectsForMonitoringOfficer(user.getId()).getSuccess();
        return new MonitoringOfficerSummaryViewModel(getInSetupProjectCount(projects), getPreviousProjectCount(projects), getDocumentsComplete(projects), getDocumentsInComplete(projects), getDocumentsAwaitingReview(projects));
    }

    public MonitoringOfficerSummaryViewModel populate(List<ProjectResource> projects) {
        return new MonitoringOfficerSummaryViewModel(getInSetupProjectCount(projects), getPreviousProjectCount(projects), getDocumentsComplete(projects), getDocumentsInComplete(projects), getDocumentsAwaitingReview(projects));
    }

    public int getInSetupProjectCount(List<ProjectResource> projects) {
        List<ProjectResource> inSetupProjects = projects.stream()
                .filter(project -> !project.getProjectState().isComplete())
                .collect(Collectors.toList());

        return inSetupProjects.size();
    }

    public int getPreviousProjectCount(List<ProjectResource> projects) {
        List<ProjectResource> previousProjects = projects.stream()
                .filter(project -> project.getProjectState().isComplete())
                .collect(Collectors.toList());

        return previousProjects.size();
    }

    public int getDocumentsComplete(List<ProjectResource> projects) {
        List<ProjectResource> documentsComplete = projects.stream()
                .filter(project -> project.getProjectDocuments().containsAll(Collections.singleton(DocumentStatus.APPROVED)))
                .collect(Collectors.toList());

        return documentsComplete.size();
    }

    public int getDocumentsInComplete(List<ProjectResource> projects) {
        List<ProjectResource> documentsInComplete = projects.stream()
                .filter(project -> project.getProjectDocuments().containsAll(Collections.singleton(DocumentStatus.UNSET)))
                .collect(Collectors.toList());

        return documentsInComplete.size();
    }

    public int getDocumentsAwaitingReview(List<ProjectResource> projects) {
        List<ProjectResource> documentsAwaitingReview = projects.stream()
                .filter(project -> project.getProjectDocuments().contains(Collections.singleton(DocumentStatus.SUBMITTED)))
                .collect(Collectors.toList());

        return documentsAwaitingReview.size();
    }
}
