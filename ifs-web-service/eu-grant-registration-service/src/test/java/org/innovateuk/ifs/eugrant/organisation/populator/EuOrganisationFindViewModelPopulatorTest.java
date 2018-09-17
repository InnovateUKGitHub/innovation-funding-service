package org.innovateuk.ifs.eugrant.organisation.populator;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.eugrant.EuOrganisationType;
import org.innovateuk.ifs.eugrant.organisation.form.EuOrganisationForm;
import org.innovateuk.ifs.eugrant.organisation.viewmodel.EuOrganisationFindViewModel;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.user.service.OrganisationSearchRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EuOrganisationFindViewModelPopulatorTest extends BaseServiceUnitTest<EuOrganisationFindModelPopulator> {

    @Mock
    private OrganisationSearchRestService searchRestService;

    @Mock
    private MessageSource messageSource;

    @Override
    protected EuOrganisationFindModelPopulator supplyServiceUnderTest() {
        return new EuOrganisationFindModelPopulator();
    }

    @Test
    public void populate() {
        EuOrganisationType type = EuOrganisationType.BUSINESS;
        EuOrganisationForm form = new EuOrganisationForm();
        form.setOrganisationSearching(true);
        form.setOrganisationSearchName("SearchString");
        HttpServletRequest request = mock(HttpServletRequest.class);
        List<OrganisationSearchResult> results = asList(
                new OrganisationSearchResult("123", "name1"),
                new OrganisationSearchResult("321", "name2")
        );
        Locale locale = Locale.CANADA;
        when(request.getLocale()).thenReturn(locale);
        when(searchRestService.searchOrganisation(type, "SearchString")).thenReturn(restSuccess(results));
        when(messageSource.getMessage("registration.BUSINESS.SearchLabel", null, locale)).thenReturn("SearchLabel");
        when(messageSource.getMessage("registration.BUSINESS.SearchHint", null, locale)).thenReturn("SearchHint");

        EuOrganisationFindViewModel viewModel = service.populate(type, form, request);

        assertEquals(viewModel.getResults(), results);
        assertEquals(viewModel.getType(), type);
        assertEquals(viewModel.getSearchHint(), "SearchHint");
        assertEquals(viewModel.getSearchLabel(), "SearchLabel");
    }
}
