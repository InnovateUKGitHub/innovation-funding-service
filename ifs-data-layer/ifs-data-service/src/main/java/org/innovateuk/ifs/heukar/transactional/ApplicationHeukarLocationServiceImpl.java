package org.innovateuk.ifs.heukar.transactional;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.heukar.domain.ApplicationHeukarLocation;
import org.innovateuk.ifs.heukar.mapper.ApplicationHeukarLocationMapper;
import org.innovateuk.ifs.heukar.repository.ApplicationHeukarLocationRepository;
import org.innovateuk.ifs.heukar.resource.ApplicationHeukarLocationResource;
import org.innovateuk.ifs.heukar.resource.HeukarLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class ApplicationHeukarLocationServiceImpl implements ApplicationHeukarLocationService {

    @Autowired
    private ApplicationHeukarLocationRepository applicationHeukarLocationRepository;

    @Autowired
    private ApplicationHeukarLocationMapper applicationHeukarLocationMapper;

    @Override
    @Transactional
    public ServiceResult<Void> updateLocationsForApplication(List<HeukarLocation> locations, long applicationId) {
        applicationHeukarLocationRepository.deleteAllByApplicationId(applicationId);
        List<ApplicationHeukarLocation> toAdd = new ArrayList<>();
        for (HeukarLocation location : locations) {
            toAdd.add(applicationHeukarLocationMapper.mapIdAndLocationToDomain(applicationId, location));
        }
        applicationHeukarLocationRepository.saveAll(toAdd);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<List<ApplicationHeukarLocationResource>> findSelectedForApplication(long applicationId) {
        List<ApplicationHeukarLocation> domain = applicationHeukarLocationRepository
                .findByApplicationId(applicationId);
        return serviceSuccess(Lists.newArrayList(applicationHeukarLocationMapper.mapToResource(domain)));
    }

}
