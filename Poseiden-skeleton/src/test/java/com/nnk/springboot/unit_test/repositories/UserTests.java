package com.nnk.springboot.unit_test.repositories;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserTests {

	@Autowired
	private UserRepository userRepository;

	private User userTest;

	@BeforeEach
	public void setupTestData(){
		// ARRANGE
		userTest = new User("usernameTest", "encodedPassword1!", "fullname Test","USER");
	}

	@Test
	@DisplayName("Test that saves a User")
	public void givenUserObject_whenSave_thenReturnSavedUser() {
		// ACT
		User savedUser = userRepository.save(userTest);

		//ASSERT
		assertThat(savedUser).isNotNull();
		assertThat(savedUser.getId()).isGreaterThan(0);
		assertThat(savedUser.getFullname()).isEqualTo(userTest.getFullname());

	}

	@Test
	@DisplayName("Test that gets a list of User")
	public void givenUserList_whenFindAll_thenReturnUserList(){
		// ACT
		List<User> userList = userRepository.findAll();

		// ASSERT
		assertThat(userList).isNotEmpty();
		assertThat(userList.size()).isEqualTo(3);
		assertThat(userList.get(1).getUsername()).isEqualTo("userTest2");
	}

	@Test
	@DisplayName("Test to get User by Id")
	public void givenUserObject_whenFindById_thenReturnUserObject() {
		// ARRANGE
		userRepository.save(userTest);

		// ACT
		User getUser = userRepository.findById(userTest.getId()).get();

		// ASSERT
		assertThat(getUser).isNotNull();
		assertThat(getUser.getUsername()).isEqualTo(userTest.getUsername());
	}

	@Test
	@DisplayName("Test : get User update operation")
	public void givenUserObject_whenUpdate_thenUserObject() {
		// ARRANGE
		userRepository.save(userTest);

		// ACT
		User getUser = userRepository.findById(userTest.getId()).get();

		getUser.setPassword("encodedPassword1!updated");
		getUser.setUsername("usernameTest2.0");

		User updatedUser = userRepository.save(getUser);

		// ASSERT
		assertThat(updatedUser).isNotNull();
		assertThat(updatedUser.getPassword()).isEqualTo("encodedPassword1!updated");
		assertThat(updatedUser.getUsername()).isEqualTo("usernameTest2.0");
	}

	@Test
	@DisplayName("Test : delete User operation")
	public void givenEmployeeObject_whenDelete_thenRemoveEmployee() {
		// ARRANGE
		Integer userIdToDelete = 1;

		// ACT
		userRepository.deleteById(userIdToDelete);
		Optional<User> deleteUser = userRepository.findById(userIdToDelete);

		// ASSERT
		assertThat(deleteUser).isEmpty();
	}
}
