package com.worth.ifs.form.transactional;

import com.worth.ifs.form.domain.FormInputType;
import com.worth.ifs.form.repository.FormInputTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormInputTypeServiceImpl implements FormInputTypeService {
    @Autowired
    private FormInputTypeRepository repository;

    @Override
    public FormInputType findOne(Long id) {
        return repository.findOne(id);
    }
}