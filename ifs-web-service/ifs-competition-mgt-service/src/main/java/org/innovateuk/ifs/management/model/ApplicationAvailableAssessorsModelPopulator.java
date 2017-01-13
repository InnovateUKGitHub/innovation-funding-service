package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.invite.resource.AvailableAssessorResource;
import org.innovateuk.ifs.management.viewmodel.ApplicationAvailableAssessorsRowViewModel;
import org.innovateuk.ifs.management.viewmodel.ApplicationAvailableAssessorsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

/**
 * Build the model for the available assessors table view.
 */
@Component
public class ApplicationAvailableAssessorsModelPopulator {

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    private static Map<String, Comparator<ApplicationAvailableAssessorsRowViewModel>> sortMap() {
        return Collections.unmodifiableMap(Stream.of(
                new AbstractMap.SimpleEntry<>("title", comparing(ApplicationAvailableAssessorsRowViewModel::getName)),
                new AbstractMap.SimpleEntry<>("skills", comparing(ApplicationAvailableAssessorsRowViewModel::getSkillAreas)),
                new AbstractMap.SimpleEntry<>("totalApplications", comparing(ApplicationAvailableAssessorsRowViewModel::getTotalApplications)),
                new AbstractMap.SimpleEntry<>("assignedApplications", comparing(ApplicationAvailableAssessorsRowViewModel::getTotalApplications)),
                new AbstractMap.SimpleEntry<>("acceptedApplications", comparing(ApplicationAvailableAssessorsRowViewModel::getTotalApplications)))
                .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));
    }

    public ApplicationAvailableAssessorsViewModel populateModel(CompetitionResource competition, String selectedSort) {
        return new ApplicationAvailableAssessorsViewModel(getAvailableAssessors(competition, selectedSort));
    }

    private List<ApplicationAvailableAssessorsRowViewModel> getAvailableAssessors(CompetitionResource competition, String selectedSort) {

        List<ApplicationAvailableAssessorsRowViewModel> availableAssessors =
                Arrays.asList(new ApplicationAvailableAssessorsRowViewModel("John Smith", "John's skills", 10, 4, 3),
                        new ApplicationAvailableAssessorsRowViewModel("Phil Jones", "Phil's skills", 6, 2, 1));
        availableAssessors.sort(sortMap().get(selectedSort));
        return availableAssessors;
    }

    private ApplicationAvailableAssessorsRowViewModel getRowViewModel(AvailableAssessorResource availableAssessorResource) {
        return new ApplicationAvailableAssessorsRowViewModel("John Smith", "skills", 10, 4, 3);
    }
}
