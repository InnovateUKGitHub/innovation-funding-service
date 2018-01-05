package org.innovateuk.ifs.async.controller.endtoend;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.service.DefaultRestTemplateAdaptor;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.processRoleResourceListType;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * An end-to-end test scenario for using the async mechanism provided in the {@link org.innovateuk.ifs.async} package.
 */
public class EndToEndAsyncControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private EndToEndAsyncControllerTestController controller;

    @Autowired
    private GenericApplicationContext applicationContext;

    private RestTemplate originalRestTemplate;

    private RestTemplate restTemplateMock;

    @Before
    public void swapOutRestTemplateForMock() {

        DefaultRestTemplateAdaptor restTemplateAdaptor = applicationContext.getBean(DefaultRestTemplateAdaptor.class);
        originalRestTemplate = (RestTemplate) ReflectionTestUtils.getField(restTemplateAdaptor, "restTemplate");

        restTemplateMock = mock(RestTemplate.class);
        ReflectionTestUtils.setField(restTemplateAdaptor, "restTemplate", restTemplateMock);
    }

    @After
    public void restoreOriginalRestTemplate() {
        DefaultRestTemplateAdaptor restTemplateAdaptor = applicationContext.getBean(DefaultRestTemplateAdaptor.class);
        ReflectionTestUtils.setField(restTemplateAdaptor, "restTemplate", originalRestTemplate);
    }

    @Before
    public void setupHttpFilterThreadLocals() {
        setLoggedInUser(newUserResource().build());
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @Test
    public void testControllerAsyncBehaviour() {

        BindingAwareModelMap model = new BindingAwareModelMap();

        CompetitionResource competition = newCompetitionResource().
                withId(456L).
                withActivityCode("The Activity Code").
                build();

        ApplicationResource application = newApplicationResource().
                withId(123L).
                withCompetition(competition.getId()).
                withName("My Application").
                withInnovationArea(newInnovationAreaResource().withSectorName("The Sector").build()).
                build();

        when(restTemplateMock.exchange(eq("/application/123"), eq(HttpMethod.GET), isA(HttpEntity.class),
                eq(ApplicationResource.class))).thenAnswer(invocation -> delayedResponse(entity(application)));

        when(restTemplateMock.exchange(eq("/competition/456"), eq(HttpMethod.GET), isA(HttpEntity.class),
                eq(CompetitionResource.class))).thenReturn(delayedResponse(entity(competition)));

        // set up expectations for the retrieval of the Lead Organisation
        List<Long> leadOrganisationUserIds = setupLeadOrganisationRetrievalExpectations();

        String result = controller.getMethod(application.getId(), model);
        assertThat(result, equalTo("/application/My Application"));

        //
        // assert that the values added directly to the model in the Controller itself are present
        //
        assertThat(model.get("applicationSectorAndCompetitionCode"), equalTo("The Sector-The Activity Code"));
        assertThat(model.get("leadOrganisationUsers"), equalTo(leadOrganisationUserIds));

        // and assert that the values added as a Future directly to the model in the Controller have resolved correctly
        assertThat((List<String>) model.get("explicitlyAsyncResultsAddedAsAFutureToTheModel"), contains(
                "doExplicitAsyncActivities2ThenAmended",
                "doExplicitAsyncActivities4ThenAmended",
                "doExplicitAsyncActivities6ThenAmended"));

        //
        // assert that the values added by the hidden async activities in
        // {@link EndToEndAsyncControllerTestService#doSomeHiddenAsyncActivities} have resolved
        //
        assertThat(model.get("doSomeHiddenAsyncActivitiesUser2"), equalTo(2L));
        assertThat(model.get("doSomeHiddenAsyncActivitiesUser4"), equalTo(4L));
        assertThat(model.get("doSomeHiddenAsyncActivitiesUser6"), equalTo(6L));

        //
        // assert that the values added by the hidden async activities in
        // {@link EndToEndAsyncControllerTestService#doSomeHiddenButSafeBlockingAsyncActivities} have resolved
        //
        assertThat(model.get("doSomeHiddenButSafeBlockingAsyncActivitiesUser2"), equalTo(2L));
        assertThat(model.get("doSomeHiddenButSafeBlockingAsyncActivitiesUser4"), equalTo(4L));
        assertThat(model.get("doSomeHiddenButSafeBlockingAsyncActivitiesUser6"), equalTo(6L));
    }

    private List<Long> setupLeadOrganisationRetrievalExpectations() {
        RoleResource leadApplicantRole = newRoleResource().withType(UserRoleType.LEADAPPLICANT).build();
        RoleResource collaboratorRole = newRoleResource().withType(UserRoleType.COLLABORATOR).build();

        List<ProcessRoleResource> applicationProcessRoles = newProcessRoleResource().
                withId(1L, 3L, 5L).
                withRole(collaboratorRole, leadApplicantRole, collaboratorRole).
                withOrganisation(333L, 444L, 555L).
                build(3);

        when(restTemplateMock.exchange(eq("/processrole/findByApplicationId/123"), eq(HttpMethod.GET), isA(HttpEntity.class),
                eq(processRoleResourceListType()))).thenReturn(delayedResponse(entity(applicationProcessRoles)));

        List<Long> leadOrganisationUserIds = asList(2L, 4L, 6L);
        OrganisationResource leadOrganisation = newOrganisationResource().withUsers(leadOrganisationUserIds).build();

        when(restTemplateMock.exchange(eq("/organisation/findById/444"), eq(HttpMethod.GET), isA(HttpEntity.class),
                eq(OrganisationResource.class))).thenReturn(delayedResponse(entity(leadOrganisation)));
        return leadOrganisationUserIds;
    }

    private <T> ResponseEntity<T> entity(T result) {
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private <T> T delayedResponse(T response) {
        try {
            Thread.sleep((int) (Math.random() * 50));
            return response;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
