package org.innovateuk.ifs.application.forms.saver;

import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.overheads.OverheadFileSaver;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Tests {@link ApplicationSectionSaver}
 */
@Service
public class ApplicationSectionSaverTest {

    @InjectMocks
    private ApplicationSectionSaver sectionSaver;

    @Mock
    private OrganisationService organisationService;

    @Mock
    private FinanceHandler financeHandler;

    @Mock
    private ProcessRoleService processRoleService;

    @Mock
    private SectionService sectionService;

    @Mock
    private QuestionService questionService;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Mock
    private OverheadFileSaver overheadFileSaver;

    @Mock
    private ApplicationSectionFinanceSaver financeSaver;

    @Test
    public void saveApplicationForm() {
        final ApplicationResource application;
        final Long competitionId;
        final ApplicationForm form;
        final Long sectionId;
        final Long userId;
        final HttpServletRequest request;
        final HttpServletResponse response;
        final Boolean validFinanceTerms;


        //TODO WIP
    }
}
