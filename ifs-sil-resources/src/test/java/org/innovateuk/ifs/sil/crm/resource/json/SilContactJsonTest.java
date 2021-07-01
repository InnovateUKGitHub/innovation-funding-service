package org.innovateuk.ifs.sil.crm.resource.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.sil.crm.resource.SilAddress;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.innovateuk.ifs.sil.crm.resource.SilOrganisation;
import org.junit.jupiter.api.Test;


import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

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
        silContact.setIfsUuid("0ba486fa-8deb-466f-9331-7cbcfb864dd8");
        silContact.setIfsAppID("657");
        silContact.setExperienceType("LOANS");


        String json = new ObjectMapper().writeValueAsString(silContact);
        assertAll(
                () -> assertThat(json, containsString("\"title\":\"title\"")),
                () -> assertThat(json, containsString("\"firstName\":\"first name\"")),
                () -> assertThat(json, containsString("\"lastName\":\"last name\"")),
                () -> assertThat(json, containsString("\"ifsUuid\":\"0ba486fa-8deb-466f-9331-7cbcfb864dd8\"")),
                () -> assertThat(json, containsString("\"ifsAppID\":\"657\"")),
                () -> assertThat(json, containsString("\"experienceType\":\"LOANS\"")),
                () -> assertThat(json, containsString("\"email\":\"email\"")),
                () -> assertThat(json, containsString("\"Address\":" +
                        "{\"buildingName\":\"building name\"" +
                        ",\"street\":\"street\"," +
                        "\"locality\":\"locality\"" +
                        ",\"town\":\"town\"" +
                        ",\"postcode\":\"postcode\"" +
                        ",\"country\":\"country\"}")),
                () -> assertThat(json, containsString("\"organisation\":" +
                        "{\"name\":\"name\"" +
                        ",\"registrationNumber\":\"registration number\"," +
                        "\"registeredAddress\":" +
                        "{\"buildingName\":\"building name\"" +
                        ",\"street\":\"street\"" +
                        ",\"locality\":\"locality\"" +
                        ",\"town\":\"town\"" +
                        ",\"postcode\":\"postcode\"" +
                        ",\"country\":\"country\"}" +
                        ",\"srcSysOrgId\":\"sys org id\"}")),
                () -> assertThat(json, containsString("\"srcSysContactId\":\"sys contact id\"")),
                () -> assertThat(json, containsString("\"sourceSystem\":\"IFS\""))

        );


        SilContact out = new ObjectMapper().readValue(json, SilContact.class);
        assertThat(out.toString(), is("SilContact(ifsUuid=0ba486fa-8deb-466f-9331-7cbcfb864dd8, experienceType=LOANS, ifsAppID=657, email=email, lastName=last name, firstName=first name, title=title, jobTitle=job title, address=SilAddress(buildingName=building name, street=street, locality=locality, town=town, postcode=postcode, country=country), organisation=SilOrganisation(name=name, registrationNumber=registration number, registeredAddress=SilAddress(buildingName=building name, street=street, locality=locality, town=town, postcode=postcode, country=country), srcSysOrgId=sys org id), sourceSystem=IFS, srcSysContactId=sys contact id)"));
    }
}
