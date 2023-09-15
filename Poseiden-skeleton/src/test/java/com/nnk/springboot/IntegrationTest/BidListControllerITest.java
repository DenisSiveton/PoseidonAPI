package com.nnk.springboot.IntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnk.springboot.controllers.BidListController;
import com.nnk.springboot.domain.BidList;
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
            //first request that checks the redirect send the proper URI
        mvc.perform(post("/bidList/validate").with(csrf()).content(bidToString)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(redirectedUrl("/bidList/list"));

            //second request must return updated bidList list plus added BidList
        MvcResult result = mvc.perform(get("/bidList/list"))
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();
        ArrayList<BidList> expectedUpdatedBidList = (ArrayList) resultModelAndView.getModel().get("bidLists");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("bidList/list");
        assertThat(expectedUpdatedBidList.size()).isEqualTo(4);
        assertThat(expectedUpdatedBidList.get(3).getBidQuantity()).isEqualTo(bidListToAdd.getBidQuantity());
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void bidListUpdate_ShouldReturnFormWithBidListInfoForUpdate() throws Exception {
        //ARRANGE
        String userIdToDelete = "1";

        //ACT
        MvcResult result = mvc.perform(get("/bidList/update/{id}", userIdToDelete)).andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        BidList bidListToUpdate = (BidList) resultModelAndView.getModel().get("bidList");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("bidList/update");
        assertThat(bidListToUpdate.getType()).isEqualTo("type_1");
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void bidListUpdate_ShouldReturnUpdatedBidList() throws Exception {
        //ARRANGE
        String userIdToUpdate = "3";
        BidList bidListToAdd = new BidList("account Updated test","type Updated test",25.0);
        bidListToAdd.setId(Integer.parseInt(userIdToUpdate));
        String bidToString = MAPPER.writeValueAsString(bidListToAdd);

        //ACT
            //first request that checks the request was properly redirected
        mvc.perform(patch("/bidList/update/{id}", userIdToUpdate).with(csrf()).content(bidToString)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(redirectedUrl("/bidList/list"));

            //second request must return updated BidList list
        MvcResult result = mvc.perform(get("/bidList/list"))
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();
        ArrayList<BidList> expectedUpdatedBidList = (ArrayList) resultModelAndView.getModel().get("bidLists");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("bidList/list");
        assertThat(expectedUpdatedBidList.size()).isEqualTo(3);
        assertThat(expectedUpdatedBidList.get(2).getType()).isEqualTo("type Updated test");
        assertThat(expectedUpdatedBidList.get(2).getBidQuantity()).isEqualTo(25.0);
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void bidListDelete_shouldReturnUpdatedListMinusDeletedBidList() throws Exception {
        //ARRANGE
        String userIdToDelete = "1";

        //ACT
            //first request that checks the request was properly redirected
        mvc.perform(delete("/bidList/delete/{id}", userIdToDelete).with(csrf()))
                .andExpect(redirectedUrl("/bidList/list"));;

            //second request must return updated BidList list minus deleted BidList
        MvcResult result = mvc.perform(get("/bidList/list"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        ArrayList<BidList> expectedUpdatedBidList = (ArrayList) resultModelAndView.getModel().get("bidLists");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("bidList/list");
        assertThat(expectedUpdatedBidList.size()).isEqualTo(2);
        assertThat(expectedUpdatedBidList.get(1).getType()).isEqualTo("type_2");
    }
}
