package org.innovateuk.ifs.application.finance.view;

import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.form.service.FormInputRestService;

public abstract class AbstractFinanceModelPopulator {

    private SectionService sectionService;
    private FormInputRestService formInputRestService;
    private QuestionRestService questionRestService;

    public AbstractFinanceModelPopulator(SectionService sectionService,
                                         FormInputRestService formInputRestService,
                                         QuestionRestService questionRestService) {
        this.sectionService = sectionService;
        this.formInputRestService = formInputRestService;
        this.questionRestService = questionRestService;
    }
}
