package com.worth.ifs.finance.transactional;

import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.security.NotSecured;

import java.util.List;

public interface CostFieldService {
    @NotSecured("TODO")
    CostField findOne(Long id);
    @NotSecured("TODO")
    List<CostField> findAll();
}