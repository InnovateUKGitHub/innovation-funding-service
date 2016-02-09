package com.worth.ifs.finance.transactional;

import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.repository.CostFieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CostFieldServiceImpl implements CostFieldService {
    @Autowired
    private CostFieldRepository repository;

    @Override
    public CostField findOne(Long id) {
        return repository.findOne(id);
    }

    @Override
    public List<CostField> findAll() {
        return repository.findAll();
    }
}