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
import org.innovateuk.ifs.competition.domain.CompetitionType;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceService;
import org.innovateuk.ifs.finance.transactional.GrantClaimMaximumService;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.user.transactional.UsersRolesService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static java.util.Collections.EMPTY_LIST;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.category.builder.ResearchCategoryBuilder.newResearchCategory;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
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
    private ApplicationFinanceService financeServiceMock;

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
    public void setApplicationResearchCategoryWithNoResearchCategoryChange() {
        Long applicationId = 1L;
        Long researchCategoryId = 1L;

        ResearchCategory researchCategory = newResearchCategory().withId(researchCategoryId).build();

        Competition competition = newCompetition().withFundingType(FundingType.GRANT).build();
        Application application = newApplication().withId(applicationId).withCompetition(competition).withResearchCategory(researchCategory).build();

        Application expectedApplication = newApplication().withId(applicationId).withResearchCategory(researchCategory).build();
        when(applicationRepositoryMock.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationRepositoryMock.save(expectedApplication)).thenReturn(expectedApplication);
        when(researchCategoryRepositoryMock.findById(researchCategoryId)).thenReturn(Optional.of(researchCategory));

        ServiceResult<ApplicationResource> result = service.setResearchCategory(applicationId, researchCategoryId);

        assertTrue(result.isSuccess());

        verify(applicationRepositoryMock, times(1)).save(any(Application.class));
    }

    @Test
    public void setApplicationResearchCategoryWithResearchCategoryChange() {

        Long researchCategoryId = 1L;
        Long origResearchCategoryId = 2L;

        ResearchCategory researchCategory = newResearchCategory().withId(researchCategoryId).build();
        ResearchCategory origResearchCategory = newResearchCategory().withId(origResearchCategoryId).build();
        CompetitionType compType = newCompetitionType().withName("Programme").build();

        Competition competition = newCompetition().withFundingType(FundingType.GRANT).withCompetitionType(compType).build();
        Question financeQuestion = newQuestion().build();
        OrganisationResource organisation = newOrganisationResource().withOrganisationType(BUSINESS.getId()).build();
        Application application = newApplication()
                .withCompetition(competition)
                .withResearchCategory(origResearchCategory)
                .withProcessRoles(newProcessRole().withOrganisationId(organisation.getId()).withRole(LEADAPPLICANT).build())
                .build();


        Application expectedApplication = newApplication().withId(application.getId()).withResearchCategory
                (researchCategory).build();
        when(applicationRepositoryMock.findById(application.getId())).thenReturn(Optional.of(application));
        when(organisationService.findById(application.getLeadOrganisationId())).thenReturn(serviceSuccess(organisation));
        when(grantClaimMaximumService.isMaximumFundingLevelOverridden(competition.getId())).thenReturn(serviceSuccess(true));

        when(applicationRepositoryMock.save(expectedApplication)).thenReturn(expectedApplication);
        when(researchCategoryRepositoryMock.findById(researchCategoryId)).thenReturn(Optional.of(researchCategory));
        when(usersRolesServiceMock.getAssignableProcessRolesByApplicationId(application.getId())).thenReturn(serviceSuccess(EMPTY_LIST));

        ServiceResult<ApplicationResource> result = service.setResearchCategory(application.getId(), researchCategoryId);

        assertTrue(result.isSuccess());

        verify(applicationRepositoryMock, times(1)).save(any(Application.class));
    }

    @Test
    public void setApplicationResearchCategory_nonExistingApplicationShouldResultInError() {
        Long applicationId = 1L;
        Long researchCategoryId = 2L;

        when(applicationRepositoryMock.findById(applicationId)).thenReturn(Optional.empty());

        ServiceResult<ApplicationResource> result = service.setResearchCategory(applicationId, researchCategoryId);

        assertTrue(result.isFailure());

        verify(applicationRepositoryMock, times(0)).save(any(Application.class));
        assertEquals(CommonFailureKeys.GENERAL_NOT_FOUND.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());
    }

    @Test
    public void setApplicationResearchCategory_nonExistingResearchCategoryShouldResultInError()  {
        Long applicationId = 1L;
        Long researchCategoryId = 2L;

        ResearchCategory researchCategory = newResearchCategory().withId(researchCategoryId).build();
        Application application = newApplication().withId(applicationId).build();

        Application expectedApplication = newApplication().withId(applicationId).withResearchCategory(researchCategory).build();
        when(researchCategoryRepositoryMock.findById(researchCategoryId)).thenReturn(Optional.empty());
        when(applicationRepositoryMock.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationRepositoryMock.save(expectedApplication)).thenReturn(expectedApplication);

        ServiceResult<ApplicationResource> result = service.setResearchCategory(applicationId, researchCategoryId);

        assertTrue(result.isFailure());
        verify(applicationRepositoryMock, times(0)).save(any(Application.class));
        assertEquals(CommonFailureKeys.GENERAL_NOT_FOUND.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());
    }
}
