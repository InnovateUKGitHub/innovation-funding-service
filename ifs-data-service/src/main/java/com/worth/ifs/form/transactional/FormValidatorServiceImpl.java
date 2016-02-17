package com.worth.ifs.form.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.form.domain.FormValidator;
import com.worth.ifs.form.mapper.FormValidatorMapper;
import com.worth.ifs.form.repository.FormValidatorRepository;
import com.worth.ifs.form.resource.FormValidatorResource;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

@Service
public class FormValidatorServiceImpl extends BaseTransactionalService implements FormValidatorService {

    @Autowired
    private FormValidatorRepository repository;

    @Autowired
    private FormValidatorMapper mapper;

    @Override
    public ServiceResult<FormValidatorResource> findOne(Long id) {
        return find(repository.findOne(id), notFoundError(FormValidator.class)).andOnSuccessReturn(mapper::mapFormValidatorToResource);
    }
}