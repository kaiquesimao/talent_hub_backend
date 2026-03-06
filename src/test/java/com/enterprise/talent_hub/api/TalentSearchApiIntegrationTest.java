package com.enterprise.talent_hub.api;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.enterprise.talent_hub.TestcontainersConfiguration;
import com.enterprise.talent_hub.repository.CountryRepository;
import com.enterprise.talent_hub.repository.EmployeeRepository;
import com.enterprise.talent_hub.repository.SkillRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@Testcontainers(disabledWithoutDocker = true)
class TalentSearchApiIntegrationTest {

	private static final String KAIQUE_SIMAO_NAME = "Kaique Simão";
	private static final String KAIQUE_SIMAO_EMAIL = "kaique.simao@talenthub.com";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private SkillRepository skillRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Test
	void listCountriesReturnsSortedPayload() throws Exception {
		mockMvc.perform(get("/api/v1/meta/countries"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(4)))
				.andExpect(jsonPath("$[0].name").value("Brazil"));
	}

	@Test
	void listSkillsReturnsSortedPayload() throws Exception {
		mockMvc.perform(get("/api/v1/meta/skills"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(7)))
				.andExpect(jsonPath("$[0].name").value("Communication"));
	}

	@Test
	void searchEmployeesWithNoFiltersReturnsPagedResult() throws Exception {
		mockMvc.perform(get("/api/v1/employees"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.page").value(0))
				.andExpect(jsonPath("$.size").value(12))
				.andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(4)))
				.andExpect(jsonPath("$.content[0].name").value(KAIQUE_SIMAO_NAME));
	}

	@Test
	void searchEmployeesWithCountrySkillAndProficiencyFiltersReturnsExpectedEmployee() throws Exception {
		Long brazilId = countryRepository.findAllByOrderByNameAsc().stream()
				.filter(country -> "BR".equals(country.getIsoCode()))
				.findFirst()
				.orElseThrow()
				.getId();

		Long javaSkillId = skillRepository.findAllByOrderByNameAsc().stream()
				.filter(skill -> "Java".equals(skill.getName()))
				.findFirst()
				.orElseThrow()
				.getId();

		mockMvc.perform(get("/api/v1/employees")
				.param("countryId", brazilId.toString())
				.param("skillId", javaSkillId.toString())
				.param("proficiencyLevel", "EXPERT"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalElements").value(1))
				.andExpect(jsonPath("$.content[0].name").value(KAIQUE_SIMAO_NAME));
	}

	@Test
	void findEmployeeProfileReturnsDetailedPayload() throws Exception {
		Long anaId = employeeRepository.findAll().stream()
				.filter(employee -> KAIQUE_SIMAO_EMAIL.equals(employee.getEmail()))
				.findFirst()
				.orElseThrow()
				.getId();

		mockMvc.perform(get("/api/v1/employees/{id}", anaId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value(KAIQUE_SIMAO_NAME))
				.andExpect(jsonPath("$.country.isoCode").value("BR"))
				.andExpect(jsonPath("$.skills.length()").value(greaterThanOrEqualTo(3)))
				.andExpect(jsonPath("$.skills[0].skillName").value("English"));
	}

	@Test
	void findEmployeeProfileWithUnknownIdReturnsProblemDetail() throws Exception {
		mockMvc.perform(get("/api/v1/employees/{id}", 999_999L))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.title").value("Resource not found"))
				.andExpect(jsonPath("$.detail").value(containsString("999999")));
	}
}
