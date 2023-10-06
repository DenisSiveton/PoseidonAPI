package com.nnk.springboot.unit_test.repositories;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BidListTests {

	@Autowired
	private BidListRepository bidListRepository;

	private BidList bidListTest;

	@BeforeEach
	public void setupTestData(){
		// Given : Setup object for test
		bidListTest = new BidList("Account Test", "Type Test", 10d);
	}

	@Test
	@DisplayName("Test that saves a bidList")
	public void givenBidListObject_whenSave_thenReturnSavedBidList() {
		// ACT
		BidList savedBidList = bidListRepository.save(bidListTest);

		//ASSERT
		assertThat(savedBidList).isNotNull();
		assertThat(savedBidList.getId()).isGreaterThan(0);

	}

	@Test
	@DisplayName("Test that gets a list of BidList")
	public void givenBidListList_whenFindAll_thenReturnBidList(){
		// ACT
		List<BidList> bidLists = bidListRepository.findAll();

		// ASSERT
		assertThat(bidLists).isNotEmpty();
		assertThat(bidLists.size()).isEqualTo(3);
		assertThat(bidLists.get(0).getAccount()).isEqualTo("acc_1");
	}

	@Test
	@DisplayName("Test to get BidList by Id")
	public void givenBidListObject_whenFindById_thenReturnBidListObject() {
		// ARRANGE
		bidListRepository.save(bidListTest);

		// ACT
		BidList getBidList = bidListRepository.findById(bidListTest.getId()).get();

		// ASSERT
		assertThat(getBidList).isNotNull();
		assertThat(getBidList.getAccount()).isEqualTo("Account Test");
	}

	@Test
	@DisplayName("Test for get BidList update operation")
	public void givenEmployeeObject_whenUpdate_thenEmployeeObject() {

		// ARRANGE
		bidListRepository.save(bidListTest);

		// ACT
		BidList getBidList = bidListRepository.findById(bidListTest.getId()).get();

		getBidList.setAccount("Account Updated");
		getBidList.setType("Type Updated 2.0");
		getBidList.setBidQuantity(4.20);

		BidList updatedBidList = bidListRepository.save(getBidList);

		// ASSERT
		assertThat(updatedBidList).isNotNull();
		assertThat(updatedBidList.getType()).isEqualTo("Type Updated 2.0");
	}


	// JUnit test for delete employee operation
	@Test
	@DisplayName("JUnit test for delete employee operation")
	public void givenEmployeeObject_whenDelete_thenRemoveEmployee() {

		// ARRANGE

		bidListRepository.save(bidListTest);

		// ACT
		bidListRepository.deleteById(bidListTest.getId());
		Optional<BidList> deleteBidList = bidListRepository.findById(bidListTest.getId());

		// ASSERT
		assertThat(deleteBidList).isEmpty();
	}
}
