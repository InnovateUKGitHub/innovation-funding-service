package org.innovateuk.ifs.horizon.transactional;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.horizon.domain.ApplicationHorizonWorkProgramme;
import org.innovateuk.ifs.horizon.domain.HorizonWorkProgramme;
import org.innovateuk.ifs.horizon.mapper.ApplicationHorizonWorkProgrammeMapper;
import org.innovateuk.ifs.horizon.mapper.HorizonWorkProgrammeMapper;
import org.innovateuk.ifs.horizon.repository.ApplicationHorizonWorkProgrammeRepository;
import org.innovateuk.ifs.horizon.repository.HorizonWorkProgrammeRepository;
import org.innovateuk.ifs.horizon.resource.ApplicationHorizonWorkProgrammeResource;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgrammeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class HorizonWorkProgrammeServiceImpl implements HorizonWorkProgrammeService {

    @Autowired
    private HorizonWorkProgrammeRepository horizonWorkProgrammeRepository;

    @Autowired
    private HorizonWorkProgrammeMapper horizonWorkProgrammeMapper;

    @Autowired
    private ApplicationHorizonWorkProgrammeRepository applicationHorizonWorkProgrammeRepository;

    @Autowired
    private ApplicationHorizonWorkProgrammeMapper applicationHorizonWorkProgrammeMapper;

    @Override
    public ServiceResult<HorizonWorkProgrammeResource> findById(Long workProgrammeId) {
        Optional<HorizonWorkProgramme> domain = horizonWorkProgrammeRepository.findById(workProgrammeId);
        return serviceSuccess(horizonWorkProgrammeMapper.mapToResource(domain.get()));
    }

    @Override
    public ServiceResult<List<HorizonWorkProgrammeResource>> findRootWorkProgrammes() {
       List<HorizonWorkProgramme> domain = horizonWorkProgrammeRepository.findRootWorkPorgrammes();
       return serviceSuccess(Lists.newArrayList(horizonWorkProgrammeMapper.mapToResource(domain)));
    }

    @Override
    public ServiceResult<List<HorizonWorkProgrammeResource>> findChildrenWorkProgrammes(Long workProgrammeId) {
        List<HorizonWorkProgramme> domain = horizonWorkProgrammeRepository.findByParentId(workProgrammeId);
        return serviceSuccess(Lists.newArrayList(horizonWorkProgrammeMapper.mapToResource(domain)));
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateWorkProgrammesForApplication(List<HorizonWorkProgrammeResource> programmes, Long applicationId) {
        applicationHorizonWorkProgrammeRepository.deleteAllByApplicationId(applicationId);
        List<ApplicationHorizonWorkProgramme> toAdd = new ArrayList<>();
        for (HorizonWorkProgrammeResource programme : programmes) {
            toAdd.add(applicationHorizonWorkProgrammeMapper.mapIdAndWorkProgrammeToDomain(applicationId, horizonWorkProgrammeMapper.mapToDomain(programme)));
        }
        applicationHorizonWorkProgrammeRepository.saveAll(toAdd);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<List<ApplicationHorizonWorkProgrammeResource>> findSelectedForApplication(Long applicationId) {
        List<ApplicationHorizonWorkProgramme> domain = applicationHorizonWorkProgrammeRepository
                .findByApplicationId(applicationId);
        return serviceSuccess(Lists.newArrayList(applicationHorizonWorkProgrammeMapper.mapToResource(domain)));
    }
}
