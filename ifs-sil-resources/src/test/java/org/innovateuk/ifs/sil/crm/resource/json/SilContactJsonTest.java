package org.innovateuk.ifs.sil.crm.resource.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.sil.crm.resource.SilAddress;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.innovateuk.ifs.sil.crm.resource.SilOrganisation;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class SilContactJsonTest {

    private static final LocalDate DATE = LocalDate.of(2020, 12, 10);

    @Test
    public void shouldSerializeAndDeserialize() throws IOException {
        SilAddress address = new SilAddress();
        address.setBuildingName("building name");
        address.setStreet("street");
        address.setLocality("locality");
        address.setTown("town");
        address.setCountry("country");
        address.setPostcode("postcode");

        SilOrganisation organisation = new SilOrganisation();
        organisation.setDateOfIncorporation(DATE);
        organisation.setExecutiveOfficers(Arrays.asList("officer 1", "officer 2"));
        organisation.setSicCodes(Arrays.asList("code 1", "code 2"));
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
        assertThat(json, containsString("\"title\":\"title\""));
        assertThat(json, containsString("\"firstName\":\"first name\""));
        assertThat(json, containsString("\"lastName\":\"last name\""));
        assertThat(json, containsString("\"email\":\"email\""));
        assertThat(json, containsString("\"Address\":" +
                "{\"buildingName\":\"building name\"" +
                ",\"street\":\"street\"," +
                "\"locality\":\"locality\"" +
                ",\"town\":\"town\"" +
                ",\"postcode\":\"postcode\"" +
                ",\"country\":\"country\"}"));
        assertThat(json, containsString("\"jobTitle\":\"job title\""));
        assertThat(json, containsString("\"organisation\":" +
                "{\"name\":\"name\"" +
                ",\"registrationNumber\":\"registration number\"," +
                "\"registeredAddress\":" +
                "{\"buildingName\":\"building name\"" +
                ",\"street\":\"street\"" +
                ",\"locality\":\"locality\"" +
                ",\"town\":\"town\"" +
                ",\"postcode\":\"postcode\"" +
                ",\"country\":\"country\"}" +
                ",\"srcSysOrgId\":\"sys org id\"" +
                ",\"dateOfIncorporation\":\"10/12/2020\"" +
                ",\"sicCodes\":[\"code 1\",\"code 2\"]" +
                ",\"executiveOfficers\":[\"officer 1\",\"officer 2\"]}"));
        assertThat(json, containsString("\"srcSysContactId\":\"sys contact id\""));
        assertThat(json, containsString("\"sourceSystem\":\"IFS\""));

        SilContact out = new ObjectMapper().readValue(json, SilContact.class);
        assertThat(out.getOrganisation().getDateOfIncorporation().getYear(), equalTo(2020));
        assertThat(out.getOrganisation().getDateOfIncorporation().getMonthValue(), equalTo(12));
        assertThat(out.getOrganisation().getDateOfIncorporation().getDayOfMonth(), equalTo(10));
    }
}
