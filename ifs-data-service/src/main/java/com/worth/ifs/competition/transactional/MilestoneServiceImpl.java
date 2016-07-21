package com.worth.ifs.competition.transactional;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.domain.Milestone;
import com.worth.ifs.competition.mapper.MilestoneMapper;
import com.worth.ifs.competition.repository.CompetitionRepository;
import com.worth.ifs.competition.repository.MilestoneRepository;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.transactional.BaseTransactionalService;

/**
 * Service for operations around the usage and processing of Milestones
 */
@Service
public class MilestoneServiceImpl extends BaseTransactionalService implements MilestoneService {

    private static final Log LOG = LogFactory.getLog(MilestoneServiceImpl.class);

    @Autowired
    private MilestoneRepository milestoneRepository;
    
    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private MilestoneMapper milestoneMapper;

    @Override
    public ServiceResult<List<MilestoneResource>> getAllDatesByCompetitionId(Long id) {
        return serviceSuccess ((List) milestoneMapper.mapToResource(milestoneRepository.findAllByCompetitionId(id)));
    }

    @Override
    public ServiceResult<ValidationMessages> update(Long id, List<MilestoneResource> milestones) {
        
    	Competition competition = competitionRepository.findById(id);
    	
    	ValidationMessages messages = validate(milestones);
        
        if (!messages.hasErrors()) {
            List<Milestone> milestoneEntities = milestones.stream().map(milestoneMapper::mapToDomain).collect(Collectors.toList());
            competition.setMilestones(milestoneEntities);
        }
        return serviceSuccess(messages);
    }

    @Override
    public ServiceResult<MilestoneResource> create() {
        Milestone milestone = new Milestone();
        return serviceSuccess(milestoneMapper.mapToResource(milestoneRepository.save(milestone)));
    }

    private ValidationMessages validate(List<MilestoneResource> milestones) {

        ValidationMessages vm = new ValidationMessages();

        milestones.sort((c1, c2) -> c1.getName().compareTo(c2.getName()));
        
        milestones.forEach(m -> {
        	if(m.getDate() == null) {
        		Error error = new Error("error.milestone.nulldate", "Date is null", null);
        		vm.addError(error);
        	}
        });
        
        for (int i = 1; i < milestones.size(); i++) {
        	MilestoneResource previous = milestones.get(i - 1);
        	MilestoneResource current = milestones.get(i);
        	
        	if(current.getDate() != null && previous.getDate() != null) {
        		if(previous.getDate().isAfter(current.getDate())) {
        			Error error = new Error("error.milestone.nonsequential", "Dates are not sequential", null);
            		vm.addError(error);
        		}
        	}

        }
        return vm;
    }
}
