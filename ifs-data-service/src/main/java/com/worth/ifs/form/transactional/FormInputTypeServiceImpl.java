package com.worth.ifs.form.transactional;

import com.worth.ifs.form.domain.FormInputType;
import com.worth.ifs.form.repository.FormInputTypeRepository;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormInputTypeServiceImpl extends BaseTransactionalService implements FormInputTypeService {

    @Autowired
    private FormInputTypeRepository repository;

    @Override
    public FormInputType findByTitle(String title) {
        return repository.findByTitle(title);
    }
}