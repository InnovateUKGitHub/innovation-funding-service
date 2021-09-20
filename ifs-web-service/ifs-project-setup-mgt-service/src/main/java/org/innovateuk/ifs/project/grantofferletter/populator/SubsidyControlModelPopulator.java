package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.SubsidyControlModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SubsidyControlModelPopulator {

    public SubsidyControlModel populate(Map<OrganisationResource, ProjectFinanceResource> financesForOrgs) {

        List<String> organisationNames = financesForOrgs.keySet().stream()
                .map(OrganisationResource::getName)
                .sorted()
                .collect(Collectors.toList());
        List<String> researchOrganisationNames = financesForOrgs.keySet().stream()
                .filter(organisation -> organisation.getOrganisationTypeEnum() == OrganisationTypeEnum.RESEARCH)
                .map(OrganisationResource::getName)
                .sorted()
                .collect(Collectors.toList());
        List<String> organisationNamesWithinNI = financesForOrgs.entrySet().stream()
                .filter(entry -> Boolean.TRUE.equals(entry.getValue().getNorthernIrelandDeclaration()))
                .map(entry -> entry.getKey().getName())
                .sorted()
                .collect(Collectors.toList());
        List<String> researchOrganisationNamesWithinNI = financesForOrgs.entrySet().stream()
                .filter(entry -> entry.getKey().getOrganisationTypeEnum() == OrganisationTypeEnum.RESEARCH &&
                        Boolean.TRUE.equals(entry.getValue().getNorthernIrelandDeclaration()))
                .map(entry -> entry.getKey().getName())
                .sorted()
                .collect(Collectors.toList());

        return new SubsidyControlModel(organisationNames,
                researchOrganisationNames,
                organisationNamesWithinNI,
                researchOrganisationNamesWithinNI);
    }
    
}
