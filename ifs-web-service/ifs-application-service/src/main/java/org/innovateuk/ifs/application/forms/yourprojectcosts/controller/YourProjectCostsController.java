package org.innovateuk.ifs.application.forms.yourprojectcosts.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.application.forms.yourprojectcosts.form.AbstractCostRowForm;
import org.innovateuk.ifs.application.forms.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.application.forms.yourprojectcosts.populator.YourProjectCostsFormPopulator;
import org.innovateuk.ifs.application.forms.yourprojectcosts.populator.YourProjectCostsViewModelPopulator;
import org.innovateuk.ifs.application.forms.yourprojectcosts.saver.YourProjectCostsSaver;
import org.innovateuk.ifs.application.forms.yourprojectcosts.viewmodel.YourProjectCostsViewModel;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.LabourCost;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.APPLICATION_BASE_URL;

@Controller
@RequestMapping(APPLICATION_BASE_URL + "{applicationId}/form/your-project-costs/{sectionId}")
@PreAuthorize("hasAuthority('applicant')")
@SecuredBySpring(value = "YOUR_PROJECT_COSTS_APPLICANT", description = "Applicants can all fill out the Your project costs section of the application.")
public class YourProjectCostsController {
    private final static Logger LOG = LoggerFactory.getLogger(YourProjectCostsSaver.class);

    private static final String VIEW = "application/your-project-costs";

    @Autowired
    private YourProjectCostsFormPopulator formPopulator;

    @Autowired
    private YourProjectCostsViewModelPopulator viewModelPopulator;

    @Autowired
    private YourProjectCostsSaver saver;


    @GetMapping
    public String viewYourProjectCosts(Model model,
                                       UserResource user,
                                       @PathVariable long applicationId,
                                       @PathVariable long sectionId,
                                       @ModelAttribute("form") YourProjectCostsForm form) {

        YourProjectCostsViewModel viewModel = viewModelPopulator.populate(applicationId, sectionId, user);
        model.addAttribute("model", viewModel);
        formPopulator.populateForm(form, applicationId, user, Optional.empty());
        return VIEW;
    }

    @PostMapping
    public String saveYourProjectCosts(Model model,
                                       UserResource user,
                                       @PathVariable long applicationId,
                                       @PathVariable long sectionId,
                                       @ModelAttribute("form") YourProjectCostsForm form) {
        saver.save(applicationId, form, user);
        return redirectToYourFinances(applicationId);
    }

    @PostMapping(params = "remove_cost")
    public String removeRowPost(Model model,
                                UserResource user,
                                @PathVariable long applicationId,
                                @PathVariable long sectionId,
                                @ModelAttribute("form") YourProjectCostsForm form,
                                @RequestParam("remove_row") List<String> removeRequest) {

        String id = removeRequest.get(0);
        FinanceRowType type = FinanceRowType.valueOf(removeRequest.get(1));
        saver.removeRowFromForm(form, type, id);
        recalculateTotals(form);
        YourProjectCostsViewModel viewModel = viewModelPopulator.populate(applicationId, sectionId, user);
        model.addAttribute("model", viewModel);
        return VIEW;
    }

    @PostMapping(params = "add_cost")
    public String addRowPost(Model model,
                                UserResource user,
                                @PathVariable long applicationId,
                                @PathVariable long sectionId,
                                @ModelAttribute("form") YourProjectCostsForm form,
                                @RequestParam("add_cost") FinanceRowType rowType) throws InstantiationException, IllegalAccessException {

        saver.addRowForm(form, rowType, applicationId, user);
        recalculateTotals(form);
        YourProjectCostsViewModel viewModel = viewModelPopulator.populate(applicationId, sectionId, user);
        model.addAttribute("model", viewModel);
        return VIEW;
    }

    @PostMapping("remove-row/{rowId}")
    public @ResponseBody
    JsonNode ajaxRemoveRow(UserResource user,
                           @PathVariable long applicationId,
                           @PathVariable String rowId) {
        saver.removeFinanceRow(rowId);
        return new ObjectMapper().createObjectNode();
    }

    @PostMapping("add-row/{rowType}")
    public String ajaxAddRow(Model model,
                             UserResource user,
                             @PathVariable long applicationId,
                             @PathVariable FinanceRowType rowType) throws InstantiationException, IllegalAccessException {
        YourProjectCostsForm form = new YourProjectCostsForm();
        AbstractCostRowForm row = saver.addRowForm(form, rowType, applicationId, user);
        model.addAttribute("form", form);
        model.addAttribute("id", row.getCostId());
        model.addAttribute("row", row);
        return String.format("application/your-project-costs-fragments :: ajax_%s_row", rowType.name().toLowerCase());
    }

    private void recalculateTotals(YourProjectCostsForm form) {
        form.getLabourCosts().forEach((id, row) -> {
            LabourCost cost = row.toCost();
            row.setTotal(cost.getTotal(form.getWorkingDaysPerYear()));
            row.setRate(cost.getRate(form.getWorkingDaysPerYear()));
        });
        recalculateTotal(form.getMaterialRows());
        recalculateTotal(form.getCapitalUsageRows());
        recalculateTotal(form.getSubcontractingRows());
        recalculateTotal(form.getTravelRows());
        recalculateTotal(form.getOtherRows());
    }

    private void recalculateTotal(Map<String, ? extends AbstractCostRowForm> rows) {
        rows.forEach((id, row) -> {
            FinanceRowItem cost = row.toCost();
            row.setTotal(cost.getTotal());
        });
    }


    private String redirectToYourFinances(long applicationId) {
        return String.format("redirect:/application/%d/form/%s", applicationId, SectionType.FINANCE.name());
    }
}
