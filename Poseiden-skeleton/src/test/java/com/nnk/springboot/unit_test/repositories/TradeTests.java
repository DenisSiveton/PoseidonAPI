package com.nnk.springboot.unit_test.repositories;

import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TradeTests {

	@Autowired
	private TradeRepository tradeRepository;

	private Trade tradeTest;

	@BeforeEach
	public void setupTestData(){
		// ARRANGE
		tradeTest = new Trade("account Test", "type Test", 32.0);
	}

	@Test
	@DisplayName("Test that saves a Trade")
	public void givenBidListObject_whenSave_thenReturnSavedBidList() {
		// ACT
		Trade savedTrade = tradeRepository.save(tradeTest);

		//ASSERT
		assertThat(savedTrade).isNotNull();
		assertThat(savedTrade.getId()).isGreaterThan(0);
		assertThat(savedTrade.getAccount()).isEqualTo("account Test");

	}

	@Test
	@DisplayName("Test that gets a list of Trade")
	public void givenBidListList_whenFindAll_thenReturnBidList(){
		// ACT
		List<Trade> tradeList = tradeRepository.findAll();

		// ASSERT
		assertThat(tradeList).isNotEmpty();
		assertThat(tradeList.size()).isEqualTo(3);
		assertThat(tradeList.get(1).getBuyQuantity()).isEqualTo(12.0);
	}

	@Test
	@DisplayName("Test to get Trade by Id")
	public void givenBidListObject_whenFindById_thenReturnBidListObject() {
		// ARRANGE
		tradeRepository.save(tradeTest);

		// ACT
		Trade getTrade = tradeRepository.findById(tradeTest.getId()).get();

		// ASSERT
		assertThat(getTrade).isNotNull();
		assertThat(getTrade.getType()).isEqualTo("type Test");
	}

	@Test
	@DisplayName("Test : get Trade update operation")
	public void givenEmployeeObject_whenUpdate_thenEmployeeObject() {
		// ARRANGE
		tradeRepository.save(tradeTest);

		// ACT
		Trade getTrade = tradeRepository.findById(tradeTest.getId()).get();

		getTrade.setAccount("account updated Test");
		getTrade.setType("type updated Test");

		Trade updatedTrade = tradeRepository.save(getTrade);

		// ASSERT
		assertThat(updatedTrade).isNotNull();
		assertThat(updatedTrade.getAccount()).isEqualTo("account updated Test");
		assertThat(updatedTrade.getType()).isEqualTo("type updated Test");
	}

	@Test
	@DisplayName("Test : delete Trade operation")
	public void givenEmployeeObject_whenDelete_thenRemoveEmployee() {
		// ARRANGE
		Integer id = tradeRepository.save(tradeTest).getId();

		// ACT
		tradeRepository.deleteById(id);
		Optional<Trade> deleteTrade = tradeRepository.findById(id);

		// ASSERT
		assertThat(deleteTrade).isEmpty();
	}
}
