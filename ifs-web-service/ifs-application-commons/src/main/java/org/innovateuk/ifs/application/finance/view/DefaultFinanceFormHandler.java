package org.innovateuk.ifs.application.finance.view;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.finance.service.DefaultFinanceRowRestService;
import org.innovateuk.ifs.finance.service.FinanceRowRestService;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;

import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.BUSINESS;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;

/**
 * {@code DefaultFinanceFormHandler} retrieves the costs and handles the finance data retrieved from the request, so it can be
 * transfered to view or stored. The costs retrieved from the {@link FinanceRowRestService} are converted
 * to {@link FinanceRowItem}.
 */
@Component
public class DefaultFinanceFormHandler extends BaseFinanceFormHandler<DefaultFinanceRowRestService> implements FinanceFormHandler {

    private static final Log LOG = LogFactory.getLog(DefaultFinanceFormHandler.class);

    private final FinanceService financeService;
    private final FundingLevelResetHandler fundingLevelResetHandler;
    private final ApplicationFinanceRestService applicationFinanceRestService;
    private final GrantClaimMaximumRestService grantClaimMaximumRestService;
    private final OrganisationRestService organisationRestService;

    @Autowired
    public DefaultFinanceFormHandler(final FinanceService financeService,
                                     final DefaultFinanceRowRestService defaultFinanceRowRestService,
                                     final UnsavedFieldsManager unsavedFieldsManager,
                                     final ApplicationFinanceRestService applicationFinanceRestService,
                                     final FundingLevelResetHandler fundingLevelResetHandler,
                                     final GrantClaimMaximumRestService grantClaimMaximumRestService,
                                     final OrganisationRestService organisationRestService) {
        super(defaultFinanceRowRestService, unsavedFieldsManager);
        this.financeService = financeService;
        this.applicationFinanceRestService = applicationFinanceRestService;
        this.fundingLevelResetHandler = fundingLevelResetHandler;
        this.grantClaimMaximumRestService = grantClaimMaximumRestService;
        this.organisationRestService = organisationRestService;
    }

    @Override
    public ValidationMessages update(HttpServletRequest request, Long userId, Long applicationId, Long competitionId) {

        ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(userId, applicationId);
        if (applicationFinanceResource == null) {
            applicationFinanceResource = financeService.addApplicationFinance(userId, applicationId);
        }

        storeFinancePosition(request, applicationFinanceResource.getId(), competitionId, userId);
        return ValidationMessages.noErrors();
    }

    @Override
    public ValidationMessages storeCost(Long userId, Long applicationId, String fieldName, String value, Long competitionId) {
        return ValidationMessages.noErrors();
    }

    @Override
    public ValidationMessages addCost(Long applicationId, Long userId, Long questionId) {
        return ValidationMessages.noErrors();
    }

    @Override
    public FinanceRowItem addCostWithoutPersisting(Long applicationId, Long userId, Long questionId) {
        ApplicationFinanceResource applicationFinance = financeService.getApplicationFinance(userId, applicationId);
        return getFinanceRowRestService().addWithoutPersisting(applicationFinance.getId(), questionId).getSuccess();
    }

    private void storeFinancePosition(HttpServletRequest request, @NotNull Long applicationFinanceId, Long competitionId, Long userId) {
        List<String> financePositionKeys = simpleFilter(request.getParameterMap().keySet(), k -> k.contains("financePosition-"));
        if (!financePositionKeys.isEmpty()) {
            ApplicationFinanceResource applicationFinance = applicationFinanceRestService.getById(applicationFinanceId).getSuccess();

            financePositionKeys.forEach(k -> {
                String values = request.getParameterValues(k)[0];
                LOG.debug(String.format("finance position k : %s value: %s ", k, values));
                updateFinancePosition(applicationFinance, k, values, competitionId, userId);
            });
            applicationFinanceRestService.update(applicationFinance.getId(), applicationFinance);
        }
    }

    @Override
    public void updateFinancePosition(Long userId, Long applicationId, String fieldName, String value, Long competitionId) {
        ApplicationFinanceResource applicationFinanceResource = financeService.getApplicationFinanceDetails(userId, applicationId);
        updateFinancePosition(applicationFinanceResource, fieldName, value, competitionId, userId);
        applicationFinanceRestService.update(applicationFinanceResource.getId(), applicationFinanceResource);
    }


    private void updateFinancePosition(ApplicationFinanceResource applicationFinance, String fieldName, String value, Long competitionId, Long userId) {
        String fieldNameReplaced = fieldName.replace("financePosition-", "");
        switch (fieldNameReplaced) {
            case "organisationSize":
                OrganisationSize newValue = OrganisationSize.findById(Long.valueOf(value));
                OrganisationSize oldValue = applicationFinance.getOrganisationSize();
                handleOrganisationSizeChange(applicationFinance, competitionId, userId, oldValue, newValue);
                applicationFinance.setOrganisationSize(newValue);
                break;
            default:
                LOG.error(String.format("value not saved: %s / %s", fieldNameReplaced, value));
        }
    }

    private void handleOrganisationSizeChange(ApplicationFinanceResource applicationFinance,
                                              Long competitionId,
                                              Long userId,
                                              OrganisationSize oldValue,
                                              OrganisationSize newValue) {
        if (null != oldValue  && oldValue != newValue) {
            OrganisationResource organisation = organisationRestService.getOrganisationById(applicationFinance.getOrganisation()).getSuccess();
            boolean maximumFundingLevelOverridden = grantClaimMaximumRestService.isMaximumFundingLevelOverridden(competitionId).getSuccess();

            if (organisation.getOrganisationType().equals(BUSINESS.getId()) && !maximumFundingLevelOverridden) {
                fundingLevelResetHandler.resetFundingAndMarkAsIncomplete(applicationFinance, competitionId, userId);
            }
        }
    }

    @Override
    public RestResult<ByteArrayResource> getFile(Long applicationFinanceId) {
        throw new NotImplementedException("Finance upload is not available for the default finances");
    }
}