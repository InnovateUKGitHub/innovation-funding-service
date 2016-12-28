package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.mapper.MilestoneMapper;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.repository.MilestoneRepository;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static java.util.Comparator.comparing;

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
    public ServiceResult<List<MilestoneResource>> getAllMilestonesByCompetitionId(Long id) {
        return serviceSuccess ((List<MilestoneResource>) milestoneMapper.mapToResource(milestoneRepository.findAllByCompetitionId(id)));
    }

    @Override
    public ServiceResult<MilestoneResource> getMilestoneByTypeAndCompetitionId(MilestoneType type, Long id) {
        return serviceSuccess(milestoneMapper.mapToResource(milestoneRepository.findByTypeAndCompetitionId(type, id)));
    }

    @Override
    public ServiceResult<Void> updateMilestones(List<MilestoneResource> milestones) {
    	ValidationMessages messages = validate(milestones);
        
        if (!messages.hasErrors()) {
            List<Milestone> milestoneEntities = milestones.stream().map(milestoneMapper::mapToDomain).collect(Collectors.toList());
            milestoneEntities.forEach(m -> milestoneRepository.save(m));
            return serviceSuccess();
        }

        return serviceFailure(messages.getErrors());
    }

    @Override
    public ServiceResult<Void> updateMilestone(MilestoneResource milestoneResource) {
        milestoneRepository.save(milestoneMapper.mapToDomain(milestoneResource));
        return serviceSuccess();
    }

    @Override
    public ServiceResult<MilestoneResource> create(MilestoneType type, Long id) {
        Competition competition = competitionRepository.findById(id);

        // TODO INFUND-6256 remove public default constructor for Milestone
        Milestone milestone = new Milestone();
        milestone.setType(type);
        milestone.setCompetition(competition);
        return serviceSuccess(milestoneMapper.mapToResource(milestoneRepository.save(milestone)));
    }

    private ValidationMessages validate(List<MilestoneResource> milestones) {
        ValidationMessages vm = new ValidationMessages();

        milestones.sort(comparing(MilestoneResource::getType));

        milestones.forEach(m -> {
        	if(m.getDate() == null) {
        		Error error = new Error("error.milestone.nulldate", HttpStatus.BAD_REQUEST);
        		vm.addError(error);
        	} else if(m.getDate().isBefore(LocalDateTime.now())) {
        		Error error = new Error("error.milestone.pastdate", HttpStatus.BAD_REQUEST);
        		vm.addError(error);
        	}
        });

        // preset milestones must be in the correct order
        List<MilestoneResource> presetMilestones = simpleFilter(milestones, milestoneResource -> milestoneResource.getType().isPresetDate());

        for (int i = 1; i < presetMilestones.size(); i++) {
        	MilestoneResource previous = presetMilestones.get(i - 1);
        	MilestoneResource current = presetMilestones.get(i);
        	
        	if(current.getDate() != null && previous.getDate() != null) {
        		if(previous.getDate().isAfter(current.getDate())) {
        			Error error = new Error("error.milestone.nonsequential", HttpStatus.BAD_REQUEST);
            		vm.addError(error);
        		}
        	}
        }
        return vm;
    }
}
