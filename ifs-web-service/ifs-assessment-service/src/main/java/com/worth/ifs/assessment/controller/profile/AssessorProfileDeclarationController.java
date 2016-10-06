package com.worth.ifs.assessment.controller.profile;

import com.worth.ifs.assessment.form.profile.AssessorProfileDeclarationForm;
import com.worth.ifs.assessment.model.profile.AssessorProfileDeclarationModelPopulator;
import com.worth.ifs.user.resource.AffiliationResource;
import com.worth.ifs.user.resource.AffiliationType;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.worth.ifs.user.resource.AffiliationType.*;
import static java.lang.Boolean.FALSE;
import static java.util.Collections.EMPTY_MAP;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;

/**
 * Controller to manage the Assessor Profile Declaration of Interest page
 */
@Controller
@RequestMapping("/profile/declaration")
public class AssessorProfileDeclarationController {

    @Autowired
    private AssessorProfileDeclarationModelPopulator assessorProfileDeclarationModelPopulator;

    private static final String FORM_ATTR_NAME = "form";

    @RequestMapping(method = RequestMethod.GET)
    public String getDeclaration(Model model, @ModelAttribute("loggedInUser") UserResource loggedInUser, @ModelAttribute(FORM_ATTR_NAME) AssessorProfileDeclarationForm form) {
        populateFormWithExistingValues(form, loggedInUser);
        model.addAttribute("model", assessorProfileDeclarationModelPopulator.populateModel());
        return "profile/declaration-of-interest";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitDeclaration(Model model, @ModelAttribute(FORM_ATTR_NAME) AssessorProfileDeclarationForm form) {
        return "redirect:/registration/terms";
    }

    @RequestMapping(params = {"addFamilyMemberAffiliation"}, method = RequestMethod.GET)
    public String addFamilyMemberAffiliation(@ModelAttribute(FORM_ATTR_NAME) AssessorProfileDeclarationForm form) {
        return "profile/declaration-of-interest";
    }

    private void populateFormWithExistingValues(AssessorProfileDeclarationForm form, UserResource user) {
        Map<AffiliationType, List<AffiliationResource>> affiliations = getAffiliationsMap(user);

        form.setPrincipalEmployer(getPrincipalEmployer(affiliations).map(AffiliationResource::getOrganisation).orElse(null));
        form.setRole(getPrincipalEmployer(affiliations).map(AffiliationResource::getPosition).orElse(null));
        form.setProfessionalAffiliations(getProfessionalAffiliations(affiliations));

        form.setHasAppointments(hasAppointments(affiliations));
        //form.setAppointments(getAppointments(affiliations));

        form.setHasFinancialInterests(hasFinancialInterests(affiliations));
        form.setFinancialInterests(getFinancialInterests(affiliations));

        form.setHasFamilyAffiliations(hasFamilyAffiliations(affiliations));
        //form.setFamilyAffiliations(getFamilyAffiliations(affiliations));

        form.setHasFamilyFinancialInterests(hasFamilyFinancialInterests(affiliations));
        form.setFamilyFinancialInterests(getFamilyFinancialInterests(affiliations));
    }

    private Optional<AffiliationResource> getPrincipalEmployer(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return getAffiliationByType(EMPLOYER, affiliations);
    }

    private String getProfessionalAffiliations(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return getAffiliationByType(PROFESSIONAL, affiliations).map(AffiliationResource::getDescription).orElse(null);
    }

    private Boolean hasAppointments(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return hasAffiliationsByType(PERSONAL, affiliations);
    }

    private Optional<List<AffiliationResource>> getAppointments(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return getAffiliationsByType(PERSONAL, affiliations);
    }

    private Boolean hasFinancialInterests(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return hasAffiliationsByType(PERSONAL_FINANCIAL, affiliations);
    }

    private String getFinancialInterests(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return getAffiliationByType(PERSONAL_FINANCIAL, affiliations).map(AffiliationResource::getDescription).orElse(null);
    }

    private Boolean hasFamilyAffiliations(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return hasAffiliationsByType(FAMILY, affiliations);
    }

    private Optional<List<AffiliationResource>> getFamilyAffiliations(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return getAffiliationsByType(FAMILY, affiliations);
    }

    private Boolean hasFamilyFinancialInterests(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return hasAffiliationsByType(FAMILY_FINANCIAL, affiliations);
    }

    private String getFamilyFinancialInterests(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return getAffiliationByType(FAMILY_FINANCIAL, affiliations).map(AffiliationResource::getDescription).orElse(null);
    }

    private Map<AffiliationType, List<AffiliationResource>> getAffiliationsMap(UserResource user) {
        if (user.getAffiliations() == null) {
            return EMPTY_MAP;
        } else {
            return user.getAffiliations().stream().collect(groupingBy(AffiliationResource::getAffiliationType));
        }
    }

    private Optional<AffiliationResource> getAffiliationByType(AffiliationType affiliationType, Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return ofNullable(affiliations.get(affiliationType)).flatMap(affiliationsByType -> affiliationsByType.stream().findFirst());
    }

    private Optional<List<AffiliationResource>> getAffiliationsByType(AffiliationType affiliationType, Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return ofNullable(affiliations.get(affiliationType));
    }

    /**
     * Determine if the user specified an answer for having affiliations of the specified affiliationType. An affiliation question that has been answered as not existing is recorded as a single entry with the <code>exists</code> property as {@link Boolean#FALSE}.
     *
     * @param affiliationType
     * @param affiliations
     * @return
     */
    private Boolean hasAffiliationsByType(AffiliationType affiliationType, Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return ofNullable(affiliations.get(affiliationType)).map(affiliationsByType -> !(affiliationsByType.size() == 1 && FALSE == affiliationsByType.get(0).getExists())).orElse(null);
    }
}