package com.worth.ifs.application.model;

import com.worth.ifs.application.form.ApplicationForm;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.application.service.QuestionService;
import com.worth.ifs.application.service.SectionService;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.application.AbstractApplicationController.*;

/**
 * class with methods that are used on every model for sectionPages
 * these pages are rendered by the ApplicationFormController.applicationFormWithOpenSection method
 */
abstract class BaseSectionModelPopulator {
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
