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
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CompaniesHouseApiServiceImplTest {

	@InjectMocks
	private CompaniesHouseApiServiceImpl service;
	@Mock
	private AbstractRestTemplateAdaptor adapter;

	private Map<String, Object> searchVariables;

	private Map<String, Object> searchByIndexVariables;

	private Map<String, Object> getCompanyDirectorsUrlVariables;

	private String searchUrlPath;

	private String searchByIndexUrlPath;

	private String getCompanyDirectorsUrlPath;

	private String defaultSearchString;

	@Before
	public void setUp() {
		service.setCompaniesHouseUrl("baseurl/");

		defaultSearchString  = "searchtext";

		searchUrlPath = "baseurl/search/companies?items_per_page={items_per_page}&q={q}";

		// should override these values in specific test if necessary.
		searchVariables = new HashMap<>();
		searchVariables.put("items_per_page", 20);
		searchVariables.put("q", defaultSearchString);

		searchByIndexUrlPath = "baseurl/search/companies?q={q}&items_per_page={items_per_page}&start_index={start_index}";
		searchByIndexVariables = new HashMap<>();
		searchByIndexVariables.put("items_per_page", 10);
		searchByIndexVariables.put("q", defaultSearchString);
		searchByIndexVariables.put("start_index", 1);


		getCompanyDirectorsUrlPath = "baseurl/company/123/officers?items_per_page={items_per_page}&register_type={register_type}";
		getCompanyDirectorsUrlVariables = new HashMap<>();
		getCompanyDirectorsUrlVariables.put("items_per_page", 10);
		getCompanyDirectorsUrlVariables.put("register_type", "directors");

	}
	
	@Test
	public void searchOrganisations() {
		ReflectionTestUtils.setField(service, "isImprovedSearchEnabled", false);
		Map<String, Object> companyResultMap = companyResultMap();
		JsonNode resultNode = new ObjectMapper().valueToTree(asMap("items", asList(companyResultMap)));
		ResponseEntity<JsonNode> response = new ResponseEntity<JsonNode>(resultNode, HttpStatus.OK);
		when(adapter.restGetEntity(searchUrlPath, JsonNode.class, searchVariables)).thenReturn(response);
		
		ServiceResult<List<OrganisationSearchResult>> result = service.searchOrganisations(defaultSearchString, 0);
		
		verify(adapter).restGetEntity(searchUrlPath, JsonNode.class, searchVariables);
		assertTrue(result.isSuccess());
		assertEquals(1, result.getSuccess().size());
		assertEquals("company name", result.getSuccess().get(0).getName());
		assertEquals("1234", result.getSuccess().get(0).getOrganisationSearchId());
		assertEquals("line1", result.getSuccess().get(0).getOrganisationAddress().getAddressLine1());
		assertEquals("line2", result.getSuccess().get(0).getOrganisationAddress().getAddressLine2());
		assertEquals("line3", result.getSuccess().get(0).getOrganisationAddress().getAddressLine3());
		assertEquals("loc", result.getSuccess().get(0).getOrganisationAddress().getTown());
		assertEquals("reg", result.getSuccess().get(0).getOrganisationAddress().getCounty());
		assertEquals("ba1", result.getSuccess().get(0).getOrganisationAddress().getPostcode());
	}

	@Test
	public void searchOrganisationsByIndex() {
		ReflectionTestUtils.setField(service, "isImprovedSearchEnabled", true);
		int indexPostion = 3;
		searchByIndexVariables.put("start_index", indexPostion);

		Map<String, Object> orgSearchResultMapByIndex = improvedSearchResultMap(indexPostion);
		JsonNode resultNode = new ObjectMapper().valueToTree(asMap("items", asList(orgSearchResultMapByIndex)));
		ResponseEntity<JsonNode> response = new ResponseEntity<JsonNode>(resultNode, HttpStatus.OK);
		when(adapter.restGetEntity(searchByIndexUrlPath, JsonNode.class, searchByIndexVariables)).thenReturn(response);

		ServiceResult<List<OrganisationSearchResult>> result = service.searchOrganisations(defaultSearchString,indexPostion);

		verify(adapter).restGetEntity(searchByIndexUrlPath, JsonNode.class, searchByIndexVariables);
		assertTrue(result.isSuccess());
		assertEquals(1, result.getSuccess().size());
		assertEquals("company name2", result.getSuccess().get(0).getName());
		assertEquals("8910", result.getSuccess().get(0).getOrganisationSearchId());
		assertEquals("addressSnippet", result.getSuccess().get(0).getOrganisationAddressSnippet());
	}

	@Test
	public void searchCompanyResultShouldFilterDissolvedCompany() {
		ReflectionTestUtils.setField(service, "isImprovedSearchEnabled", true);
		int indexPostion = 1;

		Map<String, Object> orgSearchResultMapByIndex = improvedSearchResultMap(indexPostion);
		JsonNode resultNode = new ObjectMapper().valueToTree(asMap("items", asList(orgSearchResultMapByIndex)));
		ResponseEntity<JsonNode> response = new ResponseEntity<JsonNode>(resultNode, HttpStatus.OK);
		when(adapter.restGetEntity(searchByIndexUrlPath, JsonNode.class, searchByIndexVariables)).thenReturn(response);

		ServiceResult<List<OrganisationSearchResult>> result = service.searchOrganisations(defaultSearchString,indexPostion);

		verify(adapter).restGetEntity(searchByIndexUrlPath, JsonNode.class, searchByIndexVariables);
		assertEquals(1, result.getSuccess().size());
		assertEquals("dissolved", result.getSuccess().get(0).getOrganisationStatus());
		assertEquals(Boolean.FALSE, result.getSuccess().get(0).isOrganisationValidToDisplay());
	}

	@Test
	public void searchCompanyResultShouldFilterClosedCompany() {
		ReflectionTestUtils.setField(service, "isImprovedSearchEnabled", true);
		int indexPostion = 2;
		searchByIndexVariables.put("start_index", indexPostion);

		Map<String, Object> orgSearchResultMapByIndex = improvedSearchResultMap(indexPostion);
		JsonNode resultNode = new ObjectMapper().valueToTree(asMap("items", asList(orgSearchResultMapByIndex)));
		ResponseEntity<JsonNode> response = new ResponseEntity<JsonNode>(resultNode, HttpStatus.OK);
		when(adapter.restGetEntity(searchByIndexUrlPath, JsonNode.class, searchByIndexVariables)).thenReturn(response);

		ServiceResult<List<OrganisationSearchResult>> result = service.searchOrganisations(defaultSearchString,indexPostion);

		verify(adapter).restGetEntity(searchByIndexUrlPath, JsonNode.class, searchByIndexVariables);
		assertTrue(result.isSuccess());
		assertEquals(1, result.getSuccess().size());
		assertEquals("closed", result.getSuccess().get(0).getOrganisationStatus());
		assertEquals(Boolean.FALSE, result.getSuccess().get(0).isOrganisationValidToDisplay());
	}

    @Test
	public void searchOrganisationsNullAddressLine() {
		ReflectionTestUtils.setField(service, "isImprovedSearchEnabled", false);
		Map<String, Object> companyResultMap = companyResultMap();
		((Map<String,Object>)companyResultMap.get("address")).put("address_line_1", null);
		JsonNode resultNode = new ObjectMapper().valueToTree(asMap("items", asList(companyResultMap)));
		ResponseEntity<JsonNode> response = new ResponseEntity<JsonNode>(resultNode, HttpStatus.OK);
		when(adapter.restGetEntity(searchUrlPath, JsonNode.class, searchVariables)).thenReturn(response);
		
		ServiceResult<List<OrganisationSearchResult>> result = service.searchOrganisations("searchtext", 0);
		
		verify(adapter).restGetEntity(searchUrlPath, JsonNode.class, searchVariables);
		assertTrue(result.isSuccess());
		assertEquals(1, result.getSuccess().size());
		assertEquals("company name", result.getSuccess().get(0).getName());
		assertEquals("1234", result.getSuccess().get(0).getOrganisationSearchId());
		assertNull(result.getSuccess().get(0).getOrganisationAddress().getAddressLine1());
		assertEquals("line2", result.getSuccess().get(0).getOrganisationAddress().getAddressLine2());
		assertEquals("line3", result.getSuccess().get(0).getOrganisationAddress().getAddressLine3());
		assertEquals("loc", result.getSuccess().get(0).getOrganisationAddress().getTown());
		assertEquals("reg", result.getSuccess().get(0).getOrganisationAddress().getCounty());
		assertEquals("ba1", result.getSuccess().get(0).getOrganisationAddress().getPostcode());
	}
	
	@Test
	public void searchOrganisationsNullAddress() {
		ReflectionTestUtils.setField(service, "isImprovedSearchEnabled", false);
		Map<String, Object> companyResultMap = companyResultMap();
		companyResultMap.put("address", null);
		JsonNode resultNode = new ObjectMapper().valueToTree(asMap("items", asList(companyResultMap)));
		ResponseEntity<JsonNode> response = new ResponseEntity<JsonNode>(resultNode, HttpStatus.OK);
		when(adapter.restGetEntity(searchUrlPath, JsonNode.class, searchVariables)).thenReturn(response);
		
		ServiceResult<List<OrganisationSearchResult>> result = service.searchOrganisations(defaultSearchString, 0);
		
		verify(adapter).restGetEntity(searchUrlPath, JsonNode.class, searchVariables);
		assertTrue(result.isSuccess());
		assertEquals(1, result.getSuccess().size());
		assertEquals("company name", result.getSuccess().get(0).getName());
		assertEquals("1234", result.getSuccess().get(0).getOrganisationSearchId());
		assertNull(result.getSuccess().get(0).getOrganisationAddress().getAddressLine1());
		assertNull(result.getSuccess().get(0).getOrganisationAddress().getAddressLine2());
		assertNull(result.getSuccess().get(0).getOrganisationAddress().getAddressLine3());
		assertNull(result.getSuccess().get(0).getOrganisationAddress().getTown());
		assertNull(result.getSuccess().get(0).getOrganisationAddress().getCounty());
		assertNull(result.getSuccess().get(0).getOrganisationAddress().getPostcode());
	}
	
	@Test
	public void searchCompany() {
		ReflectionTestUtils.setField(service, "isImprovedSearchEnabled", false);
		Map<String, Object> companyMap = companyMap();
		JsonNode resultNode = new ObjectMapper().valueToTree(companyMap);
		ResponseEntity<JsonNode> response = new ResponseEntity<JsonNode>(resultNode, HttpStatus.OK);
		when(adapter.restGetEntity("baseurl/company/123", JsonNode.class)).thenReturn(response);
		
		ServiceResult<OrganisationSearchResult> result = service.getOrganisationById("123");
		
		verify(adapter).restGetEntity("baseurl/company/123", JsonNode.class);
		assertTrue(result.isSuccess());
		assertEquals("company name", result.getSuccess().getName());
		assertEquals("1234", result.getSuccess().getOrganisationSearchId());
		assertEquals("line1", result.getSuccess().getOrganisationAddress().getAddressLine1());
		assertEquals("line2", result.getSuccess().getOrganisationAddress().getAddressLine2());
		assertEquals("line3", result.getSuccess().getOrganisationAddress().getAddressLine3());
		assertEquals("loc", result.getSuccess().getOrganisationAddress().getTown());
		assertEquals("reg", result.getSuccess().getOrganisationAddress().getCounty());
		assertEquals("ba1", result.getSuccess().getOrganisationAddress().getPostcode());
	}
	
	@Test
	public void searchCompanyNulAddress() {
		ReflectionTestUtils.setField(service, "isImprovedSearchEnabled", false);
		Map<String, Object> companyMap = companyMap();
		companyMap.put("registered_office_address", null);
		JsonNode resultNode = new ObjectMapper().valueToTree(companyMap);
		ResponseEntity<JsonNode> response = new ResponseEntity<JsonNode>(resultNode, HttpStatus.OK);
		when(adapter.restGetEntity("baseurl/company/123", JsonNode.class)).thenReturn(response);
		
		ServiceResult<OrganisationSearchResult> result = service.getOrganisationById("123");
		
		verify(adapter).restGetEntity("baseurl/company/123", JsonNode.class);
		assertTrue(result.isSuccess());
		assertEquals("company name", result.getSuccess().getName());
		assertEquals("1234", result.getSuccess().getOrganisationSearchId());
		assertNull(result.getSuccess().getOrganisationAddress().getAddressLine1());
		assertNull(result.getSuccess().getOrganisationAddress().getAddressLine2());
		assertNull(result.getSuccess().getOrganisationAddress().getAddressLine3());
		assertNull(result.getSuccess().getOrganisationAddress().getTown());
		assertNull(result.getSuccess().getOrganisationAddress().getCounty());
		assertNull(result.getSuccess().getOrganisationAddress().getPostcode());
	}
	@Test
	public void getCompanyProfile() {
		ReflectionTestUtils.setField(service, "isImprovedSearchEnabled", true);
		Map<String, Object> companyMap = companyMap();
		JsonNode resultNode = new ObjectMapper().valueToTree(companyMap);
		ResponseEntity<JsonNode> response = new ResponseEntity<JsonNode>(resultNode, HttpStatus.OK);
		when(adapter.restGetEntity(getCompanyDirectorsUrlPath, JsonNode.class, getCompanyDirectorsUrlVariables)).thenReturn(response);
		when(adapter.restGetEntity("baseurl/company/123", JsonNode.class)).thenReturn(response);

		ServiceResult<OrganisationSearchResult> result = service.getOrganisationById("123");

		verify(adapter).restGetEntity("baseurl/company/123", JsonNode.class);
		assertTrue(result.isSuccess());
		assertEquals("company name", result.getSuccess().getName());
		assertEquals("1234", result.getSuccess().getOrganisationSearchId());
		assertEquals("line1", result.getSuccess().getOrganisationAddress().getAddressLine1());
		assertEquals("line2", result.getSuccess().getOrganisationAddress().getAddressLine2());
		assertEquals("line3", result.getSuccess().getOrganisationAddress().getAddressLine3());
		assertEquals("loc", result.getSuccess().getOrganisationAddress().getTown());
		assertEquals("reg", result.getSuccess().getOrganisationAddress().getCounty());
		assertEquals("ba1", result.getSuccess().getOrganisationAddress().getPostcode());
	}

	@Test
	public void getCompanyProfileWithSicCodes() {
		ReflectionTestUtils.setField(service, "isImprovedSearchEnabled", true);
		Map<String, Object> companyMapWithSicCodes = companyMapWithSicCodes();
		JsonNode resultNode = new ObjectMapper().valueToTree(companyMapWithSicCodes);
		ResponseEntity<JsonNode> response = new ResponseEntity<JsonNode>(resultNode, HttpStatus.OK);
		when(adapter.restGetEntity(getCompanyDirectorsUrlPath, JsonNode.class, getCompanyDirectorsUrlVariables)).thenReturn(response);
		when(adapter.restGetEntity("baseurl/company/123", JsonNode.class)).thenReturn(response);

		ServiceResult<OrganisationSearchResult> result = service.getOrganisationById("123");

		verify(adapter).restGetEntity("baseurl/company/123", JsonNode.class);
		assertTrue(result.isSuccess());

		assertEquals(sicCodesList().size(), result.getSuccess().getOrganisationSicCodes().size());
		assertEquals("62012", result.getSuccess().getOrganisationSicCodes().get(0).getSicCode());
		assertEquals("62090", result.getSuccess().getOrganisationSicCodes().get(sicCodesList().size() -1).getSicCode());
	}

	@Test
	public void getCompanyProfileWithNullSicCodes() {
		ReflectionTestUtils.setField(service, "isImprovedSearchEnabled", true);
		Map<String, Object> companyMapWithSicCodes = companyMapWithSicCodes();
		companyMapWithSicCodes.put("sic_codes", null);
		JsonNode resultNode = new ObjectMapper().valueToTree(companyMapWithSicCodes);
		ResponseEntity<JsonNode> response = new ResponseEntity<JsonNode>(resultNode, HttpStatus.OK);
		when(adapter.restGetEntity(getCompanyDirectorsUrlPath, JsonNode.class, getCompanyDirectorsUrlVariables)).thenReturn(response);
		when(adapter.restGetEntity("baseurl/company/123", JsonNode.class)).thenReturn(response);

		ServiceResult<OrganisationSearchResult> result = service.getOrganisationById("123");

		verify(adapter).restGetEntity("baseurl/company/123", JsonNode.class);
		assertTrue(result.isSuccess());

		assertEquals(0, result.getSuccess().getOrganisationSicCodes().size());
		assertTrue(result.getSuccess().getOrganisationSicCodes().isEmpty());
	}

	@Test
	public void getCompanyProfileWithDirectors() {
		ReflectionTestUtils.setField(service, "isImprovedSearchEnabled", true);
		Map<String, Object> companyMapWithDirectors = companyMapWithDirectors();
		JsonNode resultNode = new ObjectMapper().valueToTree(asMap("items", asList(companyMapWithDirectors)));
		ResponseEntity<JsonNode> response = new ResponseEntity<JsonNode>(resultNode, HttpStatus.OK);
		when(adapter.restGetEntity(getCompanyDirectorsUrlPath, JsonNode.class, getCompanyDirectorsUrlVariables)).thenReturn(response);
		when(adapter.restGetEntity("baseurl/company/123", JsonNode.class)).thenReturn(response);

		ServiceResult<OrganisationSearchResult> result = service.getOrganisationById("123");
	 	assertTrue(result.isSuccess());

		assertEquals("HANN, Indira", result.getSuccess().getOrganisationExecutiveOfficers().get(0).getName());
	}

	@Test
	public void getCompanyProfileWithNullDirectors() {
		ReflectionTestUtils.setField(service, "isImprovedSearchEnabled", true);
		Map<String, Object> companyMapWithDirectors = companyMapWithDirectors();
		companyMapWithDirectors.put("officer_role", null);
		companyMapWithDirectors.put("name", null);
		JsonNode resultNode = new ObjectMapper().valueToTree(asMap("items", asList(companyMapWithDirectors)));
		ResponseEntity<JsonNode> response = new ResponseEntity<JsonNode>(resultNode, HttpStatus.OK);
		when(adapter.restGetEntity(getCompanyDirectorsUrlPath, JsonNode.class, getCompanyDirectorsUrlVariables)).thenReturn(response);
		when(adapter.restGetEntity("baseurl/company/123", JsonNode.class)).thenReturn(response);

		ServiceResult<OrganisationSearchResult> result = service.getOrganisationById("123");
		assertTrue(result.isSuccess());

		assertEquals(1, result.getSuccess().getOrganisationExecutiveOfficers().size());
		assertEquals(" ", result.getSuccess().getOrganisationExecutiveOfficers().get(0).getName());
	}


	private Map<String, Object> companyResultMap() {
		return asMap("company_number", "1234",
				"title", "company name",
				"company_number", "1234",
				"address", addressMap());
	}
	private Map<String, Object> improvedSearchResultMap(int indexPosition) {
		Map<Integer, Map<String, Object>> indexedSearchResultMap = new HashMap<>();
		indexedSearchResultMap.put(1, asMap("company_number", "1234",
				"title", "company name",
				"company_status", "dissolved",
				"company_number", "1234",
				"address_snippet", "addressSnippet"));
		indexedSearchResultMap.put(2, asMap("company_number", "4567",
				"title", "company name1",
				"company_number", "4567",
				"company_status", "closed",
				"address_snippet", "addressSnippet"));
		indexedSearchResultMap.put(3, asMap("company_number", "8910",
				"title", "company name2",
				"company_number", "8910",
				"address_snippet", "addressSnippet"));
		indexedSearchResultMap.put(4, asMap("company_number", "1112",
				"title", "company name3",
				"company_number", "1112",
				"address_snippet", "addressSnippet"));

		return indexedSearchResultMap.get(indexPosition);

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
	private Map<String, Object> companyMapWithSicCodes() {
		return asMap("company_number", "1234",
				"company_name", "company name",
				"company_number", "1234",
				"registered_office_address", addressMap(),
				"sic_codes", sicCodesList());
	}

	private Map<String, Object> companyMapWithDirectors() {
		return asMap("company_number", "1234",
				"company_name", "company name",
				"company_number", "1234",
				"registered_office_address", addressMap(),
				"sic_codes", sicCodesList(),
				"officer_role", "director",
				"name", "HANN, Indira");
	}

	private List<String> sicCodesList() {
		return asList("62012","62020","62090");
	}

}
