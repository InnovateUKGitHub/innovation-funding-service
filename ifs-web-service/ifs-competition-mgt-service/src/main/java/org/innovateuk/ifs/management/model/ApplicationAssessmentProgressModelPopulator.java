package org.innovateuk.ifs.management.model;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.application.service.ApplicationAssessmentSummaryRestService;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.invite.resource.AvailableAssessorResource;
import org.innovateuk.ifs.management.viewmodel.ApplicationAssessmentProgressAssignedRowViewModel;
import org.innovateuk.ifs.management.viewmodel.ApplicationAssessmentProgressViewModel;
import org.innovateuk.ifs.management.viewmodel.ApplicationAvailableAssessorsRowViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Build the model for the Application Progress view.
 */
@Component
public class ApplicationAssessmentProgressModelPopulator {

    @Autowired
    private ApplicationAssessmentSummaryRestService applicationAssessmentSummaryRestService;

    private static Map<String, Comparator<ApplicationAvailableAssessorsRowViewModel>> sortMap() {
        return Collections.unmodifiableMap(Stream.of(
                new AbstractMap.SimpleEntry<>("title", comparing(ApplicationAvailableAssessorsRowViewModel::getName)),
                new AbstractMap.SimpleEntry<>("skills", comparing(ApplicationAvailableAssessorsRowViewModel::getSkillAreas)),
                new AbstractMap.SimpleEntry<>("totalApplications", comparing(ApplicationAvailableAssessorsRowViewModel::getTotalApplicationsCount)),
                new AbstractMap.SimpleEntry<>("assignedApplications", comparing(ApplicationAvailableAssessorsRowViewModel::getAssignedCount)),
                new AbstractMap.SimpleEntry<>("submittedApplications", comparing(ApplicationAvailableAssessorsRowViewModel::getSubmittedApplications)))
                .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));
    }

    public ApplicationAssessmentProgressViewModel populateModel(Long applicationId, String sortSelection) {
        ApplicationAssessmentSummaryResource applicationAssessmentSummary = applicationAssessmentSummaryRestService.getApplicationAssessmentSummary(applicationId).getSuccessObjectOrThrowException();

        List<ApplicationAssessorResource> assessors = applicationAssessmentSummaryRestService.getAssessors(applicationId).getSuccessObjectOrThrowException();
        Map<Boolean, List<ApplicationAssessorResource>> assessorsPartitionedByAvailable = assessors.stream().collect(partitioningBy(ApplicationAssessorResource::isAvailable));
        List<ApplicationAssessorResource> notAvailableAssessors = assessorsPartitionedByAvailable.getOrDefault(Boolean.FALSE, Collections.emptyList());
        List<ApplicationAssessorResource> availableAssessors = assessorsPartitionedByAvailable.getOrDefault(Boolean.TRUE, Collections.emptyList());
        String sortField = sortFieldForAvailableAssessors(sortSelection);

        return new ApplicationAssessmentProgressViewModel(applicationAssessmentSummary.getId(),
                applicationAssessmentSummary.getName(),
                applicationAssessmentSummary.getCompetitionId(),
                applicationAssessmentSummary.getCompetitionName(),
                applicationAssessmentSummary.getPartnerOrganisations(),
                getAssignedAssessors(notAvailableAssessors),
                getSortedAvailableAssessors(availableAssessors, sortFieldForAvailableAssessors(sortSelection)),
                sortField);
    }

    private List<ApplicationAssessmentProgressAssignedRowViewModel> getAssignedAssessors(List<ApplicationAssessorResource> assessors) {
        return assessors.stream()
                .filter(ApplicationAssessorResource::isAssigned)
                .map(this::getAssignedRowViewModel)
                .collect(toList());
    }

    private ApplicationAssessmentProgressAssignedRowViewModel getAssignedRowViewModel(ApplicationAssessorResource applicationAssessorResource) {
        return new ApplicationAssessmentProgressAssignedRowViewModel(applicationAssessorResource.getFirstName() + " " + applicationAssessorResource.getLastName(),
                applicationAssessorResource.getTotalApplicationsCount(),
                applicationAssessorResource.getAssignedCount(),
                applicationAssessorResource.getBusinessType(),
                simpleMap(applicationAssessorResource.getInnovationAreas(), CategoryResource::getName),
                applicationAssessorResource.isNotified(),
                applicationAssessorResource.isAccepted(),
                applicationAssessorResource.isStarted(),
                applicationAssessorResource.isSubmitted());
    }

    private List<ApplicationAvailableAssessorsRowViewModel> getSortedAvailableAssessors(List<ApplicationAssessorResource> assessors, String selectedSort) {
        List<ApplicationAvailableAssessorsRowViewModel> available = assessors.stream()
                .map(this::getAvailableRowViewModel)
                .collect(toList());
        available.sort(sortMap().get(selectedSort));
        return available;
    }

    private ApplicationAvailableAssessorsRowViewModel getAvailableRowViewModel(ApplicationAssessorResource applicationAssessorResource) {
        return new ApplicationAvailableAssessorsRowViewModel(applicationAssessorResource.getFirstName() + " " + applicationAssessorResource.getLastName(),
                defaultString(applicationAssessorResource.getSkillAreas()),
                applicationAssessorResource.getTotalApplicationsCount(),
                applicationAssessorResource.getAssignedCount(),
                applicationAssessorResource.getSubmittedCount());
    }

    private String sortFieldForAvailableAssessors(String sort) {
        return activeSortField(sort,  "title", "skills", "totalApplications", "assignedApplications", "acceptedApplications");
    }

    private String activeSortField(String givenField, String defaultField, String... allowedFields) {
        return Arrays.stream(allowedFields)
                .filter(field -> givenField != null && givenField.equals(field))
                .findAny()
                .orElse(defaultField);
    }
}