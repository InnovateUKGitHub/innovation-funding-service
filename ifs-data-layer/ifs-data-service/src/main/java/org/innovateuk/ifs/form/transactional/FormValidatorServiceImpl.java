package org.innovateuk.ifs.form.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.domain.FormValidator;
import org.innovateuk.ifs.form.mapper.FormValidatorMapper;
import org.innovateuk.ifs.form.repository.FormValidatorRepository;
import org.innovateuk.ifs.form.resource.FormValidatorResource;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class FormValidatorServiceImpl extends BaseTransactionalService implements FormValidatorService {

    @Autowired
    private FormValidatorRepository repository;

    @Autowired
    private FormValidatorMapper mapper;

    @Override
    public ServiceResult<FormValidatorResource> findOne(Long id) {
        return find(repository.findOne(id), notFoundError(FormValidator.class)).andOnSuccessReturn(mapper::mapToResource);
    }
}
