package com.nnk.springboot.IntegrationTest;

import com.nnk.springboot.controllers.HomeController;
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
public class HomeControllerITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private HomeController homeController;

    @BeforeEach
    public void setup_data() {

    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void home_ShouldReturnCorrectEndPoint() throws Exception {
        //ACT
        MvcResult result = mvc.perform(get("/"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();
        assertThat(resultModelAndView.getViewName()).isEqualTo("home");
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void userHome_ShouldReturnCorrectEndPoint() throws Exception {
        //ACT
        mvc.perform(get("/user/home"))
                .andExpect(redirectedUrl("/bidList/list"));
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "ADMIN")
    public void adminHome_ShouldReturnCorrectEndPoint() throws Exception {
        //ACT
        mvc.perform(get("/admin/home"))
                .andExpect(redirectedUrl("/bidList/list"));
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void defaultAfterLogin_WhenUserLoggedIn_ShouldReturnCorrectEndPointBasedOnRole() throws Exception {
        //ACT
        mvc.perform(get("/default"))
                .andExpect(redirectedUrl("/user/home"));
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "ADMIN")
    public void defaultAfterLogin_WhenAdminLoggedIn_ShouldReturnCorrectEndPointBasedOnRole() throws Exception {
        //ACT
        mvc.perform(get("/default"))
                .andExpect(redirectedUrl("/admin/home"));
    }
}