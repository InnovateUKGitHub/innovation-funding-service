package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.application.builder.FormInputResponseBuilder;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.project.core.builder.ProjectBuilder;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileRepository;
import org.innovateuk.ifs.sil.grant.resource.Grant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.mockito.Mockito.when;

public class GrantMapperTest {
    @Mock
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectFinanceRepository projectFinanceRepository;

    @Mock
    private ProjectFinanceRowRepository projectFinanceRowRepository;

    @Mock
    private SpendProfileRepository spendProfileRepository;

    @Mock
    private FormInputResponseRepository formInputResponseRepository;

    @InjectMocks
    protected GrantMapper grantMapper = new GrantMapper();

    @Before
    public void setupMockInjection() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMap() {
        Parameter parameter = parameters().iterator().next();
        Project project = parameter.project();
        when(formInputResponseRepository.findByApplicationId(project.getApplication().getId()))
                .thenReturn(parameter.formInputResponses());
        Grant grant = grantMapper.mapToGrant(project);
        assertThat(grant.getId(), equalTo(parameter.applicationId()));
        assertThat(grant.getCompetitionCode(), equalTo(parameter.competitionId()));
        assertThat(grant.getPublicDescription(), equalTo(parameter.publicDescription()));
        assertThat(grant.getSummary(), equalTo(parameter.projectSummary()));
    }

    @Parameterized.Parameters
    public static Collection<Parameter> parameters() {
        return Arrays.asList(
                newParameter(newProject().withId(1L).withDuration(12L),1L,2L)
        );
    }

    private static Parameter newParameter(ProjectBuilder projectBuilder, long applicationId, long competitionId) {
        return new Parameter().project(projectBuilder
                .withApplication(
                        newApplication()
                                .withId(applicationId)
                                .withCompetition(
                                        newCompetition().withId(competitionId).build())
                                .build())
                .build())
                .name("simple")
                .applicationId(applicationId)
                .competitionId(competitionId);
    }

    private static class Parameter {
        private Project project;
        private String name;
        private List<FormInputResponse> formInputResponses = new ArrayList<>();
        private long competitionId;
        private long applicationId;
        private String projectSummary;
        private String publicDescription;

        private Parameter project(Project project) {
            this.project = project;
            return this;
        }

        private Project project() {
            return project;
        }

        private Parameter applicationId(long applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        private long applicationId() {
            return applicationId;
        }

        private Parameter competitionId(long competitionId) {
            this.competitionId = competitionId;
            return this;
        }

        private long competitionId() {
            return competitionId;
        }

        private Parameter name(String name) {
            this.name = name;
            return this;
        }

        private String name() {
            return name;
        }

        private String publicDescription() {
            return publicDescription == null ? name + " public description" : publicDescription;
        }

        private String projectSummary() {
            return publicDescription == null ? name + " project summary" : projectSummary;
        }

        private List<FormInputResponse> formInputResponses() {
            List<FormInputResponse>  formInputResponses = new ArrayList<>();
            formInputResponses.add(createFormInputResponse("project summary"));
            formInputResponses.add(createFormInputResponse("public description"));
            return formInputResponses;
        }

        private FormInputResponse createFormInputResponse(String description) {
            return createFormInputResponse(description, name + " " + description);
        }

        private FormInputResponse createFormInputResponse(String description, String value) {
            FormInputResponse formInputResponse = new FormInputResponse();
            FormInput formInput = new FormInput();
            formInput.setDescription(description);
            formInputResponse.setFormInput(formInput);
            formInputResponse.setValue(value);
            return formInputResponse;
        }
    }
}
