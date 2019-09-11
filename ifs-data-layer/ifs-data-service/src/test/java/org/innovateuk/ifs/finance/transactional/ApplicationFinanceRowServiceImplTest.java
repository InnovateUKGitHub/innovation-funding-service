package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaField;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaValue;
import org.innovateuk.ifs.finance.handler.IndustrialCostFinanceHandler;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceDelegate;
import org.innovateuk.ifs.finance.mapper.ApplicationFinanceMapper;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.finance.repository.FinanceRowMetaFieldRepository;
import org.innovateuk.ifs.finance.repository.FinanceRowMetaValueRepository;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.SubContractingCost;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.domain.OrganisationType;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceRowBuilder.newApplicationFinanceRow;
import static org.innovateuk.ifs.finance.builder.FinanceRowMetaFieldBuilder.newFinanceRowMetaField;
import static org.innovateuk.ifs.finance.builder.FinanceRowMetaValueBuilder.newFinanceRowMetaValue;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.inOrder;

public class ApplicationFinanceRowServiceImplTest extends BaseServiceUnitTest<ApplicationFinanceRowServiceImpl> {

    @Mock
    private IndustrialCostFinanceHandler organisationFinanceDefaultHandlerMock;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private OrganisationFinanceDelegate organisationFinanceDelegateMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private ApplicationFinanceRepository applicationFinanceRepositoryMock;

    @Mock
    private ApplicationFinanceRowRepository applicationFinanceRowRepositoryMock;

    @Mock
    private ApplicationFinanceMapper applicationFinanceMapperMock;

    @Mock
    private FinanceRowMetaValueRepository financeRowMetaValueRepositoryMock;

    @Mock
    private FinanceRowMetaFieldRepository financeRowMetaFieldRepositoryMock;

    @Override
    protected ApplicationFinanceRowServiceImpl supplyServiceUnderTest() {
        return new ApplicationFinanceRowServiceImpl();
    }

    private FinanceRowItem newFinanceRowItem;
    private ApplicationFinance applicationFinance;
    private static final long costId = 1L;
    private FinanceRowMetaField financeRowMetaField;

    @Before
    public void setUp() {
        String metaFieldTitle = "country";
        String metaFieldType = "String";

        Application application = newApplication()
                .withCompetition(newCompetition()
                        .withCompetitionStatus(CompetitionStatus.OPEN)
                        .build()
                ).build();

        OrganisationType organisationType = newOrganisationType()
                .withOrganisationType(OrganisationTypeEnum.RESEARCH)
                .build();

        applicationFinance = newApplicationFinance()
                .withApplication(application)
                .withOrganisation(newOrganisation().withOrganisationType(organisationType).build())
                .build();

        financeRowMetaField = newFinanceRowMetaField()
                .withTitle(metaFieldTitle)
                .withType(metaFieldType).build();

        newFinanceRowItem = new SubContractingCost(
                costId, new BigDecimal(10), "Scotland", "nibbles", "purring", applicationFinance.getId());

        when(applicationRepositoryMock.findById(application.getId())).thenReturn(Optional.of(application));
        when(organisationFinanceDelegateMock.getOrganisationFinanceHandler(application.getCompetition().getId(), organisationType.getId())).thenReturn(organisationFinanceDefaultHandlerMock);
    }

    @Test
    public void getCostItemNotFound() {
        long doesNotExistId = -1L;
        ServiceResult<FinanceRowItem> result = service.get(doesNotExistId);
        assertTrue(result.isFailure());
        assertEquals("GENERAL_NOT_FOUND", result.getErrors().get(0).getErrorKey());
    }

    @Test
    public void getCostItem() {
        long doesExistId = 1L;

        FinanceRowItem financeRowItem = new SubContractingCost(doesExistId, new BigDecimal(10), "Country", "name", "role", applicationFinance.getId());
        Competition competition = newCompetition().build();
        Application application = newApplication().withCompetition(competition).build();
        OrganisationType organisationType = newOrganisationType().withOrganisationType(OrganisationTypeEnum.RESEARCH).build();
        Organisation organisation = newOrganisation().withOrganisationType(organisationType).build();
        ApplicationFinance applicationFinance = newApplicationFinance().withApplication(application).withOrganisation(organisation).build();
        ApplicationFinanceRow applicationFinanceRow = newApplicationFinanceRow().withId(doesExistId).withTarget(applicationFinance).build();

        when(applicationFinanceRowRepositoryMock.findById(doesExistId)).thenReturn(Optional.of(applicationFinanceRow ));
        when(organisationFinanceDelegateMock.getOrganisationFinanceHandler(competition.getId(), organisationType.getId())).thenReturn(organisationFinanceDefaultHandlerMock);
        when(organisationFinanceDefaultHandlerMock.toResource(applicationFinanceRow)).thenReturn(financeRowItem);

        ServiceResult<FinanceRowItem> result = service.get(doesExistId);

        assertTrue(result.isSuccess());
        assertEquals(financeRowItem, result.getSuccess());

        InOrder inOrder = inOrder(applicationFinanceRowRepositoryMock, organisationFinanceDelegateMock, organisationFinanceDefaultHandlerMock);
        inOrder.verify(applicationFinanceRowRepositoryMock).findById(doesExistId);
        inOrder.verify(organisationFinanceDelegateMock).getOrganisationFinanceHandler(competition.getId(), organisationType.getId());
        inOrder.verify(organisationFinanceDefaultHandlerMock).toResource(applicationFinanceRow);
    }

