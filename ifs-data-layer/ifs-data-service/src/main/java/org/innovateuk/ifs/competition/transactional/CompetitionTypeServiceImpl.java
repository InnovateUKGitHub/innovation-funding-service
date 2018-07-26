package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.mapper.CompetitionTypeMapper;
import org.innovateuk.ifs.competition.repository.CompetitionTypeRepository;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class CompetitionTypeServiceImpl implements CompetitionTypeService {

    @Autowired
    private CompetitionTypeMapper competitionTypeMapper;

    @Autowired
    private CompetitionTypeRepository competitionTypeRepository;

    @Override
    public ServiceResult<List<CompetitionTypeResource>> findAllTypes() {
        return serviceSuccess((List) competitionTypeMapper.mapToResource(competitionTypeRepository.findAll()));
    }
}
