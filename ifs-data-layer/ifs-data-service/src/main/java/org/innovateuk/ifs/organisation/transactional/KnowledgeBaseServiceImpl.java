package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.knowledgebase.resourse.KnowledgeBaseResource;
import org.innovateuk.ifs.organisation.domain.KnowledgeBase;
import org.innovateuk.ifs.organisation.mapper.KnowledgeBaseMapper;
import org.innovateuk.ifs.organisation.repository.KnowledgeBaseRepository;
import org.innovateuk.ifs.transactional.RootTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class KnowledgeBaseServiceImpl extends RootTransactionalService implements KnowledgeBaseService {

    @Autowired
    private KnowledgeBaseRepository knowledgeBaseRepository;

    @Autowired
    private KnowledgeBaseMapper knowledgeBaseMapper;

    @Override
    public ServiceResult<List<String>> getKnowledgeBaseNames() {
        return serviceSuccess(stream(knowledgeBaseRepository.findAll().spliterator(), false)
                .map(KnowledgeBase::getName)
                .collect(toList()));
    }

    @Override
    public ServiceResult<String> getKnowledgeBaseName(long id) {
        return find(knowledgeBaseRepository.findById(id), notFoundError(KnowledgeBase.class, singletonList(id)))
                .andOnSuccessReturn(KnowledgeBase::getName);
    }

    @Override
    public ServiceResult<KnowledgeBaseResource> getKnowledgeBaseByName(String name) {
        return find(knowledgeBaseRepository.findByName(name), notFoundError(KnowledgeBase.class, singletonList(name)))
                .andOnSuccessReturn(knowledgeBaseMapper::mapToResource);
    }
}
