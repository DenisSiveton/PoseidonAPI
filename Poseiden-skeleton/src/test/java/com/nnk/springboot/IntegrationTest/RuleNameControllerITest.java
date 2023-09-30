package com.nnk.springboot.IntegrationTest;

import com.nnk.springboot.controllers.RuleNameController;
import com.nnk.springboot.domain.RuleName;
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
public class RuleNameControllerITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RuleNameController ruleNameController;

    @BeforeEach
    public void setup_data(){

    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void homeRuleName_ShouldReturnRuleNameList() throws Exception {
        //ACT
        MvcResult result = mvc.perform(get("/ruleName/list"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();
        List<RuleName> expectedRuleNameList = (List) resultModelAndView.getModel().get("ruleNames");
        assertThat(resultModelAndView.getViewName()).isEqualTo("ruleName/list");
        assertThat(expectedRuleNameList.size()).isEqualTo(3);
        assertThat(expectedRuleNameList.get(2).getDescription()).isEqualTo("description 3");
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void ruleNameAdd_shouldReturnCorrectURI() throws Exception {
        //ACT
        MvcResult result = mvc.perform(get("/ruleName/add"))
                .andReturn();

        //ASSERT
        ModelAndView resultModelAndView = result.getModelAndView();
        assertThat(resultModelAndView).isNotNull();
        assertThat(resultModelAndView.getViewName()).isEqualTo("ruleName/add");
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void ruleNameValidate_ShouldReturnUpdatedBidListWithNewBidList() throws Exception {
        //ARRANGE
        RuleName ruleNameToAdd = new RuleName("name Test", "description Test", "template Test");

        //ACT
            //first request that checks the redirect send the proper URI
        mvc.perform(post("/ruleName/validate").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("name", ruleNameToAdd.getName())
                .param("description", ruleNameToAdd.getDescription())
                .param("template", ruleNameToAdd.getTemplate())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(redirectedUrl("/ruleName/list"));

            //second request must return updated ruleName list plus added RuleName
        MvcResult result = mvc.perform(get("/ruleName/list"))
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();
        List<RuleName> expectedUpdatedRuleName = (List) resultModelAndView.getModel().get("ruleNames");
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

        //ASSERT
        assertThat(resultModelAndView).isNotNull();
        RuleName ruleNameToUpdate = (RuleName) resultModelAndView.getModel().get("ruleName");
        assertThat(resultModelAndView.getViewName()).isEqualTo("ruleName/update");
        assertThat(ruleNameToUpdate.getName()).isEqualTo("name 1");
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void ruleNameUpdate_ShouldReturnUpdatedRuleNameList() throws Exception {
        //ARRANGE
        String ruleNameIdToUpdate = "3";
        RuleName ruleNameToUpdate = new RuleName("name 3 updated", "description 3 updated", "template 3 updated");
        ruleNameToUpdate.setId(Integer.parseInt(ruleNameIdToUpdate));

        //ACT
            //first request that checks the request was properly redirected
        mvc.perform(post("/ruleName/update/{id}", ruleNameIdToUpdate).with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("id", String.valueOf(ruleNameToUpdate.getId()))
                .param("name", ruleNameToUpdate.getName())
                .param("description", ruleNameToUpdate.getDescription())
                .param("template", ruleNameToUpdate.getTemplate())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(redirectedUrl("/ruleName/list"));

            //second request must return updated RuleName list
        MvcResult result = mvc.perform(get("/ruleName/list"))
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();
        List<RuleName> expectedUpdatedRuleNameList = (List) resultModelAndView.getModel().get("ruleNames");
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
        mvc.perform(get("/ruleName/delete/{id}", ruleNameIdToDelete).with(csrf()))
                .andExpect(redirectedUrl("/ruleName/list"));

            //second request must return updated RuleName list minus deleted RuleName
        MvcResult result = mvc.perform(get("/ruleName/list"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        assertThat(resultModelAndView).isNotNull();
        List<RuleName> expectedUpdatedRuleNameList = (List) resultModelAndView.getModel().get("ruleNames");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("ruleName/list");
        assertThat(expectedUpdatedRuleNameList.size()).isEqualTo(2);
        assertThat(expectedUpdatedRuleNameList.get(1).getDescription()).isEqualTo("description 3");
    }

    @Nested
    @Tag("ErrorHandlingCasesTests")
    @DisplayName("Cover and handle borderline cases when user sends partial and wrong data")
    class ErrorHandlingCasesTest{

        @Test
        @DisplayName("Given a RuleName with missing information, when added, then user should be redirected to previous Form")
        @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
        public void ruleNameValidate_ShouldReturnCorrectURI_WhenErrorInRuleNameEntry() throws Exception {
            //ARRANGE
            RuleName ruleNameToAdd = new RuleName("", "description error", "template error");

            //ACT
            MvcResult result = mvc.perform(post("/ruleName/validate").with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .param("name", ruleNameToAdd.getName())
                    .param("description", ruleNameToAdd.getDescription())
                    .param("template", ruleNameToAdd.getTemplate())
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            ModelAndView resultModelAndView = result.getModelAndView();

            //ASSERT
            assertThat(resultModelAndView).isNotNull();
            assertThat(resultModelAndView.getViewName()).isEqualTo("ruleName/add");
        }

        @Test
        @DisplayName("Given a RuleName Id that doesn't exist, when updated, then Exception should be emitted with correct message")
        @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
        public void ruleNameUpdate_ShouldEmitCorrectException_WhenWrongRuleNameId(){
            //ARRANGE
            String ruleNameIdToUpdate = "1000";

            //ACT
            assertThatThrownBy(() -> mvc.perform(get("/ruleName/update/{id}", ruleNameIdToUpdate)))
                    .hasCauseInstanceOf(IllegalArgumentException.class).hasMessageContaining("Invalid ruleName Id:" + ruleNameIdToUpdate);

        }

        @Test
        @DisplayName("Given a RuleName with missing information, when updated, then user should be redirected to previous Form")
        @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
        public void ruleNameUpdate_ShouldReturnToForm_WhenWrongRuleNameSubmitted() throws Exception {
            //ARRANGE
            String ruleNameIdToUpdate = "3";
            RuleName ruleNameToUpdate = new RuleName("", "description error", "template error");
            ruleNameToUpdate.setId(Integer.parseInt(ruleNameIdToUpdate));

            //ACT
            MvcResult result = mvc.perform(post("/ruleName/update/{id}", ruleNameIdToUpdate).with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .param("id", String.valueOf(ruleNameToUpdate.getId()))
                    .param("name", ruleNameToUpdate.getName())
                    .param("description", ruleNameToUpdate.getDescription())
                    .param("template", ruleNameToUpdate.getTemplate())
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            ModelAndView resultModelAndView = result.getModelAndView();

            //ASSERT
            assertThat(resultModelAndView).isNotNull();
            assertThat(resultModelAndView.getViewName()).isEqualTo("ruleName/update");
        }

        @Test
        @DisplayName("Given a RuleName Id that doesn't exist, when deleted, then Exception should be emitted with correct message")
        @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
        public void ruleNameDelete_ShouldEmitCorrectException_WhenWrongRuleNameId(){
            //ARRANGE
            String ruleNameIdToDelete = "1000";

            //ACT
            assertThatThrownBy(() -> mvc.perform(get("/ruleName/delete/{id}", ruleNameIdToDelete)))
                    .hasCauseInstanceOf(IllegalArgumentException.class).hasMessageContaining("Invalid ruleName Id:" + ruleNameIdToDelete);

        }
    }
}
