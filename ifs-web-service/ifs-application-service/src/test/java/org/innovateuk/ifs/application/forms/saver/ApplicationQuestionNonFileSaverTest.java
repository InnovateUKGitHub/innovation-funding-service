package org.innovateuk.ifs.application.forms.saver;

import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests {@link ApplicationQuestionNonFileSaver}
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplicationQuestionNonFileSaverTest {

    @InjectMocks
    private ApplicationQuestionNonFileSaver nonFileSaver;

    @Mock
    private FormInputResponseRestService formInputResponseRestService;

    @Mock
    private FormInputRestService formInputRestService;


    @Test
    public void saveNonFileUploadQuestions() {
//        List<QuestionResource> questions,
//        HttpServletRequest request,
//        Long userId,
//        Long applicationId,
//        boolean ignoreEmpty

    }
}
