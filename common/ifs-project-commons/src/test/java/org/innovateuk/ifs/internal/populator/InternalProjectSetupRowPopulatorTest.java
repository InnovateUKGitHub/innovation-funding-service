package org.innovateuk.ifs.internal.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.internal.InternalProjectSetupCell;
import org.innovateuk.ifs.internal.InternalProjectSetupRow;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.project.builder.ProjectStatusResourceBuilder.newProjectStatusResource;
import static org.innovateuk.ifs.project.internal.ProjectSetupStage.PROJECT_DETAILS;
import static org.innovateuk.ifs.project.internal.ProjectSetupStage.PROJECT_TEAM;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InternalProjectSetupRowPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private InternalProjectSetupRowPopulator populator;

    @Test
    public void populate() {

        UserResource userResource = newUserResource().build();

        List<ProjectSetupStage> projectSetupStages = asList(PROJECT_DETAILS, PROJECT_TEAM);

        CompetitionResource competitionResource = newCompetitionResource()
                .withProjectSetupStages(projectSetupStages)
                .build();

        List<ProjectStatusResource> projectStatusResources = newProjectStatusResource()
                .withProjectTitles("project title")
                .withApplicationNumber(1L)
                .withProjectState(ProjectState.LIVE)
                .withNumberOfPartners(1)
                .withProjectLeadOrganisationName("Org name")
                .withProjectNumber(1L)
                .build(3);

        List<InternalProjectSetupRow> rows = populator.populate(projectStatusResources, competitionResource, userResource);

        Set<InternalProjectSetupCell> cells = rows.stream()
                .map(InternalProjectSetupRow::getStates)
                .findFirst().get();

        assertEquals(2, cells.size());

        List<ProjectSetupStage> stages = cells.stream()
                .map(InternalProjectSetupCell::getStage)
                .collect(Collectors.toList());

        assertTrue(stages.contains(PROJECT_DETAILS));
        assertTrue(stages.contains(PROJECT_TEAM));
    }
}
