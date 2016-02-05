package com.worth.ifs.finance.transactional;

import com.worth.ifs.finance.domain.CostValue;
import com.worth.ifs.finance.domain.CostValueId;
import com.worth.ifs.finance.repository.CostValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CostValueServiceImpl implements CostValueService {
    @Autowired
    private CostValueRepository repository;

    @Override
    public CostValue findOne(CostValueId id) {
        return repository.findOne(id);
    }
}