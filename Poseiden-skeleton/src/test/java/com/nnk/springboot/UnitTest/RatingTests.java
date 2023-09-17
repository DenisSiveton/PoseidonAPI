package com.nnk.springboot.UnitTest;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RatingTests {

	@Autowired
	private RatingRepository ratingRepository;

	private Rating ratingTest;

	@BeforeEach
	public void setupTestData(){
		// ARRANGE
		ratingTest = new Rating("m_rating Test", "s_rating Test", "f_rating Test", 10);
	}

	@Test
	@DisplayName("Test that saves a Rating")
	public void givenRatingObject_whenSave_thenReturnSavedRating() {
		// ACT
		Rating savedRating = ratingRepository.save(ratingTest);

		//ASSERT
		assertThat(savedRating).isNotNull();
		assertThat(savedRating.getId()).isGreaterThan(0);
		assertThat(savedRating.getMoodysRating()).isEqualTo("m_rating Test");

	}

	@Test
	@DisplayName("Test that gets a list of Rating")
	public void givenRatingList_whenFindAll_thenReturnRatingList(){
		// ACT
		List<Rating> ratingList = ratingRepository.findAll();

		// ASSERT
		assertThat(ratingList).isNotEmpty();
		assertThat(ratingList.size()).isEqualTo(3);
		assertThat(ratingList.get(1).getMoodysRating()).isEqualTo("moodys 2");
	}

	@Test
	@DisplayName("Test to get Rating by Id")
	public void givenRatingObject_whenFindById_thenReturnRatingObject() {
		// ARRANGE
		ratingRepository.save(ratingTest);

		// ACT
		Rating getRating = ratingRepository.findById(ratingTest.getId()).get();

		// ASSERT
		assertThat(getRating).isNotNull();
		assertThat(getRating.getOrderNumber()).isEqualTo(10);
	}

	@Test
	@DisplayName("Test : get Rating update operation")
	public void givenRatingObject_whenUpdate_thenRatingObject() {
		// ARRANGE
		ratingRepository.save(ratingTest);

		// ACT
		Rating getRating = ratingRepository.findById(ratingTest.getId()).get();

		getRating.setOrderNumber(11);
		getRating.setSandPRating("Updated s_rating");

		Rating updatedRating = ratingRepository.save(getRating);

		// ASSERT
		assertThat(updatedRating).isNotNull();
		assertThat(updatedRating.getOrderNumber()).isEqualTo(11);
		assertThat(updatedRating.getSandPRating()).isEqualTo("Updated s_rating");
	}

	@Test
	@DisplayName("Test : delete Rating operation")
	public void givenRatingObject_whenDelete_thenRemoveRating() {
		// ARRANGE
		Integer id = ratingRepository.save(ratingTest).getId();

		// ACT
		ratingRepository.deleteById(id);
		Optional<Rating> deleteRating = ratingRepository.findById(id);

		// ASSERT
		assertThat(deleteRating).isEmpty();
	}
}
