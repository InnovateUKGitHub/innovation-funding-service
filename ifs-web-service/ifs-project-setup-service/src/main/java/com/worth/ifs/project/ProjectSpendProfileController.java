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
import com.worth.ifs.util.CollectionFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.worth.ifs.commons.error.CommonFailureKeys.SPEND_PROFILE_CANNOT_MARK_AS_COMPLETE_BECAUSE_SPEND_HIGHER_THAN_ELIGIBLE;
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

    @Autowired
    @Qualifier("spendProfileCostValidator")
    private SpendProfileCostValidator spendProfileCostValidator;

    @RequestMapping(method = GET)
    public String viewSpendProfile(Model model,
                                   @PathVariable("projectId") final Long projectId,
                                   @PathVariable("organisationId") final Long organisationId,
                                   @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        model.addAttribute("model", buildSpendProfileViewModel(projectId, organisationId));
        return "project/spend-profile";
    }

    @RequestMapping(value = "/edit", method = GET)
    public String editSpendProfile(Model model,
                                   HttpServletRequest request,
                                   @ModelAttribute(FORM_ATTR_NAME) SpendProfileForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   ValidationHandler validationHandler,
                                   @PathVariable("projectId") final Long projectId,
                                   @PathVariable("organisationId") final Long organisationId,
                                   @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        ProjectResource projectResource = projectService.getById(projectId);
        SpendProfileTableResource spendProfileTableResource = projectFinanceService.getSpendProfileTable(projectId, organisationId);
        form.setTable(spendProfileTableResource);

        if(spendProfileTableResource.getMarkedAsComplete()) {
            markSpendProfileInComplete(model, projectId, organisationId, "redirect:/project/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile");
        }
        model.addAttribute("model", buildSpendProfileViewModel(projectResource, organisationId, spendProfileTableResource));

        return "project/spend-profile";
    }

    @RequestMapping(value = "/edit", method = POST)
    public String saveSpendProfile(@ModelAttribute(FORM_ATTR_NAME) SpendProfileForm form,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   ValidationHandler validationHandler,
                                   @PathVariable("projectId") final Long projectId,
                                   @PathVariable("organisationId") final Long organisationId,
                                   @ModelAttribute("loggedInUser") UserResource loggedInUser) {

        String failureView = "redirect:/project/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile/edit";
        String successView = "redirect:/project/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile";

        ValidationHandler customValidationHandler = ValidationHandler.newBindingResultHandler(bindingResult);
        spendProfileCostValidator.validate(form.getTable(), bindingResult);
        if (customValidationHandler.hasErrors()) {
            return failureView;
        }

        SpendProfileTableResource spendProfileTableResource = projectFinanceService.getSpendProfileTable(projectId, organisationId);
        spendProfileTableResource.setMonthlyCostsPerCategoryMap(form.getTable().getMonthlyCostsPerCategoryMap()); // update existing resource with user entered fields

        ServiceResult<Void> result = projectFinanceService.saveSpendProfile(projectId, organisationId, spendProfileTableResource);
        if(result.isFailure()){
            validationHandler.addAnyErrors(result);
            return failureView;
        }

        return successView;
    }

    @RequestMapping(value = "/complete", method = POST)
    public String markAsCompleteSpendProfile(Model model,
                                             @PathVariable("projectId") final Long projectId,
                                             @PathVariable("organisationId") final Long organisationId,
                                             @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        return markSpendProfileComplete(model, projectId, organisationId, "redirect:/project/" + projectId + "/partner-organisation/" + organisationId + "/spend-profile");
    }

    private String markSpendProfileComplete(Model model,
                                            Long projectId,
                                            Long organisationId,
                                            String successView) {
        return markSpendProfile(model, projectId, organisationId, true, successView);
    }

    private String markSpendProfileInComplete(Model model,
                                              Long projectId,
                                              Long organisationId,
                                              String successView) {
        return markSpendProfile(model, projectId, organisationId, false, successView);
    }

    private String markSpendProfile(Model model,
                                    Long projectId,
                                    Long organisationId,
                                    Boolean complete,
                                    String successView) {
        ServiceResult<Void> result = projectFinanceService.markSpendProfile(projectId, organisationId, complete);
        if(result.isFailure()){
            ProjectSpendProfileViewModel spendProfileViewModel = buildSpendProfileViewModel(projectId, organisationId);
            spendProfileViewModel.setObjectErrors(Collections.singletonList(new ObjectError(SPEND_PROFILE_CANNOT_MARK_AS_COMPLETE_BECAUSE_SPEND_HIGHER_THAN_ELIGIBLE.getErrorKey(), "Cannot mark as complete, because totals more than eligible")));
            //model.addAttribute("model", buildSpendProfileViewModel(projectId, organisationId));
            model.addAttribute("model", spendProfileViewModel);
            return "project/spend-profile";
        } else {
            return successView;
        }
    }

    private ProjectSpendProfileViewModel buildSpendProfileViewModel(final ProjectResource projectResource, final Long organisationId, final SpendProfileTableResource spendProfileTableResource) {
        List<SpendProfileSummaryYearModel> years = createSpendProfileSummaryYears(projectResource, spendProfileTableResource);
        SpendProfileSummaryModel summary = new SpendProfileSummaryModel(years);

        Map<String, BigDecimal> categoryToActualTotal = buildSpendProfileActualTotalsForAllCategories(spendProfileTableResource);
        List<BigDecimal> totalForEachMonth = buildTotalForEachMonth(spendProfileTableResource);

        BigDecimal totalOfAllActualTotals = buildTotalOfTotals(categoryToActualTotal);
        BigDecimal totalOfAllEligibleTotals = buildTotalOfTotals(spendProfileTableResource.getEligibleCostPerCategoryMap());

        return new ProjectSpendProfileViewModel(projectResource, organisationId, spendProfileTableResource, summary,
                spendProfileTableResource.getMarkedAsComplete(), categoryToActualTotal, totalForEachMonth,
                totalOfAllActualTotals, totalOfAllEligibleTotals);
    }

    private ProjectSpendProfileViewModel buildSpendProfileViewModel(Long projectId, Long organisationId) {
        ProjectResource projectResource = projectService.getById(projectId);
        SpendProfileTableResource spendProfileTableResource = projectFinanceService.getSpendProfileTable(projectId, organisationId);
        return buildSpendProfileViewModel(projectResource, organisationId, spendProfileTableResource);
    }

    private Map<String, BigDecimal> buildSpendProfileActualTotalsForAllCategories(SpendProfileTableResource table) {

        return CollectionFunctions.simpleLinkedMapValue(table.getMonthlyCostsPerCategoryMap(),
                (List<BigDecimal> monthlyCosts) -> monthlyCosts.stream().reduce(BigDecimal.ZERO, (d1, d2) -> d1.add(d2)));
    }

    private List<BigDecimal> buildTotalForEachMonth(SpendProfileTableResource table) {

        Map<String, List<BigDecimal>> monthlyCostsPerCategoryMap = table.getMonthlyCostsPerCategoryMap();

        List<BigDecimal> totalForEachMonth = Stream.generate(() -> BigDecimal.ZERO).limit(table.getMonths().size()).collect(Collectors.toList());

        for (int index = 0; index < totalForEachMonth.size(); index++) {

            BigDecimal totalForThisMonth = totalForEachMonth.get(index);

            for (Map.Entry<String, List<BigDecimal>> entry : monthlyCostsPerCategoryMap.entrySet()) {

                BigDecimal costForThisMonthForCategory = entry.getValue().get(index);
                totalForThisMonth = totalForThisMonth.add(costForThisMonthForCategory);
            }

            totalForEachMonth.set(index, totalForThisMonth);
        }

        return totalForEachMonth;
    }

    private BigDecimal buildTotalOfTotals(Map<String, BigDecimal> input) {

        return input.values().stream().reduce(BigDecimal.ZERO, (d1, d2) -> d1.add(d2));
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
