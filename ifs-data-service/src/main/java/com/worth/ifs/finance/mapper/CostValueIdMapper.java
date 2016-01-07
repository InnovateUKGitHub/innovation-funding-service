package com.worth.ifs.finance.mapper;

import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.finance.domain.CostField;
import com.worth.ifs.finance.repository.CostFieldRepository;
import com.worth.ifs.finance.repository.CostRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(
    config = GlobalMapperConfig.class
)
public abstract class CostValueIdMapper {

    @Autowired
    private CostRepository costRepository;

    @Autowired
    private CostFieldRepository costFieldRepository;


    public CostField mapIdToCostField(Long id) {
        return costFieldRepository.findOne(id);
    }


}