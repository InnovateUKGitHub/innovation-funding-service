package com.worth.ifs.competition.transactional;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Milestone;
import com.worth.ifs.competition.mapper.MilestoneMapper;
import com.worth.ifs.competition.repository.MilestoneRepository;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;

/**
 * Service for operations around the usage and processing of Milestones
 */
@Service
public class MilestoneServiceImpl extends BaseTransactionalService implements MilestoneService {

    private static final Log LOG = LogFactory.getLog(MilestoneServiceImpl.class);

    @Autowired
    private MilestoneRepository milestoneRepository;

    @Autowired
    private MilestoneMapper milestoneMapper;



    @Override
    public ServiceResult<List<MilestoneResource>> getAllDatesByCompetitionId(Long id) {
        return serviceSuccess ((List) milestoneMapper.mapToResource(milestoneRepository.findAllByCompetitionId(id)));
    }


    @Override
    public ServiceResult<List<ValidationMessages>> update(Long id, List<MilestoneResource> milestones) {
        List<ValidationMessages> isMilestoneValid =  validate(milestones);
        
        if (isMilestoneValid.isEmpty()) {
            milestones.forEach(m -> {
                Milestone updatedMilestone = milestoneMapper.mapToDomain(m);
                updatedMilestone = milestoneRepository.save(updatedMilestone);
                milestoneMapper.mapToResource(updatedMilestone);
            });
        }
        return serviceSuccess(isMilestoneValid);
    }

    @Override
    public ServiceResult<MilestoneResource> create() {
        Milestone milestone = new Milestone();
        return serviceSuccess(milestoneMapper.mapToResource(milestoneRepository.save(milestone)));
    }

    private List<ValidationMessages> validate(List<MilestoneResource> milestones) {

        ValidationMessages vm = null;
        Error e;
        List<ValidationMessages> vmList = new ArrayList<>();
        for (int i = 1; i < milestones.size() - 1; i++) {

            if (!milestones.get(i).getDate().isAfter(milestones.get(i - 1).getDate())) {
               e = new Error("milestoneTest", "Milestone Error", null, null);
                //e.rejectValue("response.emptyResponse", "Please enter the future date");
                vm.addError((com.worth.ifs.commons.error.Error) e);
                vmList.add(vm);
            }
            Error error;

        }
        return vmList;
    }
}
