package com.worth.ifs.finance.controller;

import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.repository.CostFieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * This RestController exposes CRUD operations to both the
 * {@link com.worth.ifs.finance.service.CostFieldRestServiceImpl} and other REST-API users
 * to manage {@link CostField} related data.
 */
@RestController
@RequestMapping("/costfield")
public class CostFieldController {
    @Autowired
    CostFieldRepository costFieldRepository;

    @RequestMapping("/findAll/")
    public List<CostField> findAll() {
        List<CostField> costFields = costFieldRepository.findAll();
        return costFields;
    }
}
