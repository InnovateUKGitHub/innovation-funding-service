package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.populator;

import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.*;
import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.category.*;
import org.innovateuk.ifs.finance.resource.cost.*;
import org.innovateuk.ifs.finance.resource.cost.KtpTravelCost.KtpTravelCostType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm.generateUnsavedRowId;
import static org.innovateuk.ifs.util.CollectionFunctions.toLinkedMap;

public abstract class AbstractYourProjectCostsFormPopulator {

    @Autowired
    private OrganisationRestService organisationRestService;

    public YourProjectCostsForm populateForm(long targetId, long organisationId) {
        YourProjectCostsForm form = new YourProjectCostsForm();
        BaseFinanceResource finance = getFinanceResource(targetId, organisationId);

        form.setOverhead(overhead(finance));
        form.setLabour(labour(finance));
        form.setCapitalUsageRows(toRows(finance, FinanceRowType.CAPITAL_USAGE,
                CapitalUsageRowForm.class, CapitalUsage.class));
        form.setMaterialRows(toRows(finance, FinanceRowType.MATERIALS,
                MaterialRowForm.class, Materials.class));
        form.setOtherRows(toRows(finance, FinanceRowType.OTHER_COSTS,
                OtherCostRowForm.class, OtherCost.class));
        form.setSubcontractingRows(toRows(finance, FinanceRowType.SUBCONTRACTING_COSTS,
                SubcontractingRowForm.class, SubContractingCost.class));
        form.setTravelRows(toRows(finance, FinanceRowType.TRAVEL,
                TravelRowForm.class, TravelCost.class));

        form.setVatForm(vat(finance));
        form.setProcurementOverheadRows(toRows(finance, FinanceRowType.PROCUREMENT_OVERHEADS,
                ProcurementOverheadRowForm.class, ProcurementOverhead.class));

        form.setAssociateSalaryCostRows(associateSalaryCostRows(finance));
        form.setAssociateDevelopmentCostRows(associateDevelopment(finance));
        form.setConsumableCostRows(toRows(finance, FinanceRowType.CONSUMABLES,
                ConsumablesRowForm.class, Consumable.class));
        form.setKnowledgeBaseCostRows(toRows(finance, FinanceRowType.KNOWLEDGE_BASE,
                KnowledgeBaseCostRowForm.class, KnowledgeBaseCost.class,
                numberOfRows(finance, FinanceRowType.KNOWLEDGE_BASE) < 2));
        form.setAssociateSupportCostRows(toRows(finance, FinanceRowType.ASSOCIATE_SUPPORT,
                AssociateSupportCostRowForm.class, AssociateSupportCost.class));
        form.setEstateCostRows(toRows(finance, FinanceRowType.ESTATE_COSTS,
                EstateCostRowForm.class, EstateCost.class));

        form.setKtpTravelCostRows(toRows(finance, FinanceRowType.KTP_TRAVEL,
                KtpTravelRowForm.class, KtpTravelCost.class, Comparator.comparing(cost ->
                        ofNullable(cost.getType()).map(KtpTravelCostType::ordinal).orElse(2))));

        form.setAdditionalCompanyCostForm(additionalCompanyCostForm(finance));

        form.setFecModelEnabled(finance.getFecModelEnabled());
        form.setGrantClaimPercentage(finance.getGrantClaimPercentage());
        form.setAcademicAndSecretarialSupportForm(academicAndSecretarialSupportCostRowForm(finance));

        OrganisationResource organisation = organisationRestService.getOrganisationById(organisationId).getSuccess();

        if (ApplicationFinanceResource.class.equals(finance.getClass()) && organisation.getOrganisationTypeEnum() == OrganisationTypeEnum.KNOWLEDGE_BASE) {
            form.setJustificationForm(justificationForm(finance));
        }

        return form;
    }

    private AcademicAndSecretarialSupportCostRowForm academicAndSecretarialSupportCostRowForm(BaseFinanceResource finance) {
        Optional<FinanceRowCostCategory> financeRowCostCategory = Optional.ofNullable(
                finance.getFinanceOrganisationDetails().get(FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT));

        if (financeRowCostCategory.isPresent() && !financeRowCostCategory.get().getCosts().isEmpty()) {
            AcademicAndSecretarialSupport academicAndSecretarialSupport = (AcademicAndSecretarialSupport) financeRowCostCategory.get().getCosts().get(0);
            return new AcademicAndSecretarialSupportCostRowForm(academicAndSecretarialSupport);
        }

        return new AcademicAndSecretarialSupportCostRowForm();
    }

