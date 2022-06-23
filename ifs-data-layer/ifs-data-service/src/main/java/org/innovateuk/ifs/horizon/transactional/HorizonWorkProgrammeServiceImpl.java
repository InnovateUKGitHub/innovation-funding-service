package org.innovateuk.ifs.horizon.transactional;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.horizon.domain.ApplicationHorizonWorkProgramme;
import org.innovateuk.ifs.horizon.domain.CompetitionHorizonWorkProgramme;
import org.innovateuk.ifs.horizon.domain.HorizonWorkProgramme;
import org.innovateuk.ifs.horizon.mapper.ApplicationHorizonWorkProgrammeMapper;
import org.innovateuk.ifs.horizon.mapper.CompetitionHorizonWorkProgrammeMapper;
import org.innovateuk.ifs.horizon.mapper.HorizonWorkProgrammeMapper;
import org.innovateuk.ifs.horizon.repository.ApplicationHorizonWorkProgrammeRepository;
import org.innovateuk.ifs.horizon.repository.CompetitionHorizonWorkProgrammeRepository;
import org.innovateuk.ifs.horizon.repository.HorizonWorkProgrammeRepository;
import org.innovateuk.ifs.horizon.resource.ApplicationHorizonWorkProgrammeResource;
import org.innovateuk.ifs.horizon.resource.CompetitionHorizonWorkProgrammeResource;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgrammeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class HorizonWorkProgrammeServiceImpl implements HorizonWorkProgrammeService {

    @Autowired
    private HorizonWorkProgrammeRepository horizonWorkProgrammeRepository;
    @Autowired
    private HorizonWorkProgrammeMapper horizonWorkProgrammeMapper;

    @Autowired
    private CompetitionHorizonWorkProgrammeRepository competitionHorizonWorkProgrammeRepository;
    @Autowired
    private CompetitionHorizonWorkProgrammeMapper competitionHorizonWorkProgrammeMapper;

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
    public ServiceResult<List<HorizonWorkProgrammeResource>> findWorkProgrammesByCompetition(Long competitionId) {
        List<CompetitionHorizonWorkProgramme> domain = competitionHorizonWorkProgrammeRepository.findByCompetitionId(competitionId);
        return serviceSuccess(Lists.newArrayList(competitionHorizonWorkProgrammeMapper.mapToResource(domain)).stream()
                .map(CompetitionHorizonWorkProgrammeResource::getWorkProgramme).collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public ServiceResult<Void> initWorkProgrammesForCompetition(Long competitionId) {
        competitionHorizonWorkProgrammeRepository.deleteAllByCompetitionId(competitionId);
        List<CompetitionHorizonWorkProgramme> toAdd = new ArrayList<>();

        horizonWorkProgrammeRepository.findAll().forEach(programme -> {
            if(programme.isEnabled()) {
                toAdd.add(competitionHorizonWorkProgrammeMapper.mapIdAndWorkProgrammeToDomain(competitionId, programme));
            }
        });

        if(toAdd.size() > 0) {
            competitionHorizonWorkProgrammeRepository.saveAll(toAdd);
        }
        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> deleteWorkProgrammesForCompetition(Long competitionId) {
        competitionHorizonWorkProgrammeRepository.deleteAllByCompetitionId(competitionId);
        return serviceSuccess();
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateWorkProgrammesForApplication(List<Long> programmeIds, Long applicationId) {
        applicationHorizonWorkProgrammeRepository.deleteAllByApplicationId(applicationId);
        List<ApplicationHorizonWorkProgramme> toAdd = new ArrayList<>();
        for (Long programmeId : programmeIds) {
            HorizonWorkProgramme programme = horizonWorkProgrammeRepository.findById(programmeId).get();
            toAdd.add(applicationHorizonWorkProgrammeMapper.mapIdAndWorkProgrammeToDomain(applicationId, programme));
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
