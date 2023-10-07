package com.nnk.springboot.integration_test;

import com.nnk.springboot.controllers.UserController;
import com.nnk.springboot.domain.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;


@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan("com.nnk.springboot.controllers")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserController userController;

    @BeforeEach
    public void setup_data(){

    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void homeUser_ShouldReturnUserList() throws Exception {
        //ACT
        MvcResult result = mvc.perform(get("/user/list"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();
        List<User> expectedUserList = (List) resultModelAndView.getModel().get("users");

        assertThat(resultModelAndView.getViewName()).isEqualTo("user/list");
        assertThat(expectedUserList.size()).isEqualTo(3);
        assertThat(expectedUserList.get(2).getUsername()).isEqualTo("adminTest");
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void userAdd_shouldReturnCorrectURI() throws Exception {
        //ACT
        MvcResult result = mvc.perform(get("/user/add"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();
        assertThat(resultModelAndView.getViewName()).isEqualTo("user/add");
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void userValidate_ShouldReturnUpdatedUserListWithNewUser() throws Exception {
        //ARRANGE
        User userToAdd = new User("userTest3", "passwordWith1WithSymbol!", "User Test 3", "USER");

        //ACT
            //first request that checks the redirect send the proper URI
        mvc.perform(post("/user/validate").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("username", userToAdd.getUsername())
                .param("password", userToAdd.getPassword())
                .param("fullname", userToAdd.getFullname())
                .param("role", userToAdd.getRole())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(redirectedUrl("/user/list"));

            //second request must return updated user list plus added User
        MvcResult result = mvc.perform(get("/user/list"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();
        List<User> expectedUpdatedUserList = (List) resultModelAndView.getModel().get("users");
        assertThat(result.getModelAndView().getViewName()).isEqualTo("user/list");
        assertThat(expectedUpdatedUserList.size()).isEqualTo(4);
        assertThat(expectedUpdatedUserList.get(3).getUsername()).isEqualTo(userToAdd.getUsername());
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void userUpdate_ShouldReturnFormWithUserInfoForUpdate() throws Exception {
        //ARRANGE
        String userIdToUpdate = "1";
    
        //ACT
        MvcResult result = mvc.perform(get("/user/update/{id}", userIdToUpdate)).andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();
        User userToUpdate = (User) resultModelAndView.getModel().get("user");
        assertThat(result.getModelAndView().getViewName()).isEqualTo("user/update");
        assertThat(userToUpdate.getUsername()).isEqualTo("userTest1");
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void userUpdate_ShouldReturnUpdatedUserList() throws Exception {
        //ARRANGE
        String userIdToUpdate = "1";
        User userToUpdate = new User("userTest2.0", "passwordWith2WithSymbol!", "User Test 2.0", "USER");
        userToUpdate.setId(Integer.parseInt(userIdToUpdate));

        //ACT
            //first request that checks the request was properly redirected
        mvc.perform(post("/user/update/{id}", userIdToUpdate).with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("id", String.valueOf(userToUpdate.getId()))
                .param("username", userToUpdate.getUsername())
                .param("password", userToUpdate.getPassword())
                .param("fullname", userToUpdate.getFullname())
                .param("role", userToUpdate.getRole())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(redirectedUrl("/user/list"));

            //second request must return updated User list
        MvcResult result = mvc.perform(get("/user/list"))
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();
        List<User> expectedUpdatedUserList = (List) resultModelAndView.getModel().get("users");
        assertThat(result.getModelAndView().getViewName()).isEqualTo("user/list");
        assertThat(expectedUpdatedUserList.size()).isEqualTo(3);
        assertThat(expectedUpdatedUserList.get(0).getUsername()).isEqualTo(userToUpdate.getUsername());
        assertThat(expectedUpdatedUserList.get(0).getFullname()).isEqualTo(userToUpdate.getFullname());
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void userDelete_shouldReturnUpdatedListMinusDeletedUser() throws Exception {
        //ARRANGE
        String userIdToDelete = "1";

        //ACT
            //first request that checks the request was properly redirected
        mvc.perform(get("/user/delete/{id}", userIdToDelete).with(csrf()))
                .andExpect(redirectedUrl("/user/list"));

            //second request must return updated User list minus deleted User
        MvcResult result = mvc.perform(get("/user/list"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();
        List<User> expectedUpdatedUserList = (List) resultModelAndView.getModel().get("users");
        assertThat(result.getModelAndView().getViewName()).isEqualTo("user/list");
        assertThat(expectedUpdatedUserList.size()).isEqualTo(2);
        assertThat(expectedUpdatedUserList.get(0).getId()).isNotEqualTo(1);
        assertThat(expectedUpdatedUserList.get(0).getId()).isEqualTo(2);
    }

    @Nested
    @Tag("ErrorHandlingCasesTests")
    @DisplayName("Cover and handle borderline cases when user sends partial and wrong data")
    class ErrorHandlingCasesTest{

        @Test
        @DisplayName("Given a User with missing information, when added, then user should be redirected to previous Form")
        @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
        public void userValidate_ShouldReturnCorrectURI_WhenErrorInUserEntry() throws Exception {
            //ARRANGE
            User userToAdd = new User("", "passwordWith1WithSymbol!", "User Test 3", "USER");

            //ACT
            MvcResult result = mvc.perform(post("/user/validate").with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .param("username", userToAdd.getUsername())
                    .param("password", userToAdd.getPassword())
                    .param("fullname", userToAdd.getFullname())
                    .param("role", userToAdd.getRole())
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            ModelAndView resultModelAndView = result.getModelAndView();

            //ASSERT
            assertThat(resultModelAndView).isNotNull();
            assertThat(resultModelAndView.getViewName()).isEqualTo("user/add");
        }

        @Test
        @DisplayName("Given a User Id that doesn't exist, when updated, then Exception should be emitted with correct message")
        @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
        public void userUpdate_ShouldEmitCorrectException_WhenWrongUserId(){
            //ARRANGE
            String userIdToUpdate = "1000";

            //ACT
            assertThatThrownBy(() -> mvc.perform(get("/user/update/{id}", userIdToUpdate)))
                    .hasCauseInstanceOf(IllegalArgumentException.class).hasMessageContaining("Invalid user Id:" + userIdToUpdate);

        }

        @Test
        @DisplayName("Given a User with missing information, when updated, then user should be redirected to previous Form")
        @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
        public void userUpdate_ShouldReturnToForm_WhenWrongUserSubmitted() throws Exception {
            //ARRANGE
            String userIdToUpdate = "1";
            User userToUpdate = new User("", "passwordWith2WithSymbol!", "User Test 2.0", "USER");
            userToUpdate.setId(Integer.parseInt(userIdToUpdate));

            //ACT
            MvcResult result = mvc.perform(post("/user/update/{id}", userIdToUpdate).with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .param("id", String.valueOf(userToUpdate.getId()))
                    .param("username", userToUpdate.getUsername())
                    .param("password", userToUpdate.getPassword())
                    .param("fullname", userToUpdate.getFullname())
                    .param("role", userToUpdate.getRole())
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            ModelAndView resultModelAndView = result.getModelAndView();

            //ASSERT
            assertThat(resultModelAndView).isNotNull();
            assertThat(resultModelAndView.getViewName()).isEqualTo("user/update");
        }

        @Test
        @DisplayName("Given a User Id that doesn't exist, when deleted, then Exception should be emitted with correct message")
        @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
        public void userDelete_ShouldEmitCorrectException_WhenWrongUserId(){
            //ARRANGE
            String userIdToDelete = "1000";

            //ACT
            assertThatThrownBy(() -> mvc.perform(get("/user/delete/{id}", userIdToDelete)))
                    .hasCauseInstanceOf(IllegalArgumentException.class).hasMessageContaining("Invalid user Id:" + userIdToDelete);

        }
    }
}
