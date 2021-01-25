package org.innovateuk.ifs.procurement.milestone.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceService;
import org.innovateuk.ifs.procurement.milestone.domain.ApplicationProcurementMilestone;
import org.innovateuk.ifs.procurement.milestone.repository.ApplicationProcurementMilestoneRepository;
import org.innovateuk.ifs.procurement.milestone.repository.ProcurementMilestoneRepository;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneId;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ApplicationProcurementMilestoneServiceImpl
        extends AbstractProcurementMilestoneServiceImpl<ApplicationProcurementMilestoneResource, ApplicationProcurementMilestone, ApplicationProcurementMilestoneId>
        implements ApplicationProcurementMilestoneService {

    @Autowired
    private ApplicationProcurementMilestoneRepository repository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private ApplicationFinanceService applicationFinanceService;

    @Override
    protected ServiceResult<ApplicationProcurementMilestone> newDomain(ApplicationProcurementMilestoneResource resource) {
        return find(applicationFinanceRepository.findByApplicationIdAndOrganisationId(resource.getApplicationId(), resource.getOrganisationId()),
                notFoundError(ApplicationFinance.class, resource.getApplicationId(), resource.getOrganisationId()))
                .andOnSuccessReturn(applicationFinance -> {
                    ApplicationProcurementMilestone domain = new ApplicationProcurementMilestone();
                    domain.setApplicationFinance(applicationFinance);
                    return domain;
                });
    }

    @Override
    protected ProcurementMilestoneRepository<ApplicationProcurementMilestone> getRepository() {
        return repository;
    }

    @Override
    public ServiceResult<List<ApplicationProcurementMilestoneResource>> getByApplicationIdAndOrganisationId(long applicationId, long organisationId) {
        return serviceSuccess(repository.findByApplicationFinanceApplicationIdAndApplicationFinanceOrganisationIdOrderByMonthAsc(applicationId, organisationId)
                .stream()
                .map(mapper::mapToResource)
                .collect(toList()));
    }

    @Override
    public ServiceResult<Boolean> arePaymentMilestonesEqualToFunding(long applicationId, long organisationId) {
        return applicationFinanceService.financeDetails(applicationId, organisationId).andOnSuccessReturn(finance -> {
            BigInteger totalPayments = repository.findByApplicationFinanceApplicationIdAndApplicationFinanceOrganisationIdOrderByMonthAsc(applicationId, organisationId)
                    .stream()
                    .map(ApplicationProcurementMilestone::getPayment)
                    .reduce(BigInteger.ZERO, BigInteger::add);
            BigInteger totalFunding = finance.getTotalFundingSought().setScale(0, RoundingMode.HALF_UP).toBigInteger();
            return totalFunding.equals(totalPayments);
        });
    }

    @Override
    public ServiceResult<Optional<Integer>> findMaxMilestoneMonth(long applicationId) {
        return find(applicationRepository.findById(applicationId), notFoundError(Application.class, applicationId))
                .andOnSuccessReturn(Application::getMaxMilestoneMonth);
    }
}
