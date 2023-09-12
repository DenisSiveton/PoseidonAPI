package com.nnk.springboot.IntegrationTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nnk.springboot.controllers.BidListController;
import com.nnk.springboot.domain.BidList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import java.nio.charset.Charset;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan("com.nnk.springboot.controllers")
public class BidListControllerITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private BidListController bidListController;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeEach
    public void setup_data(){

    }

    @Test
    @WithMockUser(username = "j.d@hotmail.com", password = "1234", roles = "USER")
    public void homeBidList_ShouldReturnBidList() throws Exception {
        MvcResult result = mvc.perform(get("/bidList/list"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        ArrayList<BidList> expectedBidList = (ArrayList) resultModelAndView.getModel().get("bidLists");

        assertThat(resultModelAndView.getViewName().equals("bidList/list"));
        assertThat(expectedBidList.size()).isEqualTo(3);
        assertThat(expectedBidList.get(2).getType()).isEqualTo("type_2");
    }

    @Test
    @WithMockUser(username = "j.d@hotmail.com", password = "1234", roles = "USER")
    public void bidListAdd_shouldReturnCorrectURI() throws Exception {
        MvcResult result = mvc.perform(get("/bidList/add"))
                .andReturn();
        assertThat(result.getModelAndView().getViewName()).isEqualTo("bidList/add");
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void bidListValidate_ShouldReturnUpdatedBidListWithNewBidList() throws Exception {
        //ARRANGE
        BidList bidListToAdd = new BidList("account_test","type_test",20.0);

        String bidToString = MAPPER.writeValueAsString(bidListToAdd);

        //ACT
        MvcResult result = mvc.perform(post("/bidList/validate").content(bidToString)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        ArrayList<BidList> expectedUpdatedBidList = (ArrayList) resultModelAndView.getModel().get("bidLists");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("redirect:/bidList/list");
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void bidListUpdate_ShouldReturnFormWithBidListInfoForUpdate() throws Exception {
        //ACT
        MvcResult result = mvc.perform(get("/bidList/update/{id}", 1)).andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        BidList bidListToUpdate = (BidList) resultModelAndView.getModel().get("bidList");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("bidList/update");
        assertThat(bidListToUpdate.getType()).isEqualTo("type_1");
    }

    @Test
    @WithMockUser(username = "j.d@hotmail.com", password = "1234", roles = "USER")
    public void bidListDelete_shouldReturnUpdatedListMinusDeletedBidList() throws Exception {
        //ARRANGE
        String userIdToDelete = "1";
        //ACT
        MvcResult result = mvc.perform(get("/bidList/delete/{id}", userIdToDelete))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        ArrayList<BidList> expectedUpdatedBidList = (ArrayList) resultModelAndView.getModel().get("bidLists");

        //ASSERT
        assertThat(expectedUpdatedBidList.size()).isEqualTo(2);
        assertThat(expectedUpdatedBidList.get(1).getType()).isEqualTo("type_2");
    }
}
