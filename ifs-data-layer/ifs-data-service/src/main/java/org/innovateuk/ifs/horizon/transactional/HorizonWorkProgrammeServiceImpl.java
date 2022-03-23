package org.innovateuk.ifs.horizon.transactional;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.horizon.domain.ApplicationHorizonWorkProgramme;
import org.innovateuk.ifs.horizon.mapper.HorizonWorkProgrammeMapper;
import org.innovateuk.ifs.horizon.repository.HorizonWorkProgrammeRepository;
import org.innovateuk.ifs.horizon.resource.ApplicationHorizonWorkProgrammeResource;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgramme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class HorizonWorkProgrammeServiceImpl implements HorizonWorkProgrammeService {

    @Autowired
    private HorizonWorkProgrammeRepository horizonWorkProgrammeRepository;

    @Autowired
    private HorizonWorkProgrammeMapper horizonWorkProgrammeMapper;

    @Override
    @Transactional
    public ServiceResult<Void> updateWorkProgrammesForApplication(List<HorizonWorkProgramme> programmes, long applicationId) {
        horizonWorkProgrammeRepository.deleteAllByApplicationId(applicationId);
        List<ApplicationHorizonWorkProgramme> toAdd = new ArrayList<>();
        for (HorizonWorkProgramme programme : programmes) {
            toAdd.add(horizonWorkProgrammeMapper.mapIdAndWorkProgrammeToDomain(applicationId, programme));
        }
        horizonWorkProgrammeRepository.saveAll(toAdd);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<List<ApplicationHorizonWorkProgrammeResource>> findSelectedForApplication(long applicationId) {
        List<ApplicationHorizonWorkProgramme> domain = horizonWorkProgrammeRepository
                .findByApplicationId(applicationId);
        return serviceSuccess(Lists.newArrayList(horizonWorkProgrammeMapper.mapToResource(domain)));
    }
}
