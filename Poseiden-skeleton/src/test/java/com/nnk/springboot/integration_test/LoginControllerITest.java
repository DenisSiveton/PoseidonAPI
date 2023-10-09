package com.nnk.springboot.integration_test;

import com.nnk.springboot.controllers.LoginController;
import com.nnk.springboot.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan("com.nnk.springboot.controllers")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LoginControllerITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private LoginController loginController;

    @BeforeEach
    public void setup_data(){

    }

    @Test
    public void loginAttempt_ShouldReturnAuthenticatedUser() throws Exception {
        //ACT
        mvc.perform(formLogin().user("userTest1").password("userMDP"))

                //ASSERT
                .andExpect(authenticated());
    }

    @Test
    public void loginRequest_ShouldReturnLoginPage() throws Exception {
        //ACT
        MvcResult result = mvc.perform(get("/login"))
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();
        assertThat(resultModelAndView.getViewName()).isEqualTo("login");
    }

    @Test
    public void loginFailed_ShouldReturnLoginPageWithError() throws Exception {
        //ARRANGE
        String expectedErrorMessage ="The credentials did not match any registered user in the database.";
        // ACT
        MvcResult result = mvc.perform(get("/login-error"))
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();
        assertThat(resultModelAndView.getViewName()).isEqualTo("login");
        String errorMessage = (String) resultModelAndView.getModel().get("errorMessage");
        assertThat(errorMessage).isEqualTo(expectedErrorMessage);

    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void loginSecureArticleDetails_shouldReturnCorrectPageWithUserList() throws Exception {
        //ACT
        MvcResult result = mvc.perform(get("/secure/article-details"))
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
    public void errorHandling_shouldReturnCorrectErrorPageWithCorrectErrorMessage() throws Exception {
        //ARRANGE
        String expectedErrorMessage= "You are not authorized for the requested data.";
        // ACT
        MvcResult result = mvc.perform(get("/error"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();
        assertThat(resultModelAndView.getModel().get("errorMsg").toString()).isEqualTo(expectedErrorMessage);
        assertThat(resultModelAndView.getViewName()).isEqualTo("403");
    }
}
