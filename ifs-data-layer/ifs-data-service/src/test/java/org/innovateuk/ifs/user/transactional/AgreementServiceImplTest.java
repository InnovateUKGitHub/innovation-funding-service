package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.Agreement;
import org.innovateuk.ifs.user.mapper.AgreementMapper;
import org.innovateuk.ifs.user.repository.AgreementRepository;
import org.innovateuk.ifs.user.resource.AgreementResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.user.builder.AgreementResourceBuilder.newAgreementResource;
import static org.innovateuk.ifs.user.builder.AgreementBuilder.newAgreement;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

public class AgreementServiceImplTest extends BaseServiceUnitTest<AgreementServiceImpl> {
    @InjectMocks
    private AgreementService agreementService = new AgreementServiceImpl();

    @Mock
    private AgreementRepository agreementRepositoryMock;

    @Mock
    private AgreementMapper agreementMapperMock;

    @Override
    protected AgreementServiceImpl supplyServiceUnderTest() {
        return new AgreementServiceImpl();
    }

    @Test
    public void getCurrent() throws Exception {
        Agreement agreement = newAgreement().build();
        AgreementResource agreementResource = newAgreementResource().build();

        when(agreementRepositoryMock.findByCurrentTrue()).thenReturn(agreement);
        when(agreementMapperMock.mapToResource(same(agreement))).thenReturn(agreementResource);

        ServiceResult<AgreementResource> result = agreementService.getCurrent();
        assertTrue(result.isSuccess());
        assertEquals(agreementResource, result.getSuccess());

        verify(agreementRepositoryMock, only()).findByCurrentTrue();
        verify(agreementMapperMock, only()).mapToResource(same(agreement));
    }

    @Test
    public void getCurrent_notFound() throws Exception {
        Agreement agreement = newAgreement().build();
        AgreementResource agreementResource = newAgreementResource().build();

        when(agreementRepositoryMock.findByCurrentTrue()).thenReturn(null);
        when(agreementMapperMock.mapToResource(same(agreement))).thenReturn(agreementResource);

        ServiceResult<AgreementResource> result = agreementService.getCurrent();
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Agreement.class)));

        verify(agreementRepositoryMock, only()).findByCurrentTrue();
        verifyZeroInteractions(agreementMapperMock);
    }

}
