package com.nnk.springboot.UnitTest;

import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RuleNameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RuleNameTests {

	@Autowired
	private RuleNameRepository ruleNameRepository;

	private RuleName ruleNameTest;

	@BeforeEach
	public void setupTestData(){
		// ARRANGE
		ruleNameTest = new RuleName("name Test", "description Test", "json Test", "template Test", "sql Test", "sql_part Test");
	}

	@Test
	@DisplayName("Test that saves a RuleName")
	public void givenBidListObject_whenSave_thenReturnSavedBidList() {
		// ACT
		RuleName savedRuleName = ruleNameRepository.save(ruleNameTest);

		//ASSERT
		assertThat(savedRuleName).isNotNull();
		assertThat(savedRuleName.getId()).isGreaterThan(0);
		assertThat(savedRuleName.getDescription()).isEqualTo("description Test");

	}

	@Test
	@DisplayName("Test that gets a list of RuleName")
	public void givenBidListList_whenFindAll_thenReturnBidList(){
		// ARRANGE
		RuleName ruleNameOne = new RuleName("name 1 Test", "description 1 Test", "json 1 Test", "template 1 Test", "sql 1 Test", "sql_part 1 Test");
		RuleName ruleNameTwo = new RuleName("name 2 Test", "description 2 Test", "json 2 Test", "template 2 Test", "sql 2 Test", "sql_part 2 Test");

		ruleNameRepository.save(ruleNameOne);
		ruleNameRepository.save(ruleNameTwo);

		// ACT
		List<RuleName> ruleNameList = ruleNameRepository.findAll();

		// ASSERT
		assertThat(ruleNameList).isNotEmpty();
		assertThat(ruleNameList.size()).isEqualTo(2);
		assertThat(ruleNameList.get(1).getSqlPart()).isEqualTo("sql_part 2 Test");
	}

	@Test
	@DisplayName("Test to get RuleName by Id")
	public void givenBidListObject_whenFindById_thenReturnBidListObject() {
		// ARRANGE
		ruleNameRepository.save(ruleNameTest);

		// ACT
		RuleName getRuleName = ruleNameRepository.findById(ruleNameTest.getId()).get();

		// ASSERT
		assertThat(getRuleName).isNotNull();
		assertThat(getRuleName.getDescription()).isEqualTo("description Test");
	}

	@Test
	@DisplayName("Test : get RuleName update operation")
	public void givenEmployeeObject_whenUpdate_thenEmployeeObject() {
		// ARRANGE
		ruleNameRepository.save(ruleNameTest);

		// ACT
		RuleName getRuleName = ruleNameRepository.findById(ruleNameTest.getId()).get();

		getRuleName.setDescription("description updated Test");
		getRuleName.setName("name updated Test");

		RuleName updatedRuleName = ruleNameRepository.save(getRuleName);

		// ASSERT
		assertThat(updatedRuleName).isNotNull();
		assertThat(updatedRuleName.getDescription()).isEqualTo("description updated Test");
		assertThat(updatedRuleName.getName()).isEqualTo("name updated Test");
	}

	@Test
	@DisplayName("Test : delete RuleName operation")
	public void givenEmployeeObject_whenDelete_thenRemoveEmployee() {
		// ARRANGE
		Integer id = ruleNameRepository.save(ruleNameTest).getId();

		// ACT
		ruleNameRepository.deleteById(id);
		Optional<RuleName> deleteRating = ruleNameRepository.findById(id);

		// ASSERT
		assertThat(deleteRating).isEmpty();
	}
}
