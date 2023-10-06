package com.nnk.springboot.unit_test.controllers;


import com.nnk.springboot.Application;
import com.nnk.springboot.controllers.BidListController;
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;

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
public class BidListControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BidListController bidListController;

    @MockBean
    private BidListRepository bidListRepository;

    static List<BidList> bidListList;

    @BeforeEach
    public void setUpData(){
        bidListList = new ArrayList<>();
        bidListList.add(new BidList("account1","type1",1.0));
        bidListList.add(new BidList("account2","type2",2.0));
    }

    @Test
    @WithMockUser()
    public void home_shouldReturnListOfBidList_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        when(bidListRepository.findAll()).thenReturn(bidListList);
        // ACT
        MvcResult result = mockMvc.perform(get("/bidList/list"))
                .andExpect(status().isOk())
                .andReturn();

        //ASSERT
        assertThat(result.getModelAndView()).isNotNull();
        verify(bidListRepository, times(1)).findAll();
        List<BidList> expectedBidList = (List) result.getModelAndView().getModel().get("bidLists");
        assertThat(result.getModelAndView().getViewName()).isEqualTo("bidList/list");
        assertThat(expectedBidList.size()).isEqualTo(2);
    }

    @Test
    @WithMockUser()
    public void addBidForm_shouldReturnCorrectViewName() throws Exception {
        // ACT
        MvcResult result = mockMvc.perform(get("/bidList/add"))
                .andExpect(status().isOk())
                .andReturn();

        //ASSERT
        assertThat(result.getModelAndView()).isNotNull();
        assertThat(result.getModelAndView().getViewName()).isEqualTo("bidList/add");
    }

    @Test
    @WithMockUser()
    public void bidListValidate_ShouldReturnUpdatedBidListWithNewBidList() throws Exception {
        //ARRANGE
        BidList bidListToAdd = new BidList("account_test","type_test",20.0);
        bidListList.add(bidListToAdd);
            //Mock called methods
        when(bidListRepository.save(any(BidList.class))).thenReturn(bidListToAdd);
        when(bidListRepository.findAll())
                .thenReturn(bidListList, bidListList);
        //ACT
        //first request that checks the redirect send the proper URI
        mockMvc.perform(post("/bidList/validate").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("account", bidListToAdd.getAccount())
                .param("type", bidListToAdd.getType())
                .param("bidQuantity", String.valueOf(bidListToAdd.getBidQuantity()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list"));

        //second request after redirection must return updated bidList list plus added BidList
        MvcResult result = mockMvc.perform(get("/bidList/list"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();
        List<BidList> expectedUpdatedBidList = (List) resultModelAndView.getModel().get("bidLists");
        assertThat(resultModelAndView.getViewName()).isEqualTo("bidList/list");
        assertThat(expectedUpdatedBidList.size()).isEqualTo(3);
        assertThat(expectedUpdatedBidList.get(2).getBidQuantity()).isEqualTo(bidListToAdd.getBidQuantity());

            // Mocked calls
        verify(bidListRepository, times(1)).save(any(BidList.class));
        verify(bidListRepository, times(2)).findAll();
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void bidListUpdate_ShouldReturnFormWithBidListInfoForUpdate() throws Exception {
        //ARRANGE
        String bidListIdToUpdate = "1";

        when(bidListRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(bidListList.get(0)));

        //ACT
        MvcResult result = mockMvc.perform(get("/bidList/update/{id}", bidListIdToUpdate)).andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        assertThat(resultModelAndView).isNotNull();
        BidList bidListToUpdate = (BidList) resultModelAndView.getModel().get("bidList");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("bidList/update");
        assertThat(bidListToUpdate.getType()).isEqualTo("type1");
        verify(bidListRepository, times(1)).findById(1);
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void bidListUpdate_ShouldReturnUpdatedBidList() throws Exception {
        //ARRANGE
        String userIdToUpdate = "2";
        BidList bidListToUpdate = new BidList("account Updated test","type Updated test",25.0);
        bidListToUpdate.setId(Integer.parseInt(userIdToUpdate));

        bidListList.remove(0);
        bidListList.add(0,bidListToUpdate);

        when(bidListRepository.save(any(BidList.class))).thenReturn(bidListToUpdate);
        when(bidListRepository.findAll()).thenReturn(bidListList, bidListList);

        //ACT
            //first request that checks the request was properly redirected
        mockMvc.perform(post("/bidList/update/{id}", userIdToUpdate).with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("id", String.valueOf(bidListToUpdate.getId()))
                .param("account", bidListToUpdate.getAccount())
                .param("type", bidListToUpdate.getType())
                .param("bidQuantity", String.valueOf(bidListToUpdate.getBidQuantity()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bidList/list"));

            //second request must return updated BidList list
        MvcResult result = mockMvc.perform(get("/bidList/list"))
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();

        List<BidList> expectedUpdatedBidList = (List) resultModelAndView.getModel().get("bidLists");

        assertThat(result.getModelAndView().getViewName()).isEqualTo("bidList/list");
        assertThat(expectedUpdatedBidList.size()).isEqualTo(2);
        assertThat(expectedUpdatedBidList.get(0).getType()).isEqualTo("type Updated test");
        assertThat(expectedUpdatedBidList.get(0).getBidQuantity()).isEqualTo(25.0);

            // Mocked calls
        verify(bidListRepository, times(1)).save(any(BidList.class));
        verify(bidListRepository, times(2)).findAll();
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void bidListDelete_shouldReturnUpdatedListMinusDeletedBidList() throws Exception {
        //ARRANGE
        String userIdToDelete = "1";

        when(bidListRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(bidListList.get(0)));
        doNothing().when(bidListRepository).delete(bidListList.get(0));
        bidListList.remove(0);
        when(bidListRepository.findAll()).thenReturn(bidListList, bidListList);

        //ACT
        //first request that checks the request was properly redirected
        mockMvc.perform(get("/bidList/delete/{id}", userIdToDelete).with(csrf()))
                .andExpect(redirectedUrl("/bidList/list"));

        //second request must return updated BidList list minus deleted BidList
        MvcResult result = mockMvc.perform(get("/bidList/list"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        assertThat(resultModelAndView).isNotNull();
        List<BidList> expectedUpdatedBidList = (List) resultModelAndView.getModel().get("bidLists");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("bidList/list");
        assertThat(expectedUpdatedBidList.size()).isEqualTo(1);
        assertThat(expectedUpdatedBidList.get(0).getType()).isEqualTo("type2");
    }
}
