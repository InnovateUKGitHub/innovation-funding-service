package com.worth.ifs.workflow.repository;

import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.domain.ActivityType;
import com.worth.ifs.workflow.domain.State;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * A repository handling Activity State retrieval
 */
public interface ActivityStateRepository extends PagingAndSortingRepository<ActivityState, Long> {

    ActivityState findOneByActivityTypeAndState(ActivityType activityType, State state);
}
