package org.innovateuk.ifs.application.forms.sections.yourprojectcosts.saver;

import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.AdditionalCompanyCostCategory;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.category.OverheadCostCategory;
import org.innovateuk.ifs.finance.resource.category.VatCostCategory;
import org.innovateuk.ifs.finance.resource.cost.*;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRowRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.AbstractCostRowForm.UNSAVED_ROW_PREFIX;

@Component
public class YourProjectCostsAutosaver {

    private final static Logger LOG = LoggerFactory.getLogger(YourProjectCostsAutosaver.class);

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Autowired
    private ApplicationFinanceRowRestService financeRowRestService;

    public Optional<Long> autoSave(String field, String value, long applicationId, UserResource user) {
        OrganisationResource organisation = organisationRestService.getByUserAndApplicationId(user.getId(), applicationId).getSuccess();
        ApplicationFinanceResource finance = applicationFinanceRestService.getApplicationFinance(applicationId, organisation.getId()).getSuccess();

        try {
            if (field.equals("labour.workingDaysPerYear")) {
                finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisation.getId()).getSuccess();
                LabourCostCategory category = (LabourCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.LABOUR);
                LabourCost workingDaysCost = category.getWorkingDaysPerYearCostItem();
                workingDaysCost.setLabourDays(Integer.parseInt(value));
                financeRowRestService.update(workingDaysCost).getSuccess();
            } else if (field.startsWith("labour.rows")) {
                return autosaveLabourCost(field, value, finance);
            } else if (field.startsWith("overhead")) {
                return autosaveOverheadCost(field, value, finance, applicationId, organisation.getId());
            } else if (field.startsWith("materialRows")) {
                return autosaveMaterialCost(field, value, finance);
            } else if (field.startsWith("capitalUsageRows")) {
                return autosaveCapitalUsageCost(field, value, finance);
            } else if (field.startsWith("subcontractingRows")) {
                return autosaveSubcontractingCost(field, value, finance);
            } else if (field.startsWith("travelRows")) {
                return autosaveTravelCost(field, value, finance);
            } else if (field.startsWith("otherRows")) {
                return autosaveOtherCost(field, value, finance);
            } else if (field.startsWith("associateSalaryCostRows")) {
                return autosaveAssociateSalaryCost(field, value, finance);
            } else if (field.startsWith("associateDevelopmentCostRows")) {
                return autosaveAssociateDevelopmentCost(field, value, finance);
            } else if (field.startsWith("associateSupportCostRows")) {
                return autosaveAssociateSupportCost(field, value, finance);
            } else if (field.startsWith("consumableCostRows")) {
                return autosaveConsumableCost(field, value, finance);
            } else if (field.startsWith("knowledgeBaseCostRows")) {
                return autosaveKnowledgeBaseCost(field, value, finance);
            } else if (field.startsWith("additionalCompanyCostForm")) {
                return autosaveAdditionalCompanyCostForm(field, value, finance);
            } else if (field.startsWith("estateCostRows")) {
                return autosaveEstateCost(field, value, finance);
            } else if (field.startsWith("procurementOverheadRows")) {
                return autosaveProcurementOverheadCost(field, value, finance);
            } else if (field.startsWith("vat")) {
                return autosaveVAT(value, finance, applicationId, organisation.getId());
            } else {
                throw new IFSRuntimeException(format("Auto save field not handled %s", field), emptyList());
            }
        } catch (Exception e) {
            LOG.debug("Error auto saving", e);
            LOG.info(format("Unable to auto save field (%s) value (%s)", field, value));
        }
        return Optional.empty();
    }

    private Optional<Long> autosaveAdditionalCompanyCostForm(String field, String value, ApplicationFinanceResource finance) {
        String id = idFromRowPath(field);
        String rowField = fieldFromRowPath(field);
        AdditionalCompanyCostCategory costCategory = (AdditionalCompanyCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.ADDITIONAL_COMPANY_COSTS);
        AdditionalCompanyCost toSave;
        switch (rowField) {
            case "associateSalary.cost":
                toSave = costCategory.getAssociateSalary();
                toSave.setCost(new BigInteger(value));
                break;
            case "associateSalary.description":
                toSave = costCategory.getAssociateSalary();
                toSave.setDescription(value);
                break;
            case "managementSupervision.cost":
                toSave = costCategory.getManagementSupervision();
                toSave.setCost(new BigInteger(value));
                break;
            case "managementSupervision.description":
                toSave = costCategory.getManagementSupervision();
                toSave.setDescription(value);
                break;
            case "otherStaff.cost":
                toSave = costCategory.getOtherStaff();
                toSave.setCost(new BigInteger(value));
                break;
            case "otherStaff.description":
                toSave = costCategory.getOtherStaff();
                toSave.setDescription(value);
                break;
            case "capitalEquipment.cost":
                toSave = costCategory.getCapitalEquipment();
                toSave.setCost(new BigInteger(value));
                break;
            case "capitalEquipment.description":
                toSave = costCategory.getCapitalEquipment();
                toSave.setDescription(value);
                break;
            case "otherCosts.cost":
                toSave = costCategory.getOtherCosts();
                toSave.setCost(new BigInteger(value));
                break;
            case "otherCosts.description":
                toSave = costCategory.getOtherCosts();
                toSave.setDescription(value);
                break;
            default:
                throw new IFSRuntimeException(format("Auto save consumable field not handled %s", rowField), emptyList());
        }
        financeRowRestService.update(toSave);
        return Optional.empty();
    }

    private Optional<Long> autosaveKnowledgeBaseCost(String field, String value, ApplicationFinanceResource finance) {
        String id = idFromRowPath(field);
        String rowField = fieldFromRowPath(field);
        KnowledgeBaseCost cost = getCost(id, () -> new KnowledgeBaseCost(finance.getId()));
        switch (rowField) {
            case "description":
                cost.setDescription(value);
                break;
            case "cost":
                cost.setCost(new BigInteger(value));
                break;
            default:
                throw new IFSRuntimeException(format("Auto save consumable field not handled %s", rowField), emptyList());
        }
        financeRowRestService.update(cost);
        return Optional.of(cost.getId());
    }

    private Optional<Long> autosaveConsumableCost(String field, String value, ApplicationFinanceResource finance) {
        String id = idFromRowPath(field);
        String rowField = fieldFromRowPath(field);
        Consumable cost = getCost(id, () -> new Consumable(finance.getId()));
        switch (rowField) {
            case "item":
                cost.setItem(value);
                break;
            case "quantity":
                cost.setQuantity(Integer.parseInt(value));
                break;
            case "cost":
                cost.setCost(new BigInteger(value));
                break;
            default:
                throw new IFSRuntimeException(format("Auto save consumable field not handled %s", rowField), emptyList());
        }
        financeRowRestService.update(cost);
        return Optional.of(cost.getId());
    }

    private Optional<Long> autosaveAssociateDevelopmentCost(String field, String value, ApplicationFinanceResource finance) {
        String id = idFromRowPath(field);
        String rowField = fieldFromRowPath(field);
        AssociateDevelopmentCost cost = getCost(id, () -> new AssociateDevelopmentCost(finance.getId()));

        switch (rowField) {
            case "role":
                cost.setRole(value);
                break;
            case "duration":
                cost.setDuration(Integer.parseInt(value));
                break;
            case "cost":
                cost.setCost(new BigInteger(value));
                break;
            default:
                throw new IFSRuntimeException(format("Auto save associate development field not handled %s", rowField), emptyList());
        }
        financeRowRestService.update(cost);
        return Optional.of(cost.getId());
    }

    private Optional<Long> autosaveLabourCost(String field, String value, ApplicationFinanceResource finance) {
        String id = idFromRowPath(field);
        String rowField = fieldFromRowPath(field);
        LabourCost cost = getCost(id, () -> new LabourCost(finance.getId()));

        switch (rowField) {
            case "role":
                cost.setRole(value);
                break;
            case "gross":
                cost.setGrossEmployeeCost(new BigDecimal(value));
                break;
            case "days":
                cost.setLabourDays(Integer.parseInt(value));
                break;
            default:
                throw new IFSRuntimeException(format("Auto save labour field not handled %s", rowField), emptyList());
        }
        financeRowRestService.update(cost);
        return Optional.of(cost.getId());
    }

    private Optional<Long> autosaveAssociateSalaryCost(String field, String value, ApplicationFinanceResource finance) {
        String id = idFromRowPath(field);
        String rowField = fieldFromRowPath(field);
        AssociateSalaryCost cost = getCost(id, () -> new AssociateSalaryCost(finance.getId()));

        switch (rowField) {
            case "role":
                cost.setRole(value);
                break;
            case "duration":
                cost.setDuration(Integer.parseInt(value));
                break;
            case "cost":
                cost.setCost(new BigInteger(value));
                break;
            default:
                throw new IFSRuntimeException(format("Auto save associate salary field not handled %s", rowField), emptyList());
        }
        financeRowRestService.update(cost);
        return Optional.of(cost.getId());
    }

    private Optional<Long> autosaveAssociateSupportCost(String field, String value, ApplicationFinanceResource finance) {
        String id = idFromRowPath(field);
        String rowField = fieldFromRowPath(field);
        AssociateSupportCost cost = getCost(id, () -> new AssociateSupportCost(finance.getId()));
        switch (rowField) {
            case "description":
                cost.setDescription(value);
                break;
            case "cost":
                cost.setCost(new BigInteger(value));
                break;
            default:
                throw new IFSRuntimeException(format("Auto save associate support field not handled %s", rowField), emptyList());
        }
        financeRowRestService.update(cost);
        return Optional.of(cost.getId());
    }

    private Optional<Long> autosaveEstateCost(String field, String value, ApplicationFinanceResource finance) {
        String id = idFromRowPath(field);
        String rowField = fieldFromRowPath(field);
        EstateCost cost = getCost(id, () -> new EstateCost(finance.getId()));
        switch (rowField) {
            case "description":
                cost.setDescription(value);
                break;
            case "cost":
                cost.setCost(new BigInteger(value));
                break;
            default:
                throw new IFSRuntimeException(format("Auto save estate costs field not handled %s", rowField), emptyList());
        }
        financeRowRestService.update(cost);
        return Optional.of(cost.getId());
    }

    private Optional<Long> autosaveOverheadCost(String field, String value, ApplicationFinanceResource finance, long applicationId, Long organisationId) {
        if (field.equals("overhead.totalSpreadsheet")) {
            finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();
            OverheadCostCategory category = (OverheadCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.OVERHEADS);
            Overhead overheadCost = (Overhead) category.getCosts().get(0);
            overheadCost.setRate(Integer.valueOf(value));
            financeRowRestService.update(overheadCost);
        }
        return Optional.empty();
    }

    private Optional<Long> autosaveVAT(String value, ApplicationFinanceResource finance, long applicationId, Long organisationId) {
        finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();
        VatCostCategory category = (VatCostCategory) finance.getFinanceOrganisationDetails().get(FinanceRowType.VAT);
        Vat vat = (Vat) category.getCosts().get(0);
        vat.setRegistered(Boolean.valueOf(value));
        financeRowRestService.update(vat);
        return Optional.empty();
    }

    private Optional<Long> autosaveMaterialCost(String field, String value, ApplicationFinanceResource finance) {
        String id = idFromRowPath(field);
        String rowField = fieldFromRowPath(field);
        Materials cost = getCost(id, () -> new Materials(finance.getId()));
        switch (rowField) {
            case "item":
                cost.setItem(value);
                break;
            case "quantity":
                cost.setQuantity(Integer.parseInt(value));
                break;
            case "cost":
                cost.setCost(new BigDecimal(value));
                break;
            default:
                throw new IFSRuntimeException(format("Auto save material field not handled %s", rowField), emptyList());
        }
        financeRowRestService.update(cost);
        return Optional.of(cost.getId());
    }

    private Optional<Long> autosaveProcurementOverheadCost(String field, String value, ApplicationFinanceResource finance) {
        String id = idFromRowPath(field);
        String rowField = fieldFromRowPath(field);
        ProcurementOverhead cost = getCost(id, () -> new ProcurementOverhead(finance.getId()));
        switch (rowField) {
            case "item":
                cost.setItem(value);
                break;
            case "companyCost":
                cost.setCompanyCost(Integer.parseInt(value));
                break;
            case "projectCost":
                cost.setProjectCost(new BigDecimal(value));
                break;
            default:
                throw new IFSRuntimeException(format("Auto save procurement overhead field not handled %s", rowField), emptyList());
        }
        financeRowRestService.update(cost);
        return Optional.of(cost.getId());
    }

    private Optional<Long> autosaveCapitalUsageCost(String field, String value, ApplicationFinanceResource finance) {
        String id = idFromRowPath(field);
        String rowField = fieldFromRowPath(field);
        CapitalUsage cost = getCost(id, () -> new CapitalUsage(finance.getId()));
        switch (rowField) {
            case "item":
                cost.setDescription(value);
                break;
            case "newItem":
                cost.setExisting(Boolean.parseBoolean(value) ? "New" : "Existing");
                break;
            case "deprecation":
                cost.setDeprecation(Integer.valueOf(value));
                break;
            case "netValue":
                cost.setNpv(new BigDecimal(value));
                break;
            case "residualValue":
                cost.setResidualValue(new BigDecimal(value));
                break;
            case "utilisation":
                cost.setUtilisation(Integer.valueOf(value));
                break;
            default:
                throw new IFSRuntimeException(format("Auto save capital usage field not handled %s", rowField), emptyList());
        }
        financeRowRestService.update(cost);
        return Optional.of(cost.getId());
    }

    private Optional<Long> autosaveSubcontractingCost(String field, String value, ApplicationFinanceResource finance) {
        String id = idFromRowPath(field);
        String rowField = fieldFromRowPath(field);
        SubContractingCost cost = getCost(id, () -> new SubContractingCost(finance.getId()));
        switch (rowField) {
            case "name":
                cost.setName(value);
                break;
            case "country":
                cost.setCountry(value);
                break;
            case "role":
                cost.setRole(value);
                break;
            case "cost":
                cost.setCost(new BigDecimal(value));
                break;
            default:
                throw new IFSRuntimeException(format("Auto save sub-contracting field not handled %s", rowField), emptyList());
        }
        financeRowRestService.update(cost);
        return Optional.of(cost.getId());
    }

    private Optional<Long> autosaveTravelCost(String field, String value, ApplicationFinanceResource finance) {
        String id = idFromRowPath(field);
        String rowField = fieldFromRowPath(field);
        TravelCost cost = getCost(id, () -> new TravelCost(finance.getId()));
        switch (rowField) {
            case "item":
                cost.setItem(value);
                break;
            case "times":
                cost.setQuantity(Integer.valueOf(value));
                break;
            case "eachCost":
                cost.setCost(new BigDecimal(value));
                break;
            default:
                throw new IFSRuntimeException(format("Auto save travel field not handled %s", rowField), emptyList());
        }
        financeRowRestService.update(cost);
        return Optional.of(cost.getId());
    }

    private Optional<Long> autosaveOtherCost(String field, String value, ApplicationFinanceResource finance) {
        String id = idFromRowPath(field);
        String rowField = fieldFromRowPath(field);
        OtherCost cost = getCost(id, () -> new OtherCost(finance.getId()));
        switch (rowField) {
            case "description":
                cost.setDescription(value);
                break;
            case "estimate":
                cost.setCost(new BigDecimal(value));
                break;
            default:
                throw new IFSRuntimeException(format("Auto save other cost field not handled %s", rowField), emptyList());
        }
        financeRowRestService.update(cost);
        return Optional.of(cost.getId());
    }

    private <R extends FinanceRowItem> R getCost(String id, Supplier<R> creator) {
        if (id.startsWith(UNSAVED_ROW_PREFIX)) {
            return (R) financeRowRestService.create(creator.get()).getSuccess();
        } else {
            return (R) financeRowRestService.get(Long.valueOf(id)).getSuccess();
        }
    }

    private String idFromRowPath(String field) {
        return field.substring(field.indexOf('[') + 1, field.indexOf(']'));
    }

    private String fieldFromRowPath(String field) {
        return field.substring(field.indexOf("].") + 2);
    }
}
