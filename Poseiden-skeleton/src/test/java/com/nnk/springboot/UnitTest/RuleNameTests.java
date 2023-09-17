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
		ruleNameTest = new RuleName("name Test", "description Test", "template Test");
	}

	@Test
	@DisplayName("Test that saves a RuleName")
	public void givenRuleNameObject_whenSave_thenReturnSavedRuleName() {
		// ACT
		RuleName savedRuleName = ruleNameRepository.save(ruleNameTest);

		//ASSERT
		assertThat(savedRuleName).isNotNull();
		assertThat(savedRuleName.getId()).isGreaterThan(0);
		assertThat(savedRuleName.getDescription()).isEqualTo("description Test");

	}

	@Test
	@DisplayName("Test that gets a list of RuleName")
	public void givenRuleNameList_whenFindAll_thenReturnRuleNameList(){
		// ACT
		List<RuleName> ruleNameList = ruleNameRepository.findAll();

		// ASSERT
		assertThat(ruleNameList).isNotEmpty();
		assertThat(ruleNameList.size()).isEqualTo(3);
		assertThat(ruleNameList.get(1).getDescription()).isEqualTo("description 2");
	}

	@Test
	@DisplayName("Test to get RuleName by Id")
	public void givenRuleNameObject_whenFindById_thenReturnRuleNameObject() {
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
	public void givenRuleNameObject_whenUpdate_thenRuleNameObject() {
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
	public void givenRuleNameObject_whenDelete_thenRemoveRuleName() {
		// ARRANGE
		Integer id = ruleNameRepository.save(ruleNameTest).getId();

		// ACT
		ruleNameRepository.deleteById(id);
		Optional<RuleName> deleteRating = ruleNameRepository.findById(id);

		// ASSERT
		assertThat(deleteRating).isEmpty();
	}
}
