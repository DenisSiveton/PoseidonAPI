package com.nnk.springboot.unit_test.controllers;


import com.nnk.springboot.Application;
import com.nnk.springboot.controllers.TradeController;
import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;
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
public class TradeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TradeController tradeController;

    @MockBean
    private TradeRepository tradeRepository;

    static List<Trade> tradeList;

    @BeforeEach
    public void setUpData(){
        tradeList = new ArrayList<>();
        tradeList.add(new Trade("account1","type1",1.0));
        tradeList.add(new Trade("account2","type2",2.0));
    }

    @Test
    @WithMockUser()
    public void home_shouldReturnListOfTrade_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        when(tradeRepository.findAll()).thenReturn(tradeList);
        // ACT
        MvcResult result = mockMvc.perform(get("/trade/list"))
                .andExpect(status().isOk())
                .andReturn();

        //ASSERT
        assertThat(result.getModelAndView()).isNotNull();
        verify(tradeRepository, times(1)).findAll();
        List<Trade> expectedTrade = (List) result.getModelAndView().getModel().get("trades");
        assertThat(result.getModelAndView().getViewName()).isEqualTo("trade/list");
        assertThat(expectedTrade.size()).isEqualTo(2);
    }

    @Test
    @WithMockUser()
    public void addTradeForm_shouldReturnCorrectViewName() throws Exception {
        // ACT
        MvcResult result = mockMvc.perform(get("/trade/add"))
                .andExpect(status().isOk())
                .andReturn();

        //ASSERT
        assertThat(result.getModelAndView()).isNotNull();
        assertThat(result.getModelAndView().getViewName()).isEqualTo("trade/add");
    }

    @Test
    @WithMockUser()
    public void tradeValidate_ShouldReturnUpdatedTradeWithNewTrade_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        Trade tradeToAdd = new Trade("account3","type3",3.0);
        tradeList.add(tradeToAdd);
            //Mock called methods
        when(tradeRepository.save(any(Trade.class))).thenReturn(tradeToAdd);
        when(tradeRepository.findAll())
                .thenReturn(tradeList, tradeList);
        //ACT
        //first request that checks the redirect send the proper URI
        mockMvc.perform(post("/trade/validate").with(csrf())
               .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                   .param("account", tradeToAdd.getAccount())
                   .param("type", tradeToAdd.getType())
                   .param("buyQuantity", String.valueOf(tradeToAdd.getBuyQuantity()))
                   .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/trade/list"));

        //second request after redirection must return updated trade list plus added Trade
        MvcResult result = mockMvc.perform(get("/trade/list"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();
        List<Trade> expectedUpdatedTrade = (List) resultModelAndView.getModel().get("trades");
        assertThat(resultModelAndView.getViewName()).isEqualTo("trade/list");
        assertThat(expectedUpdatedTrade.size()).isEqualTo(3);
        assertThat(expectedUpdatedTrade.get(2).getBuyQuantity()).isEqualTo(tradeToAdd.getBuyQuantity());

            // Mocked calls
        verify(tradeRepository, times(1)).save(any(Trade.class));
        verify(tradeRepository, times(2)).findAll();
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void tradeUpdate_ShouldReturnFormWithTradeInfoForUpdate_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        String tradeIdToUpdate = "1";

        when(tradeRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(tradeList.get(0)));

        //ACT
        MvcResult result = mockMvc.perform(get("/trade/update/{id}", tradeIdToUpdate)).andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        assertThat(resultModelAndView).isNotNull();
        Trade tradeToUpdate = (Trade) resultModelAndView.getModel().get("trade");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("trade/update");
        assertThat(tradeToUpdate.getType()).isEqualTo(tradeList.get(0).getType());
        verify(tradeRepository, times(1)).findById(1);
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void tradeUpdate_ShouldReturnUpdatedTrade_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        String userIdToUpdate = "1";
        Trade tradeToUpdate = new Trade("account1.1","type1.1",1.5);
        tradeToUpdate.setId(Integer.parseInt(userIdToUpdate));

        tradeList.remove(0);
        tradeList.add(0,tradeToUpdate);

        when(tradeRepository.save(any(Trade.class))).thenReturn(tradeToUpdate);
        when(tradeRepository.findAll()).thenReturn(tradeList, tradeList);

        //ACT
            //first request that checks the request was properly redirected
        mockMvc.perform(post("/trade/update/{id}", userIdToUpdate).with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .param("id", String.valueOf(tradeToUpdate.getId()))
                    .param("account", tradeToUpdate.getAccount())
                    .param("type", tradeToUpdate.getType())
                    .param("buyQuantity", String.valueOf(tradeToUpdate.getBuyQuantity()))
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/trade/list"));

            //second request must return updated Trade list
        MvcResult result = mockMvc.perform(get("/trade/list"))
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();

        List<Trade> expectedUpdatedTrade = (List) resultModelAndView.getModel().get("trades");

        assertThat(result.getModelAndView().getViewName()).isEqualTo("trade/list");
        assertThat(expectedUpdatedTrade.size()).isEqualTo(2);
        assertThat(expectedUpdatedTrade.get(0).getAccount()).isEqualTo("account1.1");
        assertThat(expectedUpdatedTrade.get(0).getType()).isEqualTo(tradeList.get(0).getType());

            // Mocked calls
        verify(tradeRepository, times(1)).save(any(Trade.class));
        verify(tradeRepository, times(2)).findAll();
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void tradeDelete_shouldReturnUpdatedListMinusDeletedTrade_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        String tradeIdToDelete = "1";

        when(tradeRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(tradeList.get(0)));
        doNothing().when(tradeRepository).delete(tradeList.get(0));
        Trade tradeDelete = tradeList.remove(0);
        when(tradeRepository.findAll()).thenReturn(tradeList, tradeList);

        //ACT
        //first request that checks the request was properly redirected
        mockMvc.perform(get("/trade/delete/{id}", tradeIdToDelete).with(csrf()))
                .andExpect(redirectedUrl("/trade/list"));

        //second request must return updated Trade list minus deleted Trade
        MvcResult result = mockMvc.perform(get("/trade/list"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        assertThat(resultModelAndView).isNotNull();
        List<Trade> expectedUpdatedTrade = (List) resultModelAndView.getModel().get("trades");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("trade/list");
        assertThat(expectedUpdatedTrade.size()).isEqualTo(1);
        assertThat(expectedUpdatedTrade.get(0).getType()).isEqualTo("type2");

        // Mocked calls
        verify(tradeRepository, times(1)).findById(1);
        verify(tradeRepository, times(1)).delete(tradeDelete);
        verify(tradeRepository, times(2)).findAll();
    }
}
