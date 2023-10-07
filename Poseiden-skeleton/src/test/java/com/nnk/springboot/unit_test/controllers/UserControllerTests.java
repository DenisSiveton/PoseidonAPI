package com.nnk.springboot.unit_test.controllers;


import com.nnk.springboot.Application;
import com.nnk.springboot.controllers.UserController;
import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@ComponentScan("com.nnk.springboot.controllers")
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @MockBean
    private UserRepository userRepository;

    static List<User> userList;

    @BeforeEach
    public void setUpData(){
        userList = new ArrayList<>();
        userList.add(new User("user1","$2a$10$3.GD7d4o4pNREE2HdbY52O5VINdNLOSe1W7LT4txvo7NxPElYcVEO","User 1","USER"));
        userList.add(new User("user2","$2a$10$3.GD7d4o4pNREE2HdbY52O5VINdNLOSe1W7LT4txvo7NxPElYcVEO","User 2","USER"));
    }

    @Test
    @WithMockUser()
    public void home_shouldReturnListOfUser_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        when(userRepository.findAll()).thenReturn(userList);
        // ACT
        MvcResult result = mockMvc.perform(get("/user/list"))
                .andExpect(status().isOk())
                .andReturn();

        //ASSERT
        assertThat(result.getModelAndView()).isNotNull();
        verify(userRepository, times(1)).findAll();
        List<User> expectedUser = (List) result.getModelAndView().getModel().get("users");
        assertThat(result.getModelAndView().getViewName()).isEqualTo("user/list");
        assertThat(expectedUser.size()).isEqualTo(2);
    }

    @Test
    @WithMockUser()
    public void addUserForm_shouldReturnCorrectViewName() throws Exception {
        // ACT
        MvcResult result = mockMvc.perform(get("/user/add"))
                .andExpect(status().isOk())
                .andReturn();

        //ASSERT
        assertThat(result.getModelAndView()).isNotNull();
        assertThat(result.getModelAndView().getViewName()).isEqualTo("user/add");
    }

    @Test
    @WithMockUser()
    public void userValidate_ShouldReturnUpdatedUserWithNewUser_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        User userToAdd = new User("user1","$2a$10$3.GD7d4o4pNREE2HdbY52O5VINdNLOSe1W7LT4txvo7NxPElYcVEO","User 1","USER");
        userList.add(userToAdd);
            //Mock called methods
        when(userRepository.save(any(User.class))).thenReturn(userToAdd);
        when(userRepository.findAll())
                .thenReturn(userList, userList);
        //ACT
        //first request that checks the redirect send the proper URI
        mockMvc.perform(post("/user/validate").with(csrf())
               .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                   .param("username", userToAdd.getUsername())
                   .param("password", userToAdd.getPassword())
                   .param("fullname", userToAdd.getFullname())
                   .param("role", userToAdd.getRole())
                   .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/user/list"));

        //second request after redirection must return updated user list plus added User
        MvcResult result = mockMvc.perform(get("/user/list"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();
        List<User> expectedUpdatedUser = (List) resultModelAndView.getModel().get("users");
        assertThat(resultModelAndView.getViewName()).isEqualTo("user/list");
        assertThat(expectedUpdatedUser.size()).isEqualTo(3);
        assertThat(expectedUpdatedUser.get(2).getUsername()).isEqualTo(userToAdd.getUsername());

            // Mocked calls
        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(2)).findAll();
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void userUpdate_ShouldReturnFormWithUserInfoForUpdate_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        String userIdToUpdate = "1";

        when(userRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(userList.get(0)));

        //ACT
        MvcResult result = mockMvc.perform(get("/user/update/{id}", userIdToUpdate)).andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        assertThat(resultModelAndView).isNotNull();
        User userToUpdate = (User) resultModelAndView.getModel().get("user");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("user/update");
        assertThat(userToUpdate.getFullname()).isEqualTo(userList.get(0).getFullname());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void userUpdate_ShouldReturnUpdatedUser_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        String userIdToUpdate = "1";
        User userToUpdate = new User("user1.0","$2a$10$3.GD7d4o4pNREE2HdbY52O5VINdNLOSe1W7LT4txvo7NxPElYcVEO","User updated 1","USER");
        userToUpdate.setId(Integer.parseInt(userIdToUpdate));

        userList.remove(0);
        userList.add(0,userToUpdate);

        when(userRepository.save(any(User.class))).thenReturn(userToUpdate);
        when(userRepository.findAll()).thenReturn(userList, userList);

        //ACT
            //first request that checks the request was properly redirected
        mockMvc.perform(post("/user/update/{id}", userIdToUpdate).with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .param("id", String.valueOf(userToUpdate.getId()))
                    .param("username", userToUpdate.getUsername())
                    .param("password", userToUpdate.getPassword())
                    .param("fullname", userToUpdate.getFullname())
                    .param("role", userToUpdate.getRole())
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/list"));

            //second request must return updated User list
        MvcResult result = mockMvc.perform(get("/user/list"))
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();

        List<User> expectedUpdatedUser = (List) resultModelAndView.getModel().get("users");

        assertThat(result.getModelAndView().getViewName()).isEqualTo("user/list");
        assertThat(expectedUpdatedUser.size()).isEqualTo(2);
        assertThat(expectedUpdatedUser.get(0).getRole()).isEqualTo("USER");
        assertThat(expectedUpdatedUser.get(0).getFullname()).isEqualTo("User updated 1");

            // Mocked calls
        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(2)).findAll();
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void userDelete_shouldReturnUpdatedListMinusDeletedUser_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        String userIdToDelete = "1";

        when(userRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(userList.get(0)));
        doNothing().when(userRepository).delete(userList.get(0));
        User userDelete = userList.remove(0);
        when(userRepository.findAll()).thenReturn(userList, userList);

        //ACT
        //first request that checks the request was properly redirected
        mockMvc.perform(get("/user/delete/{id}", userIdToDelete).with(csrf()))
                .andExpect(redirectedUrl("/user/list"));

        //second request must return updated User list minus deleted User
        MvcResult result = mockMvc.perform(get("/user/list"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        assertThat(resultModelAndView).isNotNull();
        List<User> expectedUpdatedUser = (List) resultModelAndView.getModel().get("users");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("user/list");
        assertThat(expectedUpdatedUser.size()).isEqualTo(1);
        assertThat(expectedUpdatedUser.get(0).getFullname()).isEqualTo("User 2");

        // Mocked calls
        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).delete(userDelete);
        verify(userRepository, times(2)).findAll();
    }
}
