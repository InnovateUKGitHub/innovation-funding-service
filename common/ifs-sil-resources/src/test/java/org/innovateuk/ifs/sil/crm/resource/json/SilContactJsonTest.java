package org.innovateuk.ifs.sil.crm.resource.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.sil.crm.resource.SilAddress;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.innovateuk.ifs.sil.crm.resource.SilOrganisation;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.text.IsEqualCompressingWhiteSpace.equalToCompressingWhiteSpace;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SilContactJsonTest {

    private static final String contactToString = "SilContact(ifsUuid=null, experienceType=null, ifsAppID=null, email=email, lastName=last name, firstName=first name, title=title, jobTitle=job title, address=SilAddress(buildingName=building name, street=street, locality=locality, town=town, postcode=postcode, country=country), organisation=SilOrganisation(name=name, registrationNumber=registration number, registeredAddress=SilAddress(buildingName=building name, street=street, locality=locality, town=town, postcode=postcode, country=country), srcSysOrgId=sys org id), sourceSystem=IFS, srcSysContactId=sys contact id)";
    String baseCrmPayloadFromFile;
    String extraCrmPayloadFromFile;

    @Before
    public void setup() throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        File base = new File(classLoader.getResource("payloads/CRMPayload_BaseAttributes.json").getFile());
        baseCrmPayloadFromFile = new String(Files.readAllBytes(base.toPath()));
        File extra = new File(classLoader.getResource("payloads/CRMPayload_ExtraAttributes.json").getFile());
        extraCrmPayloadFromFile = new String(Files.readAllBytes(extra.toPath()));
    }


    @Test
    public void shouldSerializeWithBaseAttributes() throws IOException {
        SilAddress address = new SilAddress();
        address.setBuildingName("building name");
        address.setStreet("street");
        address.setLocality("locality");
        address.setTown("town");
        address.setCountry("country");
        address.setPostcode("postcode");

        SilOrganisation organisation = new SilOrganisation();
        organisation.setName("name");
        organisation.setRegisteredAddress(address);
        organisation.setRegistrationNumber("registration number");
        organisation.setSrcSysOrgId("sys org id");

        SilContact silContact = new SilContact();
        silContact.setTitle("title");
        silContact.setFirstName("first name");
        silContact.setLastName("last name");
        silContact.setEmail("email");
        silContact.setAddress(address);
        silContact.setJobTitle("job title");
        silContact.setOrganisation(organisation);
        silContact.setSrcSysContactId("sys contact id");

        String crmDataGenerated = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(silContact);
        assertThat("Generated and Read data should be same", equalToCompressingWhiteSpace(crmDataGenerated).matches(baseCrmPayloadFromFile));


    }

    @Test
    public void shouldDeserializeWithBaseAttributes() throws IOException {

        SilContact out = new ObjectMapper().readValue(baseCrmPayloadFromFile, SilContact.class);
        assertThat(out.toString(), equalTo(contactToString));

    }

    @Test
    public void shouldSerializeWithExtraAttributes() throws IOException {
        SilAddress address = new SilAddress();
        address.setBuildingName("building name");
        address.setStreet("street");
        address.setLocality("locality");
        address.setTown("town");
        address.setCountry("country");
        address.setPostcode("postcode");

        SilOrganisation organisation = new SilOrganisation();
        organisation.setName("name");
        organisation.setRegisteredAddress(address);
        organisation.setRegistrationNumber("registration number");
        organisation.setSrcSysOrgId("sys org id");

        SilContact silContact = new SilContact();
        silContact.setTitle("title");
        silContact.setFirstName("first name");
        silContact.setLastName("last name");
        silContact.setEmail("email");
        silContact.setAddress(address);
        silContact.setJobTitle("job title");
        silContact.setOrganisation(organisation);
        silContact.setSrcSysContactId("sys contact id");
        silContact.setIfsAppID("1");
        silContact.setExperienceType("LOAN");
        silContact.setIfsUuid("d39f211d-a1aa-4973-bd0d-9a746851559e");

        String crmDataGenerated = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(silContact);
        assertThat("Generated and Read data should be same", equalToCompressingWhiteSpace(crmDataGenerated).matches(extraCrmPayloadFromFile));
    }

    @Test
    public void shouldDeserializeWithExtraAttributes() throws IOException {

        SilContact out = new ObjectMapper().readValue(baseCrmPayloadFromFile, SilContact.class);
        assertThat(out.toString(), equalTo(contactToString));

    }


}
