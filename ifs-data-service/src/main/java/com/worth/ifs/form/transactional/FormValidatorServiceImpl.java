package com.worth.ifs.form.transactional;

import com.worth.ifs.form.domain.FormValidator;
import com.worth.ifs.form.repository.FormValidatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormValidatorServiceImpl implements FormValidatorService {
    @Autowired
    private FormValidatorRepository repository;

    @Override
    public FormValidator findOne(Long id) {
        return repository.findOne(id);
    }
}