package com.worth.ifs.workflow.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.repository.ActivityStateRepository;
import com.worth.ifs.workflow.resource.ActivityStateResource;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;


@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
    }
)
public abstract class ActivityStateMapper extends BaseMapper<ActivityState, ActivityStateResource, Long> {

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Override
    public ActivityStateResource mapToResource(ActivityState object) {
        if (object == null) {
            return null;
        }
        return new ActivityStateResource(object.getActivityType(), object.getState());
    }

    @Override
    public ActivityState mapToDomain(ActivityStateResource resource) {
        return activityStateRepository.findOneByActivityTypeAndState(resource.getActivityType(), resource.getState());
    }
}