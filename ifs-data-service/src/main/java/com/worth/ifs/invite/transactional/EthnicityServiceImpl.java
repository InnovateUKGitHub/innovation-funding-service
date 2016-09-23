package com.worth.ifs.invite.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.mapper.EthnicityMapper;
import com.worth.ifs.user.repository.EthnicityRepository;
import com.worth.ifs.user.resource.EthnicityResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * Transactional and secured service providing operations around {@link com.worth.ifs.user.domain.Ethnicity} data.
 */
@Service
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
