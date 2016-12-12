package org.innovateuk.ifs.application.model;

import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

/**
 * class with methods that are used on every model for sectionPages
 * these pages are rendered by the ApplicationFormController.applicationFormWithOpenSection method
 */
abstract class BaseSectionModelPopulator {
    protected static final String MODEL_ATTRIBUTE_FORM = "form";

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private ApplicationNavigationPopulator applicationNavigationPopulator;

    public abstract void populateModel(ApplicationForm form, Model model, ApplicationResource application, SectionResource section, UserResource user, BindingResult bindingResult, List<SectionResource> allSections);

    protected void addNavigation(SectionResource section, Long applicationId, Model model) {
        applicationNavigationPopulator.addNavigation(section, applicationId, model);
    }
}
