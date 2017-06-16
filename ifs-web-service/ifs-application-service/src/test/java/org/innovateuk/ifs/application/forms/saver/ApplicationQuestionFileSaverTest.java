package org.innovateuk.ifs.application.forms.saver;

import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests {@link ApplicationQuestionFileSaver}
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplicationQuestionFileSaverTest {

    @InjectMocks
    private ApplicationQuestionFileSaver fileSaver;

    @Mock
    private FormInputResponseRestService formInputResponseRestService;

    @Mock
    private FormInputRestService formInputRestService;

    @Test
    public void saveFileUploadQuestionsIfAny() {
//        List<QuestionResource> questions,
//        final Map<String, String[]> params,
//        HttpServletRequest request,
//        Long applicationId,
//        Long processRoleId
    }
}
