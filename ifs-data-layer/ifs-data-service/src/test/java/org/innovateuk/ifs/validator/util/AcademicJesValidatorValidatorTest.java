package org.innovateuk.ifs.validator.util;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.validator.AcademicJesValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.validator.ValidatorTestUtil.getBindingResult;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AcademicJesValidatorValidatorTest {
    @InjectMocks
    private AcademicJesValidator validator;

    @Mock
    private LoggedInUserSupplier loggedInUserSupplier;

    @Mock
    private OrganisationService organisationService;

    private Application application;

    private BindingResult bindingResult;

    private static final Long USER_ID = 123912L;

    private static final Long ORGANISATION_ID = 123L;


    @Before
    public void setUp() {
        application = newApplication()
                .with(application -> application.setApplicationFinances(
                        newApplicationFinance()
                                .withOrganisation(newOrganisation()
                                        .withId(ORGANISATION_ID)
                                        .build())
                                .with(applicationFinance -> applicationFinance.setFinanceFileEntry(null))
                                .build(1)
                )).build();
        when(loggedInUserSupplier.get()).thenReturn(newUser().withId(USER_ID).build());
        when(organisationService.getPrimaryForUser(USER_ID))
                .thenReturn(ServiceResult.serviceSuccess(newOrganisationResource().withId(ORGANISATION_ID).build()));
        bindingResult = getBindingResult(application);
    }

    @Test
    public void testValidate_applicationFinanceNoEntry() throws Exception {

        validator.validate(application, bindingResult);

        assertTrue(bindingResult.hasErrors());
    }

    @Test
    public void testValidate_NoApplicationFinance() throws Exception {
        application = newApplication()
                .with(application -> application.setApplicationFinances(null))
                .build();

        validator.validate(application, bindingResult);

        assertTrue(bindingResult.hasErrors());
    }

    @Test
    public void testValidate_NoApplicationFinanceWithSameOrg() throws Exception {
        when(organisationService.getPrimaryForUser(USER_ID)).thenReturn(ServiceResult.serviceSuccess(newOrganisationResource().withId(1202020L).build()));

        validator.validate(application, bindingResult);

        assertTrue(bindingResult.hasErrors());
    }
}