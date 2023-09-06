package com.nnk.springboot.UnitTest;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CurvePointTests {

	@Autowired
	private CurvePointRepository curvePointRepository;

	private CurvePoint curvePointTest;

	@BeforeEach
	public void setupTestData(){
		// ARRANGE
		curvePointTest = new CurvePoint(10, 15d, 31d);
	}

	@Test
	@DisplayName("Test that saves a curvePoint")
	public void givenBidListObject_whenSave_thenReturnSavedBidList() {
		// ACT
		CurvePoint savedCurvePoint = curvePointRepository.save(curvePointTest);

		//ASSERT
		assertThat(savedCurvePoint).isNotNull();
		assertThat(savedCurvePoint.getId()).isGreaterThan(0);

	}

	@Test
	@DisplayName("Test that gets a list of CurvePoint")
	public void givenBidListList_whenFindAll_thenReturnBidList(){
		// ARRANGE
		CurvePoint curvePointOne = new CurvePoint(10, 15d, 30d);
		CurvePoint curvePointTwo = new CurvePoint(15, 16d, 40d);

		curvePointRepository.save(curvePointOne);
		curvePointRepository.save(curvePointTwo);

		// ACT
		List<CurvePoint> curvePointList = curvePointRepository.findAll();

		// ASSERT
		assertThat(curvePointList).isNotEmpty();
		assertThat(curvePointList.size()).isEqualTo(2);
		assertThat(curvePointList.get(1).getCurveId()).isEqualTo(15);
	}

	@Test
	@DisplayName("Test to get CurvePoint by Id")
	public void givenBidListObject_whenFindById_thenReturnBidListObject() {
		// ARRANGE
		curvePointRepository.save(curvePointTest);

		// ACT
		CurvePoint getCurvePoint = curvePointRepository.findById(curvePointTest.getId()).get();

		// ASSERT
		assertThat(getCurvePoint).isNotNull();
		assertThat(getCurvePoint.getValue()).isEqualTo(31d);
	}

	@Test
	@DisplayName("Test : get CurvePoint update operation")
	public void givenEmployeeObject_whenUpdate_thenEmployeeObject() {
		// ARRANGE
		curvePointRepository.save(curvePointTest);

		// ACT
		CurvePoint getCurvePoint = curvePointRepository.findById(curvePointTest.getId()).get();

		getCurvePoint.setCurveId(5);
		getCurvePoint.setTerm(4.33);
		getCurvePoint.setValue(4.20);

		CurvePoint updatedCurvePoint = curvePointRepository.save(getCurvePoint);

		// ASSERT
		assertThat(updatedCurvePoint).isNotNull();
		assertThat(updatedCurvePoint.getTerm()).isEqualTo(4.33);
		assertThat(updatedCurvePoint.getValue()).isEqualTo(4.20);
	}

	@Test
	@DisplayName("Test : delete CurvePoint operation")
	public void givenEmployeeObject_whenDelete_thenRemoveEmployee() {
		// ARRANGE
		Integer id = curvePointRepository.save(curvePointTest).getId();

		// ACT
		curvePointRepository.deleteById(id);
		Optional<CurvePoint> deleteCurvePoint = curvePointRepository.findById(id);

		// ASSERT
		assertThat(deleteCurvePoint).isEmpty();
	}
}