    @Test
    public void alreadyExistingMetaValueShouldBeUpdated() {
        List<FinanceRowMetaValue> currentFinanceRowMetaValue = singletonList(newFinanceRowMetaValue()
                .withFinanceRowMetaField(financeRowMetaField)
                .withValue("England")
                .build());

        List<FinanceRowMetaValue> newFinanceRowMetaValue = singletonList(newFinanceRowMetaValue()
                .withFinanceRowMetaField(financeRowMetaField)
                .withValue("purring")
                .build());

        ApplicationFinanceRow convertedApplicationFinanceRow = newApplicationFinanceRow()
                .withFinanceRowMetadata(newFinanceRowMetaValue)
                .withTarget(applicationFinance).build();

        ApplicationFinanceRow currentApplicationFinanceRow = newApplicationFinanceRow()
                .withFinanceRowMetadata(currentFinanceRowMetaValue)
                .withTarget(applicationFinance).build();

        when(applicationFinanceRowRepositoryMock.findById(costId)).thenReturn(Optional.of(currentApplicationFinanceRow));
        when(organisationFinanceDefaultHandlerMock.toApplicationDomain(any())).thenReturn(convertedApplicationFinanceRow);
        when(organisationFinanceDefaultHandlerMock.updateCost(any())).thenReturn(convertedApplicationFinanceRow);
        when(financeRowMetaValueRepositoryMock.financeRowIdAndFinanceRowMetaFieldId(any(), any())).thenReturn(currentFinanceRowMetaValue.get(0));

        ServiceResult<FinanceRowItem> result = service.update(costId, newFinanceRowItem);

        assertTrue(result.isSuccess());

        FinanceRowMetaValue combinedFinanceRowMetaValue = currentFinanceRowMetaValue.get(0);
        combinedFinanceRowMetaValue.setValue(newFinanceRowMetaValue.get(0).getValue());

        verify(financeRowMetaValueRepositoryMock, times(1)).save(combinedFinanceRowMetaValue);
    }

    @Test
    public void nonExistingMetaValueShouldBeCreated() {
        List<FinanceRowMetaValue> financeRowMetaValue = singletonList(
                newFinanceRowMetaValue()
                        .withFinanceRowMetaField(financeRowMetaField)
                        .withValue("England")
                        .build()
        );

        ApplicationFinanceRow convertedApplicationFinanceRow = newApplicationFinanceRow()
                .withFinanceRowMetadata(financeRowMetaValue)
                .withTarget(applicationFinance).build();

        ApplicationFinanceRow currentApplicationFinanceRow = newApplicationFinanceRow()
                .withTarget(applicationFinance).build();

        when(applicationFinanceRowRepositoryMock.findById(costId)).thenReturn(Optional.of(currentApplicationFinanceRow));
        when(organisationFinanceDefaultHandlerMock.toApplicationDomain(any())).thenReturn(convertedApplicationFinanceRow);
        when(organisationFinanceDefaultHandlerMock.updateCost(any())).thenReturn(currentApplicationFinanceRow);
        when(financeRowMetaValueRepositoryMock.financeRowIdAndFinanceRowMetaFieldId(any(), any())).thenReturn(null);
        when(financeRowMetaFieldRepositoryMock.findById(financeRowMetaField.getId())).thenReturn(Optional.of(financeRowMetaField));

        ServiceResult<FinanceRowItem> result = service.update(costId, newFinanceRowItem);

        assertTrue(result.isSuccess());
        verify(financeRowMetaValueRepositoryMock, times(1)).save(financeRowMetaValue.get(0));
    }

    @Test
    public void noAttachedMetaValueDoesNotCreateOrUpdateMetaValue() {
        ApplicationFinanceRow convertedApplicationFinanceRow = newApplicationFinanceRow()
                .withTarget(applicationFinance).build();

        ApplicationFinanceRow currentApplicationFinanceRow = newApplicationFinanceRow()
                .withTarget(applicationFinance).build();

        when(applicationFinanceRowRepositoryMock.findById(costId)).thenReturn(Optional.of(currentApplicationFinanceRow));
        when(organisationFinanceDefaultHandlerMock.toApplicationDomain(any())).thenReturn(convertedApplicationFinanceRow);
        when(organisationFinanceDefaultHandlerMock.updateCost(any())).thenReturn(currentApplicationFinanceRow);
        when(financeRowMetaValueRepositoryMock.financeRowIdAndFinanceRowMetaFieldId(any(), any())).thenReturn(null);
        when(financeRowMetaFieldRepositoryMock.findById(financeRowMetaField.getId())).thenReturn(Optional.of(financeRowMetaField));

        ServiceResult<FinanceRowItem> result = service.update(costId, newFinanceRowItem);

        assertTrue(result.isSuccess());
        verify(financeRowMetaValueRepositoryMock, times(0)).save(any(FinanceRowMetaValue.class));
    }

}