    private JustificationForm justificationForm(BaseFinanceResource finance) {

        ApplicationFinanceResource applicationFinanceResource = (ApplicationFinanceResource) finance;
        if (StringUtils.isEmpty(applicationFinanceResource.getJustification())) {
            return new JustificationForm();
        }
        return new JustificationForm(Boolean.TRUE, applicationFinanceResource.getJustification());
    }

    private int numberOfRows(BaseFinanceResource finance, FinanceRowType type) {
        return ofNullable(finance.getFinanceOrganisationDetails().get(type)).map(category -> category.getCosts().size()).orElse(0);
    }

    private LabourForm labour(BaseFinanceResource finance) {
        LabourCostCategory costCategory = (LabourCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.LABOUR);
        LabourForm labourForm = new LabourForm();
        if (costCategory != null) {
            costCategory.calculateTotal();
            labourForm.setWorkingDaysPerYear(costCategory.getWorkingDaysPerYear());
            labourForm.setRows(labourCosts(costCategory));
        }
        return labourForm;
    }

    private OverheadForm overhead(BaseFinanceResource finance) {
        OverheadCostCategory costCategory = (OverheadCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.OVERHEADS);
        if (costCategory != null) {
            Overhead overhead = costCategory.getCosts().stream().findFirst().map(Overhead.class::cast).orElseThrow(() -> new IFSRuntimeException("Missing expected overheads cost."));
            String filename = overheadFile(overhead.getId()).map(FileEntryResource::getName).orElse(null);
            return new OverheadForm(overhead, filename);
        }
        return new OverheadForm();
    }

    private Map<String, LabourRowForm> labourCosts(LabourCostCategory costCategory) {
        Map<String, LabourRowForm> rows = costCategory.getCosts().stream()
                .map(LabourCost.class::cast)
                .map(LabourRowForm::new)
                .collect(toLinkedMap((row) -> String.valueOf(row.getCostId()), Function.identity()));

        if (shouldAddEmptyRow()) {
            rows.put(generateUnsavedRowId(), new LabourRowForm());
        }
        return rows;
    }

    private VatForm vat(BaseFinanceResource finance) {
        VatCostCategory costCategory = (VatCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.VAT);
        if (costCategory != null) {
            Vat vat = costCategory.getCosts().stream().findFirst().map(Vat.class::cast).orElseThrow(() -> new IFSRuntimeException("Missing expected Vat cost"));
            return new VatForm(vat);
        }
        return new VatForm();
    }

    private AdditionalCompanyCostForm additionalCompanyCostForm(BaseFinanceResource finance) {
        AdditionalCompanyCostCategory costCategory = (AdditionalCompanyCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.ADDITIONAL_COMPANY_COSTS);
        if (costCategory != null) {
            return new AdditionalCompanyCostForm(costCategory);
        }
        return null;
    }

    private Map<String, AssociateSalaryCostRowForm> associateSalaryCostRows(BaseFinanceResource finance) {
        DefaultCostCategory costCategory = (DefaultCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.ASSOCIATE_SALARY_COSTS);
        if (costCategory != null) {
            Map<String, AssociateSalaryCostRowForm> rows = costCategory.getCosts().stream()
                    .map((cost) -> (AssociateSalaryCost) cost)
                    .map(AssociateSalaryCostRowForm::new)
                    .limit(2)
                    .collect(toLinkedMap((row) -> String.valueOf(row.getCostId()), Function.identity()));
            if (rows.isEmpty()) {
                rows.put(generateUnsavedRowId(), new AssociateSalaryCostRowForm("Associate 1"));
            }
            if (rows.size() == 1) {
                rows.put(generateUnsavedRowId(), new AssociateSalaryCostRowForm("Associate 2"));
            }
            int index = 1;
            for (Entry<String, AssociateSalaryCostRowForm> entry : rows.entrySet()) {
                entry.getValue().setRole("Associate " + index);
                index++;
            }
            return rows;
        }
        return new HashMap<>();
    }

