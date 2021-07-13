package org.innovateuk.ifs.sil.crm.resource.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.sil.crm.resource.SilAddress;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.innovateuk.ifs.sil.crm.resource.SilOrganisation;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SilContactJsonTest {
    private static final String crmJsonPayload = "{\"ifsUuid\":null," +
            "\"experienceType\":null," +
            "\"ifsAppID\":null," +
            "\"email\":\"email\"," +
            "\"lastName\":\"last name\"," +
            "\"firstName\":\"first name\"," +
            "\"title\":\"title\"," +
            "\"jobTitle\":\"job title\"," +
            "\"organisation\":{" +
            "\"name\":\"name\"," +
            "\"registrationNumber\":\"registration number\"," +
            "\"registeredAddress\":{" +
            "\"buildingName\":\"building name\"," +
            "\"street\":\"street\"," +
            "\"locality\":\"locality\"," +
            "\"town\":\"town\"," +
            "\"postcode\":\"postcode\"," +
            "\"country\":\"country\"" +
            "}," +
            "\"srcSysOrgId\":\"sys org id\"}," +
            "\"sourceSystem\":\"IFS\"," +
            "\"srcSysContactId\":\"sys contact id\"," +
            "\"Address\":{" +
            "\"buildingName\":\"building name\"," +
            "\"street\":\"street\"," +
            "\"locality\":\"locality\"," +
            "\"town\":\"town\"," +
            "\"postcode\":\"postcode\"," +
            "\"country\":\"country\"}" +
            "}";
    private static final String contactToString = "SilContact(ifsUuid=null, experienceType=null, ifsAppID=null, email=email, lastName=last name, firstName=first name, title=title, jobTitle=job title, address=SilAddress(buildingName=building name, street=street, locality=locality, town=town, postcode=postcode, country=country), organisation=SilOrganisation(name=name, registrationNumber=registration number, registeredAddress=SilAddress(buildingName=building name, street=street, locality=locality, town=town, postcode=postcode, country=country), srcSysOrgId=sys org id), sourceSystem=IFS, srcSysContactId=sys contact id)";

    @Test
    public void shouldSerialize() throws IOException {
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

        String json = new ObjectMapper().writeValueAsString(silContact);
        assertThat(json, equalTo(crmJsonPayload));
    }

    @Test
    public void shouldDeserialize() throws IOException {

        SilContact out = new ObjectMapper().readValue(crmJsonPayload, SilContact.class);
        assertThat(out.toString(), equalTo(contactToString));

    }
}
