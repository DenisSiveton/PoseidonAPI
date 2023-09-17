package com.nnk.springboot.IntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnk.springboot.controllers.BidListController;
import com.nnk.springboot.controllers.RatingController;
import com.nnk.springboot.controllers.RuleNameController;
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.domain.RuleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;


@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan("com.nnk.springboot.controllers")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RuleNameControllerITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RuleNameController ruleNameController;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeEach
    public void setup_data(){

    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void homeRuleName_ShouldReturnRuleNameList() throws Exception {
        MvcResult result = mvc.perform(get("/ruleName/list"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        ArrayList<RuleName> expectedRuleNameList = (ArrayList) resultModelAndView.getModel().get("ruleNames");

        assertThat(resultModelAndView.getViewName().equals("ruleName/list"));
        assertThat(expectedRuleNameList.size()).isEqualTo(3);
        assertThat(expectedRuleNameList.get(2).getDescription()).isEqualTo("description 3");
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void ruleNameAdd_shouldReturnCorrectURI() throws Exception {
        MvcResult result = mvc.perform(get("/ruleName/add"))
                .andReturn();
        assertThat(result.getModelAndView().getViewName()).isEqualTo("ruleName/add");
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void ruleNameValidate_ShouldReturnUpdatedBidListWithNewBidList() throws Exception {
        //ARRANGE
        RuleName ruleNameToAdd = new RuleName("name Test", "description Test", "template Test");

        String ruleNameToString = MAPPER.writeValueAsString(ruleNameToAdd);

        //ACT
            //first request that checks the redirect send the proper URI
        mvc.perform(post("/ruleName/validate").with(csrf()).content(ruleNameToString)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(redirectedUrl("/ruleName/list"));

            //second request must return updated ruleName list plus added RuleName
        MvcResult result = mvc.perform(get("/ruleName/list"))
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();
        ArrayList<RuleName> expectedUpdatedRuleName = (ArrayList) resultModelAndView.getModel().get("ruleNames");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("ruleName/list");
        assertThat(expectedUpdatedRuleName.size()).isEqualTo(4);
        assertThat(expectedUpdatedRuleName.get(3).getName()).isEqualTo(ruleNameToAdd.getName());
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void ruleNameUpdate_ShouldReturnFormWithRuleNameInfoForUpdate() throws Exception {
        //ARRANGE
        String ruleNameIdToUpdate = "1";

        //ACT
        MvcResult result = mvc.perform(get("/ruleName/update/{id}", ruleNameIdToUpdate)).andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        RuleName ruleNameToUpdate = (RuleName) resultModelAndView.getModel().get("ruleName");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("ruleName/update");
        assertThat(ruleNameToUpdate.getName()).isEqualTo("name 1");
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void ruleNameUpdate_ShouldReturnUpdatedRuleNameList() throws Exception {
        //ARRANGE
        String ruleNameIdToUpdate = "3";
        RuleName ruleNameToAdd = new RuleName("name 3 updated", "description 3 updated", "template 3 updated");
        ruleNameToAdd.setId(Integer.parseInt(ruleNameIdToUpdate));
        String ruleNameToString = MAPPER.writeValueAsString(ruleNameToAdd);

        //ACT
            //first request that checks the request was properly redirected
        mvc.perform(patch("/ruleName/update/{id}", ruleNameIdToUpdate).with(csrf()).content(ruleNameToString)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(redirectedUrl("/ruleName/list"));

            //second request must return updated RuleName list
        MvcResult result = mvc.perform(get("/ruleName/list"))
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();
        ArrayList<RuleName> expectedUpdatedRuleNameList = (ArrayList) resultModelAndView.getModel().get("ruleNames");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("ruleName/list");
        assertThat(expectedUpdatedRuleNameList.size()).isEqualTo(3);
        assertThat(expectedUpdatedRuleNameList.get(2).getTemplate()).isEqualTo("template 3 updated");
        assertThat(expectedUpdatedRuleNameList.get(2).getDescription()).isEqualTo("description 3 updated");
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void ruleNameDelete_shouldReturnUpdatedListMinusDeletedRuleNAme() throws Exception {
        //ARRANGE
        String ruleNameIdToDelete = "1";

        //ACT
            //first request that checks the request was properly redirected
        mvc.perform(delete("/ruleName/delete/{id}", ruleNameIdToDelete).with(csrf()))
                .andExpect(redirectedUrl("/ruleName/list"));;

            //second request must return updated RuleName list minus deleted RuleName
        MvcResult result = mvc.perform(get("/ruleName/list"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        ArrayList<RuleName> expectedUpdatedRuleNameList = (ArrayList) resultModelAndView.getModel().get("ruleNames");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("ruleName/list");
        assertThat(expectedUpdatedRuleNameList.size()).isEqualTo(2);
        assertThat(expectedUpdatedRuleNameList.get(1).getDescription()).isEqualTo("description 3");
    }
}
