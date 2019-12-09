package org.innovateuk.ifs.application.forms.academiccosts.saver;

import org.innovateuk.ifs.application.forms.academiccosts.form.AcademicCostForm;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.AcademicCost;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRowRestService;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Component
public class AcademicCostSaver extends AbstractAcademicCostSaver {
    private final static Logger LOG = LoggerFactory.getLogger(AcademicCostSaver.class);

    @Autowired
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Autowired
    private ApplicationFinanceRowRestService financeRowRestService;

    public ServiceResult<Void> save(AcademicCostForm form, long applicationId, long organisationId) {
        ApplicationFinanceResource finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();
        return save(form, finance);
    }


    public Optional<Long> autoSave(String field, String value, long applicationId, long organisationId) {
        try {
            ApplicationFinanceResource finance = applicationFinanceRestService.getFinanceDetails(applicationId, organisationId).getSuccess();
            Map<String, AcademicCost> costMap = mapCostsByName(finance);

            switch (field) {
                case "tsbReference":
                    AcademicCost tsbReference = costMap.get("tsb_reference");
                    tsbReference.setItem(value);
                    financeRowRestService.update(tsbReference).getSuccess();
                    break;
                default:
                    AcademicCost cost = costMap.get(formFieldToCostName.get(field));
                    cost.setCost(new BigDecimal(value));
                    financeRowRestService.update(cost).getSuccess();
                    break;
            }
        } catch (Exception e) {
            LOG.debug("Error auto saving", e);
            LOG.info(String.format("Unable to auto save field (%s) value (%s)", field, value));
        }

        return Optional.empty();
    }

    @Override
    protected FinanceRowRestService financeRowRestService() {
        return financeRowRestService;
    }
}
