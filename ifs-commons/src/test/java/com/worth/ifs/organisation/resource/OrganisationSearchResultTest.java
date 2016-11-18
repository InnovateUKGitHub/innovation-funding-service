package com.worth.ifs.organisation.resource;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.worth.ifs.address.resource.AddressResource;

public class OrganisationSearchResultTest {

	private OrganisationSearchResult result;
	
	@Before
	public void setUp() {
		result = new OrganisationSearchResult();
	}
	
	@Test
	public void testLocation() {
		result.setOrganisationAddress(new AddressResource("line1", "line2", "line3", "town", "county", "postcode"));
		
		String location = result.getLocation();
		
		assertEquals("line1, town, postcode", location);
	}
	
	@Test
	public void testLocationWithNullTown() {
		result.setOrganisationAddress(new AddressResource("line1", "line2", "line3", null, "county", "postcode"));
		
		String location = result.getLocation();
		
		assertEquals("line1, postcode", location);
	}
	
	@Test
	public void testLocationWithNullPostcode() {
		result.setOrganisationAddress(new AddressResource("line1", "line2", "line3", "town", "county", null));
		
		String location = result.getLocation();
		
		assertEquals("line1, town", location);
	}
	
	@Test
	public void testLocationWithNullLine1() {
		result.setOrganisationAddress(new AddressResource(null, "line2", "line3", "town", "county", "postcode"));
		
		String location = result.getLocation();
		
		assertEquals("town, postcode", location);
	}
	
	@Test
	public void testLocationWithNullLine1AndTownAndPostcode() {
		result.setOrganisationAddress(new AddressResource(null, "line2", "line3", null, "county", null));
		
		String location = result.getLocation();
		
		assertEquals("", location);
	}
}
