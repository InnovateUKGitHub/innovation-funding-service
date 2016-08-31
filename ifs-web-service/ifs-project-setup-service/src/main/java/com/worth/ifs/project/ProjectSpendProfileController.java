package com.worth.ifs.project;

import com.worth.ifs.commons.rest.LocalDateResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.project.finance.ProjectFinanceService;
import com.worth.ifs.project.form.SpendProfileForm;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;
import com.worth.ifs.project.util.DateUtil;
import com.worth.ifs.project.util.FinancialYearDate;
import com.worth.ifs.project.validation.SpendProfileCostValidator;
import com.worth.ifs.project.viewmodel.ProjectSpendProfileViewModel;
import com.worth.ifs.project.viewmodel.SpendProfileSummaryModel;
import com.worth.ifs.project.viewmodel.SpendProfileSummaryYearModel;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This controller will handle all requests that are related to spend profile.
 */
@Controller
@RequestMapping("/project/{projectId}/partner-organisation/{organisationId}/spend-profile")
public class ProjectSpendProfileController {

    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @RequestMapping(method = GET)
    public String viewSpendProfile(Model model,
                                   @PathVariable("projectId") final Long projectId,
                                   @PathVariable("organisationId") final Long organisationId,
                                   @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        buildSpendProfileViewModel(model, projectId, organisationId);

        return "project/spend-profile";
    }

    @RequestMapping(value = "/edit", method = GET)
    public String editSpendProfile(Model model,
                                   @PathVariable("projectId") final Long projectId,
                                   @PathVariable("organisationId") final Long organisationId,
                                   @ModelAttribute("loggedInUser") UserResource loggedInUser) {


        ProjectSpendProfileViewModel viewModel = buildSpendProfileViewModel(model, projectId, organisationId);

        SpendProfileForm form = new SpendProfileForm();
        form.setTable(viewModel.getTable());
        model.addAttribute(FORM_ATTR_NAME, form);

        return "project/spend-profile/edit";
    }

    @RequestMapping(value = "/edit", method = POST)
    public String saveSpendProfile(Model model,
                                   @PathVariable("projectId") final Long projectId,
                                   @PathVariable("organisationId") final Long organisationId,
                                   @ModelAttribute(FORM_ATTR_NAME) SpendProfileForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        return editOrMarkAsCompleteSpendProfile(model, bindingResult, form, projectId, organisationId, false, "project/spend-profile/edit");
    }

    @RequestMapping(value = "/confirm", method = POST)
    public String markAsCompleteSpendProfile(Model model,
                                   @PathVariable("projectId") final Long projectId,
                                   @PathVariable("organisationId") final Long organisationId,
                                   @ModelAttribute(FORM_ATTR_NAME) SpendProfileForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        return editOrMarkAsCompleteSpendProfile(model, bindingResult, form, projectId, organisationId, true, "project/spend-profile/confirm");
    }

    private String editOrMarkAsCompleteSpendProfile(Model model,
                                                    BindingResult bindingResult,
                                                    SpendProfileForm form,
                                                    Long projectId,
                                                    Long organisationId,
                                                    boolean isMarkAsComplete,
                                                    String successView) {

        //BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(form.getTable(), "");
        ValidationHandler validationHandler = ValidationHandler.newBindingResultHandler(bindingResult);
        new SpendProfileCostValidator().validate(form.getTable(), bindingResult);

        if (validationHandler.hasErrors()) {
            return "project/spend-profile/edit";
        }

        ServiceResult<Void> result = isMarkAsComplete ? projectFinanceService.markSpendProfileComplete(projectId, organisationId, form.getTable()) : projectFinanceService.saveSpendProfile(projectId, organisationId, form.getTable());
        if (result.isFailure()) {

            // If this model attribute is set, it means there are some categories where the totals don't match
            model.addAttribute("errorCategories", result.getFailure().getErrors());

            if (isMarkAsComplete) {
                return "project/spend-profile/edit";
            }
        }

        return successView;
    }

    private ProjectSpendProfileViewModel buildSpendProfileViewModel(Model model, Long projectId, Long organisationId) {

        ProjectSpendProfileViewModel viewModel = populateSpendProfileViewModel(projectId, organisationId);
        model.addAttribute("model", viewModel);

        return viewModel;
    }

    private ProjectSpendProfileViewModel populateSpendProfileViewModel(final Long projectId, final Long organisationId) {
        ProjectResource projectResource = projectService.getById(projectId);
        SpendProfileTableResource table = projectFinanceService.getSpendProfileTable(projectId, organisationId);
        List<SpendProfileSummaryYearModel> years = createSpendProfileSummaryYears(projectResource, table);
        SpendProfileSummaryModel summary = new SpendProfileSummaryModel(years);
        return new ProjectSpendProfileViewModel(projectResource, table, summary, false);
    }

    private List<SpendProfileSummaryYearModel> createSpendProfileSummaryYears(ProjectResource project, SpendProfileTableResource table){
        Integer startYear = new FinancialYearDate(DateUtil.asDate(project.getTargetStartDate())).getFiscalYear();
        Integer endYear = new FinancialYearDate(DateUtil.asDate(project.getTargetStartDate().plusMonths(project.getDurationInMonths()))).getFiscalYear();
        return IntStream.range(startYear, endYear + 1).
                mapToObj(
                        year -> {
                            Set<String> keys = table.getMonthlyCostsPerCategoryMap().keySet();
                            BigDecimal totalForYear = BigDecimal.ZERO;

                            for(String key : keys){
                                List<BigDecimal> values = table.getMonthlyCostsPerCategoryMap().get(key);
                                for(int i = 0; i < values.size(); i++){
                                    LocalDateResource month = table.getMonths().get(i);
                                    FinancialYearDate financialYearDate = new FinancialYearDate(DateUtil.asDate(month.getLocalDate()));
                                    if(year == financialYearDate.getFiscalYear()){
                                        totalForYear = totalForYear.add(values.get(i));
                                    }
                                }
                            }
                            return new SpendProfileSummaryYearModel(year, totalForYear.toPlainString());
                        }

                ).collect(toList());
    }
}
