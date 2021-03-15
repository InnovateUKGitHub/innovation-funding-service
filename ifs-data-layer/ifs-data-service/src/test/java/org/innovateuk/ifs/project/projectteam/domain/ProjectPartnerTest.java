package org.innovateuk.ifs.project.projectteam.domain;

import org.innovateuk.ifs.project.projectteam.builder.PendingPartnerProgressBuilder;
import org.junit.Test;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.APPLICATION_DETAILS;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.SUBSIDY_BASIS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProjectPartnerTest {

    @Test
    public void testIsSubsidyBasisRequired() {
        PendingPartnerProgress pendingPartnerProgressSubsidyRequired
                = PendingPartnerProgressBuilder.newPendingPartnerProgress()
                .withPartnerOrganisation(newPartnerOrganisation()
                        .withProject(newProject()
                                .withApplication(newApplication()
                                        .withCompetition(newCompetition()
                                                .withQuestions(singletonList(newQuestion()
                                                        // A SUBSIDY_BASIS question means that subsidy basis is required
                                                        .withQuestionSetupType(SUBSIDY_BASIS)
                                                        .build()))
                                                .build())
                                        .build())
                                .build())
                        .build())
                .build();

        assertTrue(pendingPartnerProgressSubsidyRequired.isSubsidyBasisRequired());
    }

    @Test
    public void testIsSubsidyBasisNotRequired() {
        PendingPartnerProgress pendingPartnerProgressSubsidyRequired
                = PendingPartnerProgressBuilder.newPendingPartnerProgress()
                .withPartnerOrganisation(newPartnerOrganisation()
                        .withProject(newProject()
                                .withApplication(newApplication()
                                        .withCompetition(newCompetition()
                                                .withQuestions(singletonList(newQuestion()
                                                        // No SUBSIDY_BASIS question means that subsidy basis is not required
                                                        .withQuestionSetupType(APPLICATION_DETAILS)
                                                        .build()))
                                                .build())
                                        .build())
                                .build())
                        .build())
                .build();

        assertFalse(pendingPartnerProgressSubsidyRequired.isSubsidyBasisRequired());
    }
}