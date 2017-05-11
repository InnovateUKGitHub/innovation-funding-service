package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.mapper.EthnicityMapper;
import org.innovateuk.ifs.user.repository.EthnicityRepository;
import org.innovateuk.ifs.user.resource.EthnicityResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Transactional and secured service providing operations around {@link org.innovateuk.ifs.user.domain.Ethnicity} data.
 */
@Service
@Transactional(readOnly = true)

public class EthnicityServiceImpl implements EthnicityService{
    @Autowired
    private EthnicityRepository ethnicityRepository;

    @Autowired
    private EthnicityMapper ethnicityMapper;

    @Override
    public ServiceResult<List<EthnicityResource>> findAllActive() {
        return serviceSuccess(simpleMap(ethnicityRepository.findByActiveTrueOrderByPriorityAsc(), ethnicityMapper::mapToResource));
    }
}
