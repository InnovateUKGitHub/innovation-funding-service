package com.worth.ifs.controller;

import com.worth.ifs.domain.CostField;
import com.worth.ifs.repository.CostFieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
