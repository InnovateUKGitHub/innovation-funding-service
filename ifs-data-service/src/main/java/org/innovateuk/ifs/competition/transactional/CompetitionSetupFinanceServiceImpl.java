package org.innovateuk.ifs.competition.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.form.resource.FormInputType.STAFF_COUNT;
import static org.innovateuk.ifs.form.resource.FormInputType.STAFF_TURNOVER;


/**
 * Service implementation to deal with the finance part of competition setup.
 */
@Service
public class CompetitionSetupFinanceServiceImpl extends BaseTransactionalService implements CompetitionSetupFinanceService {

    private static final Log LOG = LogFactory.getLog(CompetitionSetupFinanceServiceImpl.class);

    @Autowired
    private FormInputRepository formInputRepository;


    @Override
    public ServiceResult<Void> save(CompetitionSetupFinanceResource competitionSetupFinanceResource) {
        Long competitionId = competitionSetupFinanceResource.getCompetitionId();
        formInputRepository.findByCompetitionIdAndTypeIn(competitionId, asList(STAFF_COUNT, STAFF_TURNOVER)).forEach(
                formInput -> {
                    formInput.setActive(!competitionSetupFinanceResource.isIncludeGrowthTable());
                    formInputRepository.save(formInput);
                }
        );

        return ServiceResult.serviceSuccess();
    }

}
