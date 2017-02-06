package org.innovateuk.ifs.threads.security;

import org.innovateuk.ifs.alert.mapper.AlertMapper;
import org.innovateuk.ifs.alert.repository.AlertRepository;
import org.innovateuk.ifs.alert.resource.AlertResource;
import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.threads.domain.Thread;
import org.innovateuk.ifs.threads.repository.ThreadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class ThreadLookupStrategy<T extends Thread, R, M extends BaseMapper<T, R, Long>> {

    @Autowired
    private ThreadRepository<T> threadRepository;

    @Autowired
    private M threadMapper;

    @PermissionEntityLookupStrategy
    public R findById(final Long id) {
        return threadMapper.mapToResource(threadRepository.findOne(id));
    }

}