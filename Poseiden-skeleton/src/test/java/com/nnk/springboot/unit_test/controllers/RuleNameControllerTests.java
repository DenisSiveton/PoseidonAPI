package com.nnk.springboot.unit_test.controllers;


import com.nnk.springboot.Application;
import com.nnk.springboot.controllers.RuleNameController;
import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RuleNameRepository;
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
public class RuleNameControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RuleNameController ruleNameController;

    @MockBean
    private RuleNameRepository ruleNameRepository;

    static List<RuleName> ruleNameList;

    @BeforeEach
    public void setUpData(){
        ruleNameList = new ArrayList<>();
        ruleNameList.add(new RuleName("name1","description1","template1"));
        ruleNameList.add(new RuleName("name2","description2","template2"));
    }

    @Test
    @WithMockUser()
    public void home_shouldReturnListOfRuleName_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        when(ruleNameRepository.findAll()).thenReturn(ruleNameList);
        // ACT
        MvcResult result = mockMvc.perform(get("/ruleName/list"))
                .andExpect(status().isOk())
                .andReturn();

        //ASSERT
        assertThat(result.getModelAndView()).isNotNull();
        verify(ruleNameRepository, times(1)).findAll();
        List<RuleName> expectedRuleName = (List) result.getModelAndView().getModel().get("ruleNames");
        assertThat(result.getModelAndView().getViewName()).isEqualTo("ruleName/list");
        assertThat(expectedRuleName.size()).isEqualTo(2);
    }

    @Test
    @WithMockUser()
    public void addRuleNameForm_shouldReturnCorrectViewName() throws Exception {
        // ACT
        MvcResult result = mockMvc.perform(get("/ruleName/add"))
                .andExpect(status().isOk())
                .andReturn();

        //ASSERT
        assertThat(result.getModelAndView()).isNotNull();
        assertThat(result.getModelAndView().getViewName()).isEqualTo("ruleName/add");
    }

    @Test
    @WithMockUser()
    public void ruleNameValidate_ShouldReturnUpdatedRuleNameWithNewRuleName_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        RuleName ruleNameToAdd = new RuleName("name3","description3","template3");
        ruleNameList.add(ruleNameToAdd);
            //Mock called methods
        when(ruleNameRepository.save(any(RuleName.class))).thenReturn(ruleNameToAdd);
        when(ruleNameRepository.findAll())
                .thenReturn(ruleNameList, ruleNameList);
        //ACT
        //first request that checks the redirect send the proper URI
        mockMvc.perform(post("/ruleName/validate").with(csrf())
               .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                   .param("name", ruleNameToAdd.getName())
                   .param("description", ruleNameToAdd.getDescription())
                   .param("template", ruleNameToAdd.getTemplate())
                   .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/ruleName/list"));

        //second request after redirection must return updated ruleName list plus added RuleName
        MvcResult result = mockMvc.perform(get("/ruleName/list"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();
        List<RuleName> expectedUpdatedRuleName = (List) resultModelAndView.getModel().get("ruleNames");
        assertThat(resultModelAndView.getViewName()).isEqualTo("ruleName/list");
        assertThat(expectedUpdatedRuleName.size()).isEqualTo(3);
        assertThat(expectedUpdatedRuleName.get(2).getDescription()).isEqualTo(ruleNameToAdd.getDescription());

            // Mocked calls
        verify(ruleNameRepository, times(1)).save(any(RuleName.class));
        verify(ruleNameRepository, times(2)).findAll();
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void ruleNameUpdate_ShouldReturnFormWithRuleNameInfoForUpdate_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        String ruleNameIdToUpdate = "1";

        when(ruleNameRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(ruleNameList.get(0)));

        //ACT
        MvcResult result = mockMvc.perform(get("/ruleName/update/{id}", ruleNameIdToUpdate)).andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        assertThat(resultModelAndView).isNotNull();
        RuleName ruleNameToUpdate = (RuleName) resultModelAndView.getModel().get("ruleName");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("ruleName/update");
        assertThat(ruleNameToUpdate.getDescription()).isEqualTo(ruleNameList.get(0).getDescription());
        verify(ruleNameRepository, times(1)).findById(1);
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void ruleNameUpdate_ShouldReturnUpdatedRuleName_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        String userIdToUpdate = "1";
        RuleName ruleNameToUpdate = new RuleName("name3","description3","template3");
        ruleNameToUpdate.setId(Integer.parseInt(userIdToUpdate));

        ruleNameList.remove(0);
        ruleNameList.add(0,ruleNameToUpdate);

        when(ruleNameRepository.save(any(RuleName.class))).thenReturn(ruleNameToUpdate);
        when(ruleNameRepository.findAll()).thenReturn(ruleNameList, ruleNameList);

        //ACT
            //first request that checks the request was properly redirected
        mockMvc.perform(post("/ruleName/update/{id}", userIdToUpdate).with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .param("id", String.valueOf(ruleNameToUpdate.getId()))
                    .param("name", ruleNameToUpdate.getName())
                    .param("description", ruleNameToUpdate.getDescription())
                    .param("template", ruleNameToUpdate.getTemplate())
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ruleName/list"));

            //second request must return updated RuleName list
        MvcResult result = mockMvc.perform(get("/ruleName/list"))
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();

        List<RuleName> expectedUpdatedRuleName = (List) resultModelAndView.getModel().get("ruleNames");

        assertThat(result.getModelAndView().getViewName()).isEqualTo("ruleName/list");
        assertThat(expectedUpdatedRuleName.size()).isEqualTo(2);
        assertThat(expectedUpdatedRuleName.get(0).getDescription()).isEqualTo("description3");
        assertThat(expectedUpdatedRuleName.get(0).getName()).isEqualTo(ruleNameList.get(0).getName());

            // Mocked calls
        verify(ruleNameRepository, times(1)).save(any(RuleName.class));
        verify(ruleNameRepository, times(2)).findAll();
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void ruleNameDelete_shouldReturnUpdatedListMinusDeletedRuleName_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        String ruleNameIdToDelete = "1";

        when(ruleNameRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(ruleNameList.get(0)));
        doNothing().when(ruleNameRepository).delete(ruleNameList.get(0));
        RuleName ruleNameDelete = ruleNameList.remove(0);
        when(ruleNameRepository.findAll()).thenReturn(ruleNameList, ruleNameList);

        //ACT
        //first request that checks the request was properly redirected
        mockMvc.perform(get("/ruleName/delete/{id}", ruleNameIdToDelete).with(csrf()))
                .andExpect(redirectedUrl("/ruleName/list"));

        //second request must return updated RuleName list minus deleted RuleName
        MvcResult result = mockMvc.perform(get("/ruleName/list"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        assertThat(resultModelAndView).isNotNull();
        List<RuleName> expectedUpdatedRuleName = (List) resultModelAndView.getModel().get("ruleNames");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("ruleName/list");
        assertThat(expectedUpdatedRuleName.size()).isEqualTo(1);
        assertThat(expectedUpdatedRuleName.get(0).getName()).isEqualTo("name2");

        // Mocked calls
        verify(ruleNameRepository, times(1)).findById(1);
        verify(ruleNameRepository, times(1)).delete(ruleNameDelete);
        verify(ruleNameRepository, times(2)).findAll();
    }
}
