package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.category.domain.ResearchCategory;
import org.innovateuk.ifs.category.repository.ResearchCategoryRepository;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.transactional.FinanceService;
import org.innovateuk.ifs.finance.transactional.GrantClaimMaximumService;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.user.transactional.UsersRolesService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;

import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.category.builder.ResearchCategoryBuilder.newResearchCategory;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.BUSINESS;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link  ApplicationResearchCategoryServiceImpl}
 */

public class ApplicationResearchCategoryServiceImplTest extends BaseServiceUnitTest<ApplicationResearchCategoryService> {

    @Mock
    private ResearchCategoryRepository researchCategoryRepositoryMock;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private QuestionService questionServiceMock;

    @Mock
    private UsersRolesService usersRolesServiceMock;

    @Mock
    private FinanceService financeServiceMock;

    @Mock
    private ApplicationMapper applicationMapperMock;

    @Mock
    private GrantClaimMaximumService grantClaimMaximumService;

    @Mock
    private OrganisationService organisationService;

    @Override
    protected ApplicationResearchCategoryService supplyServiceUnderTest() {
        return new ApplicationResearchCategoryServiceImpl();
    }

    @Test
    public void setApplicationResearchCategoryWithNoResearchCategoryChange() throws Exception {
        Long applicationId = 1L;
        Long researchCategoryId = 1L;

        ResearchCategory researchCategory = newResearchCategory().withId(researchCategoryId).build();

        Competition competition = newCompetition().build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withResearchCategory(researchCategory).build();

        Application expectedApplication = newApplication().withId(applicationId).withResearchCategory(researchCategory).build();
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(applicationRepositoryMock.save(expectedApplication)).thenReturn(expectedApplication);
        when(researchCategoryRepositoryMock.findOne(researchCategoryId)).thenReturn(researchCategory);

        ServiceResult<ApplicationResource> result = service.setResearchCategory(applicationId, researchCategoryId);

        assertTrue(result.isSuccess());

        verify(applicationRepositoryMock, times(1)).save(any(Application.class));
    }

    @Test
    public void setApplicationResearchCategoryWithResearchCategoryChange() throws Exception {

        Long applicationId = 1L;
        Long researchCategoryId = 1L;
        Long origResearchCategoryId = 2L;

        ResearchCategory researchCategory = newResearchCategory().withId(researchCategoryId).build();
        ResearchCategory origResearchCategory = newResearchCategory().withId(origResearchCategoryId).build();

        Competition competition = newCompetition().build();
        Question financeQuestion = newQuestion().build();
        OrganisationResource organisation = newOrganisationResource().withOrganisationType(BUSINESS.getId()).build();
        Application application = newApplication()
                .withId(applicationId)
                .withCompetition(competition)
                .withResearchCategory(origResearchCategory)
                .withProcessRoles(newProcessRole().withOrganisationId(organisation.getId()).withRole(LEADAPPLICANT).build())
                .build();


        Application expectedApplication = newApplication().withId(applicationId).withResearchCategory(researchCategory).build();
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(organisationService.findById(application.getLeadOrganisationId())).thenReturn(serviceSuccess(organisation));
        when(grantClaimMaximumService.getGrantClaimMaximumsForCompetitionType(anyLong())).thenReturn(serviceSuccess(asSet(researchCategory.getId())));
        when(grantClaimMaximumService.getGrantClaimMaximumsForCompetition(competition.getId())).thenReturn(serviceSuccess(asSet(origResearchCategory.getId())));

        when(applicationRepositoryMock.save(expectedApplication)).thenReturn(expectedApplication);
        when(researchCategoryRepositoryMock.findOne(researchCategoryId)).thenReturn(researchCategory);
        when(questionServiceMock.getQuestionByCompetitionIdAndFormInputType(competition.getId(), FormInputType.FINANCE)).thenReturn(serviceSuccess(financeQuestion));
        when(usersRolesServiceMock.getAssignableProcessRolesByApplicationId(applicationId)).thenReturn(serviceSuccess(Collections.EMPTY_LIST));

        ServiceResult<ApplicationResource> result = service.setResearchCategory(applicationId, researchCategoryId);

        assertTrue(result.isSuccess());

        verify(applicationRepositoryMock, times(1)).save(any(Application.class));
    }

    @Test
    public void setApplicationResearchCategory_nonExistingApplicationShouldResultInError() throws Exception {
        Long applicationId = 1L;
        Long researchCategoryId = 2L;

        ResearchCategory researchCategory = newResearchCategory().withId(researchCategoryId).build();

        Application expectedApplication = newApplication().withId(applicationId).withResearchCategory(researchCategory).build();
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(null);

        ServiceResult<ApplicationResource> result = service.setResearchCategory(applicationId, researchCategoryId);

        assertTrue(result.isFailure());

        verify(applicationRepositoryMock, times(0)).save(any(Application.class));
        assertEquals(CommonFailureKeys.GENERAL_NOT_FOUND.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());
    }

    @Test
    public void setApplicationResearchCategory_nonExistingResearchCategoryShouldResultInError() throws Exception {
        Long applicationId = 1L;
        Long researchCategoryId = 2L;

        ResearchCategory researchCategory = newResearchCategory().withId(researchCategoryId).build();
        Application application = newApplication().withId(applicationId).build();

        Application expectedApplication = newApplication().withId(applicationId).withResearchCategory(researchCategory).build();
        when(researchCategoryRepositoryMock.findOne(researchCategoryId)).thenReturn(null);
        when(applicationRepositoryMock.findOne(applicationId)).thenReturn(application);
        when(applicationRepositoryMock.save(expectedApplication)).thenReturn(expectedApplication);

        ServiceResult<ApplicationResource> result = service.setResearchCategory(applicationId, researchCategoryId);

        assertTrue(result.isFailure());
        verify(applicationRepositoryMock, times(0)).save(any(Application.class));
        assertEquals(CommonFailureKeys.GENERAL_NOT_FOUND.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());
    }
}
