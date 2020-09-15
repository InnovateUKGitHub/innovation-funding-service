package org.innovateuk.ifs.finance.mapper;

import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.finance.domain.KtpFinancialYears;
import org.innovateuk.ifs.finance.resource.KtpYearsResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                KtpYearMapper.class
        }
)
public abstract class KtpYearsMapper extends BaseResourceMapper<KtpFinancialYears, KtpYearsResource> {
}
