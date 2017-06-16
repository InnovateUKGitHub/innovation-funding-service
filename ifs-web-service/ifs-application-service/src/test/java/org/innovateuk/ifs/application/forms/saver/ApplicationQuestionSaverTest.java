package org.innovateuk.ifs.application.forms.saver;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Tests {@link ApplicationQuestionSaver}
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplicationQuestionSaverTest {

    @InjectMocks
    private ApplicationQuestionSaver questionSaver;

    @Mock
    private ProcessRoleService processRoleService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private UserService userService;

    @Mock
    private QuestionService questionService;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Mock
    private ApplicationQuestionApplicationDetailsSaverTest detailsSaver;

    @Test
    public void saveApplicationForm() {
        final Long applicationId;
        final Long competitionId;
        final ApplicationForm form;
        final Long sectionId;
        final Long userId;
        final HttpServletRequest request;
        final HttpServletResponse response;
        final BindingResult bindingResult;

    }

}
