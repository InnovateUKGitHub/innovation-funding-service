package com.worth.ifs.bankdetails.mapper;

import com.worth.ifs.address.mapper.AddressMapper;
import com.worth.ifs.bankdetails.domain.BankDetails;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.project.mapper.ProjectMapper;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                ProjectMapper.class,
                AddressMapper.class
        }
)
public abstract class BankDetailsMapper extends BaseMapper<BankDetails, BankDetailsResource, Long> {
        public Long mapBankDetailsToId(BankDetails object) {
                if (object == null) {
                        return null;
                }
                return object.getId();
        }
}