    private Map<String, AssociateDevelopmentCostRowForm> associateDevelopment(BaseFinanceResource finance) {
        DefaultCostCategory costCategory = (DefaultCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.ASSOCIATE_DEVELOPMENT_COSTS);
        if (costCategory != null) {
            Map<String, AssociateDevelopmentCostRowForm> rows = costCategory.getCosts().stream()
                    .map((cost) -> (AssociateDevelopmentCost) cost)
                    .map(AssociateDevelopmentCostRowForm::new)
                    .limit(2)
                    .collect(toLinkedMap((row) -> String.valueOf(row.getCostId()), Function.identity()));
            if (rows.isEmpty()) {
                rows.put(generateUnsavedRowId(), new AssociateDevelopmentCostRowForm("Associate 1", getAssociateSalaryDuration(finance, 0)));
            }
            if (rows.size() == 1) {
                rows.put(generateUnsavedRowId(), new AssociateDevelopmentCostRowForm("Associate 2", getAssociateSalaryDuration(finance, 1)));
            }
            int index = 1;
            for (Entry<String, AssociateDevelopmentCostRowForm> entry : rows.entrySet()) {
                entry.getValue().setRole("Associate " + index);
                entry.getValue().setDuration(getAssociateSalaryDuration(finance, index - 1));
                index++;
            }
            return rows;
        }
        return new HashMap<>();
    }

    private Integer getAssociateSalaryDuration(BaseFinanceResource finance, int index) {
        DefaultCostCategory costCategory = (DefaultCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.ASSOCIATE_SALARY_COSTS);
        if (costCategory.getCosts().size() > index) {
            AssociateSalaryCost cost = (AssociateSalaryCost) costCategory.getCosts().get(index);
            return cost.getDuration();
        }
        return null;

    }

    private <C extends AbstractFinanceRowItem, F extends AbstractCostRowForm<C>> Map<String, F> toRows(BaseFinanceResource finance, FinanceRowType financeRowType, Class<F> formClazz, Class<C> costClazz) {
        return toRows(finance, financeRowType, formClazz, costClazz, true);
    }

    private <C extends AbstractFinanceRowItem, F extends AbstractCostRowForm<C>> Map<String, F> toRows(BaseFinanceResource finance, FinanceRowType financeRowType, Class<F> formClazz, Class<C> costClazz, boolean addEmptyRowOverride) {
        return toRows(finance, financeRowType, formClazz, costClazz, addEmptyRowOverride, null);
    }

    private <C extends AbstractFinanceRowItem, F extends AbstractCostRowForm<C>> Map<String, F> toRows(BaseFinanceResource finance, FinanceRowType financeRowType, Class<F> formClazz, Class<C> costClazz, Comparator<F> comparator) {
        return toRows(finance, financeRowType, formClazz, costClazz, true, comparator);
    }

    private <C extends AbstractFinanceRowItem, F extends AbstractCostRowForm<C>> Map<String, F> toRows(BaseFinanceResource finance, FinanceRowType financeRowType, Class<F> formClazz, Class<C> costClazz, boolean addEmptyRowOverride, Comparator<F> comparator) {
        DefaultCostCategory costCategory = (DefaultCostCategory) finance.getFinanceOrganisationDetails().get(financeRowType);

        if (costCategory != null) {
            Stream<F> stream =  costCategory.getCosts().stream()
                    .map((cost) -> (C) cost)
                    .map(cost -> {
                        try {
                            return formClazz.getConstructor(costClazz).newInstance(cost);
                        } catch (NoSuchMethodException |
                                IllegalAccessException |
                                InstantiationException |
                                InvocationTargetException e) {
                            throw new IFSRuntimeException(e);
                        }
                    });
            if (comparator != null) {
                stream = stream.sorted(comparator);
            }
            Map<String, F> rows = stream
                    .collect(toLinkedMap((row) -> String.valueOf(row.getCostId()), Function.identity()));
            if (shouldAddEmptyRow() && addEmptyRowOverride) {
                try {
                    rows.put(generateUnsavedRowId(), formClazz.newInstance());
                } catch (IllegalAccessException |
                        InstantiationException e) {
                    throw new IFSRuntimeException(e);
                }
            }
            return rows;
        }
        return new HashMap<>();
    }

    protected abstract BaseFinanceResource getFinanceResource(long targetId, long organisationId);

    protected abstract boolean shouldAddEmptyRow();

    protected abstract Optional<FileEntryResource> overheadFile(long costId);
}
