package org.innovateuk.ifs.organisation.transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CompanyHouseApiServiceImplTest {

	@InjectMocks
	private CompanyHouseApiServiceImpl service;
	@Mock
	private AbstractRestTemplateAdaptor adapter;

	private Map<String, Object> searchVariables;

	private String searchUrlPath;

	private String defaultSearchString;
	
	@Before
	public void setUp() {
		service.setCompanyHouseUrl("baseurl/");

		defaultSearchString  = "searchtext";

		searchUrlPath = "baseurl/search/companies?items_per_page={items_per_page}&q={q}";

		// should override these values in specific test if necessary.
		searchVariables = new HashMap<>();
		searchVariables.put("items_per_page", 10);
		searchVariables.put("q", defaultSearchString);

	}
	
	@Test
	public void searchOrganisations() {
		
		Map<String, Object> companyResultMap = companyResultMap();
		JsonNode resultNode = new ObjectMapper().valueToTree(asMap("items", asList(companyResultMap)));
		ResponseEntity<JsonNode> response = new ResponseEntity<JsonNode>(resultNode, HttpStatus.OK);
		when(adapter.restGetEntity(searchUrlPath, JsonNode.class, searchVariables)).thenReturn(response);
		
		ServiceResult<List<OrganisationSearchResult>> result = service.searchOrganisations(defaultSearchString);
		
		verify(adapter).restGetEntity(searchUrlPath, JsonNode.class, searchVariables);
		assertTrue(result.isSuccess());
		assertEquals(1, result.getSuccessObject().size());
		assertEquals("company name", result.getSuccessObject().get(0).getName());
		assertEquals("1234", result.getSuccessObject().get(0).getOrganisationSearchId());
		assertEquals("line1", result.getSuccessObject().get(0).getOrganisationAddress().getAddressLine1());
		assertEquals("line2", result.getSuccessObject().get(0).getOrganisationAddress().getAddressLine2());
		assertEquals("line3", result.getSuccessObject().get(0).getOrganisationAddress().getAddressLine3());
		assertEquals("loc", result.getSuccessObject().get(0).getOrganisationAddress().getTown());
		assertEquals("reg", result.getSuccessObject().get(0).getOrganisationAddress().getCounty());
		assertEquals("ba1", result.getSuccessObject().get(0).getOrganisationAddress().getPostcode());
	}

	@Test
	public void searchOrganisationsNullAddressLine() {
		
		Map<String, Object> companyResultMap = companyResultMap();
		((Map<String,Object>)companyResultMap.get("address")).put("address_line_1", null);
		JsonNode resultNode = new ObjectMapper().valueToTree(asMap("items", asList(companyResultMap)));
		ResponseEntity<JsonNode> response = new ResponseEntity<JsonNode>(resultNode, HttpStatus.OK);
		when(adapter.restGetEntity(searchUrlPath, JsonNode.class, searchVariables)).thenReturn(response);
		
		ServiceResult<List<OrganisationSearchResult>> result = service.searchOrganisations("searchtext");
		
		verify(adapter).restGetEntity(searchUrlPath, JsonNode.class, searchVariables);
		assertTrue(result.isSuccess());
		assertEquals(1, result.getSuccessObject().size());
		assertEquals("company name", result.getSuccessObject().get(0).getName());
		assertEquals("1234", result.getSuccessObject().get(0).getOrganisationSearchId());
		assertNull(result.getSuccessObject().get(0).getOrganisationAddress().getAddressLine1());
		assertEquals("line2", result.getSuccessObject().get(0).getOrganisationAddress().getAddressLine2());
		assertEquals("line3", result.getSuccessObject().get(0).getOrganisationAddress().getAddressLine3());
		assertEquals("loc", result.getSuccessObject().get(0).getOrganisationAddress().getTown());
		assertEquals("reg", result.getSuccessObject().get(0).getOrganisationAddress().getCounty());
		assertEquals("ba1", result.getSuccessObject().get(0).getOrganisationAddress().getPostcode());
	}
	
	@Test
	public void searchOrganisationsNullAddress() {
		
		Map<String, Object> companyResultMap = companyResultMap();
		companyResultMap.put("address", null);
		JsonNode resultNode = new ObjectMapper().valueToTree(asMap("items", asList(companyResultMap)));
		ResponseEntity<JsonNode> response = new ResponseEntity<JsonNode>(resultNode, HttpStatus.OK);
		when(adapter.restGetEntity(searchUrlPath, JsonNode.class, searchVariables)).thenReturn(response);
		
		ServiceResult<List<OrganisationSearchResult>> result = service.searchOrganisations(defaultSearchString);
		
		verify(adapter).restGetEntity(searchUrlPath, JsonNode.class, searchVariables);
		assertTrue(result.isSuccess());
		assertEquals(1, result.getSuccessObject().size());
		assertEquals("company name", result.getSuccessObject().get(0).getName());
		assertEquals("1234", result.getSuccessObject().get(0).getOrganisationSearchId());
		assertNull(result.getSuccessObject().get(0).getOrganisationAddress().getAddressLine1());
		assertNull(result.getSuccessObject().get(0).getOrganisationAddress().getAddressLine2());
		assertNull(result.getSuccessObject().get(0).getOrganisationAddress().getAddressLine3());
		assertNull(result.getSuccessObject().get(0).getOrganisationAddress().getTown());
		assertNull(result.getSuccessObject().get(0).getOrganisationAddress().getCounty());
		assertNull(result.getSuccessObject().get(0).getOrganisationAddress().getPostcode());
	}
	
	@Test
	public void searchCompany() {
		
		Map<String, Object> companyMap = companyMap();
		JsonNode resultNode = new ObjectMapper().valueToTree(companyMap);
		ResponseEntity<JsonNode> response = new ResponseEntity<JsonNode>(resultNode, HttpStatus.OK);
		when(adapter.restGetEntity("baseurl/company/123", JsonNode.class)).thenReturn(response);
		
		ServiceResult<OrganisationSearchResult> result = service.getOrganisationById("123");
		
		verify(adapter).restGetEntity("baseurl/company/123", JsonNode.class);
		assertTrue(result.isSuccess());
		assertEquals("company name", result.getSuccessObject().getName());
		assertEquals("1234", result.getSuccessObject().getOrganisationSearchId());
		assertEquals("line1", result.getSuccessObject().getOrganisationAddress().getAddressLine1());
		assertEquals("line2", result.getSuccessObject().getOrganisationAddress().getAddressLine2());
		assertEquals("line3", result.getSuccessObject().getOrganisationAddress().getAddressLine3());
		assertEquals("loc", result.getSuccessObject().getOrganisationAddress().getTown());
		assertEquals("reg", result.getSuccessObject().getOrganisationAddress().getCounty());
		assertEquals("ba1", result.getSuccessObject().getOrganisationAddress().getPostcode());
	}
	
	@Test
	public void searchCompanyNulAddress() {
		
		Map<String, Object> companyMap = companyMap();
		companyMap.put("registered_office_address", null);
		JsonNode resultNode = new ObjectMapper().valueToTree(companyMap);
		ResponseEntity<JsonNode> response = new ResponseEntity<JsonNode>(resultNode, HttpStatus.OK);
		when(adapter.restGetEntity("baseurl/company/123", JsonNode.class)).thenReturn(response);
		
		ServiceResult<OrganisationSearchResult> result = service.getOrganisationById("123");
		
		verify(adapter).restGetEntity("baseurl/company/123", JsonNode.class);
		assertTrue(result.isSuccess());
		assertEquals("company name", result.getSuccessObject().getName());
		assertEquals("1234", result.getSuccessObject().getOrganisationSearchId());
		assertNull(result.getSuccessObject().getOrganisationAddress().getAddressLine1());
		assertNull(result.getSuccessObject().getOrganisationAddress().getAddressLine2());
		assertNull(result.getSuccessObject().getOrganisationAddress().getAddressLine3());
		assertNull(result.getSuccessObject().getOrganisationAddress().getTown());
		assertNull(result.getSuccessObject().getOrganisationAddress().getCounty());
		assertNull(result.getSuccessObject().getOrganisationAddress().getPostcode());
	}

	private Map<String, Object> companyResultMap() {
		return asMap("company_number", "1234",
				"title", "company name",
				"company_number", "1234",
				"address", addressMap());
	}
	
	private Map<String, Object> companyMap() {
		return asMap("company_number", "1234",
				"company_name", "company name",
				"company_number", "1234",
				"registered_office_address", addressMap());
	}
	
	private Map<String, Object> addressMap() {
		return asMap("address_line_1","line1",
				"address_line_2","line2",
				"address_line_3","line3",
				"locality","loc",
				"region","reg",
				"postal_code","ba1"
					);
	}
	
	
}
