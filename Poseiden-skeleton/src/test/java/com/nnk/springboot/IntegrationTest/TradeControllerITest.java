package com.nnk.springboot.IntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnk.springboot.controllers.BidListController;
import com.nnk.springboot.controllers.TradeController;
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.domain.Trade;
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

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;


@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan("com.nnk.springboot.controllers")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TradeControllerITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TradeController tradeController;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeEach
    public void setup_data(){

    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void homeTrade_ShouldReturnBidList() throws Exception {
        MvcResult result = mvc.perform(get("/trade/list"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        ArrayList<Trade> expectedTradeList = (ArrayList) resultModelAndView.getModel().get("trades");

        assertThat(resultModelAndView.getViewName().equals("trade/list"));
        assertThat(expectedTradeList.size()).isEqualTo(3);
        assertThat(expectedTradeList.get(2).getType()).isEqualTo("type_2");
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void tradeAdd_shouldReturnCorrectURI() throws Exception {
        MvcResult result = mvc.perform(get("/trade/add"))
                .andReturn();
        assertThat(result.getModelAndView().getViewName()).isEqualTo("trade/add");
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void tradeValidate_ShouldReturnUpdatedTradeListWithNewTrade() throws Exception {
        //ARRANGE
        Trade tradeToAdd = new Trade("account 1 Test", "type 1 Test", 34.0);

        String tradeToString = MAPPER.writeValueAsString(tradeToAdd);

        //ACT
            //first request that checks the redirect send the proper URI
        mvc.perform(post("/trade/validate").with(csrf()).content(tradeToString)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(redirectedUrl("/trade/list"));

            //second request must return updated trade list plus added Trade
        MvcResult result = mvc.perform(get("/trade/list"))
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();
        ArrayList<Trade> expectedUpdatedTradeList = (ArrayList) resultModelAndView.getModel().get("trades");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("trade/list");
        assertThat(expectedUpdatedTradeList.size()).isEqualTo(4);
        assertThat(expectedUpdatedTradeList.get(3).getType()).isEqualTo(tradeToAdd.getType());
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void tradeUpdate_ShouldReturnFormWithTradeInfoForUpdate() throws Exception {
        //ARRANGE
        String tradeIdToDelete = "1";
    
        //ACT
        MvcResult result = mvc.perform(get("/trade/update/{id}", tradeIdToDelete)).andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        Trade tradeToUpdate = (Trade) resultModelAndView.getModel().get("trade");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("trade/update");
        assertThat(tradeToUpdate.getType()).isEqualTo("type_1");
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void tradeUpdate_ShouldReturnUpdatedTradeList() throws Exception {
        //ARRANGE
        String tradeIdToUpdate = "3";
        Trade tradeToAdd = new Trade("account updated", "type updated", 34.0);
        tradeToAdd.setId(Integer.parseInt(tradeIdToUpdate));
        String tardeToString = MAPPER.writeValueAsString(tradeToAdd);

        //ACT
            //first request that checks the request was properly redirected
        mvc.perform(post("/trade/update/{id}", tradeIdToUpdate).with(csrf()).content(tardeToString)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(redirectedUrl("/trade/list"));

            //second request must return updated Trade list
        MvcResult result = mvc.perform(get("/trade/list"))
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();
        ArrayList<Trade> expectedUpdatedTradeList = (ArrayList) resultModelAndView.getModel().get("trades");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("trade/list");
        assertThat(expectedUpdatedTradeList.size()).isEqualTo(3);
        assertThat(expectedUpdatedTradeList.get(2).getType()).isEqualTo("type updated");
        assertThat(expectedUpdatedTradeList.get(2).getBuyQuantity()).isEqualTo(34.0);
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void tradeDelete_shouldReturnUpdatedListMinusDeletedTrade() throws Exception {
        //ARRANGE
        String tradeIdToDelete = "1";

        //ACT
            //first request that checks the request was properly redirected
        mvc.perform(get("/trade/delete/{id}", tradeIdToDelete).with(csrf()))
                .andExpect(redirectedUrl("/trade/list"));;

            //second request must return updated Trade list minus deleted Trade
        MvcResult result = mvc.perform(get("/trade/list"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        ArrayList<Trade> expectedUpdatedTrade = (ArrayList) resultModelAndView.getModel().get("trades");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("trade/list");
        assertThat(expectedUpdatedTrade.size()).isEqualTo(2);
        assertThat(expectedUpdatedTrade.get(1).getType()).isEqualTo("type_2");
    }

    @Nested
    @Tag("ErrorHandlingCasesTests")
    @DisplayName("Cover and handle borderline cases when user sends partial and wrong data")
    class ErrorHandlingCasesTest{

        @Test
        @DisplayName("Given a Trade with missing information, when added, then user should be redirected to previous Form")
        @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
        public void tradeValidate_ShouldReturnCorrectURI_WhenErrorInTradeEntry() throws Exception {
            //ARRANGE
            Trade tradeToAdd = new Trade("", "type updated", 34.0);

            String tradeToString = MAPPER.writeValueAsString(tradeToAdd);

            //ACT
            MvcResult result = mvc.perform(post("/trade/validate").with(csrf()).content(tradeToString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            ModelAndView resultModelAndView = result.getModelAndView();

            //ASSERT
            assertThat(resultModelAndView.getViewName()).isEqualTo("trade/add");
        }

        @Test
        @DisplayName("Given a Trade Id that doesn't exist, when updated, then Exception should be emitted with correct message")
        @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
        public void tradeUpdate_ShouldEmitCorrectException_WhenWrongTradeId() throws Exception {
            //ARRANGE
            String tradeIdToUpdate = "1000";

            //ACT
            assertThatThrownBy(() -> mvc.perform(get("/trade/update/{id}", tradeIdToUpdate)))
                    .hasCauseInstanceOf(IllegalArgumentException.class).hasMessageContaining("Invalid trade Id:" + tradeIdToUpdate);

        }

        @Test
        @DisplayName("Given a Trade with missing information, when updated, then user should be redirected to previous Form")
        @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
        public void tradeUpdate_ShouldReturnToForm_WhenWrongTradeSubmitted() throws Exception {
            //ARRANGE
            String tradeIdToUpdate = "3";
            Trade tradeToAdd = new Trade("", "type updated", 34.0);
            tradeToAdd.setId(Integer.parseInt(tradeIdToUpdate));
            String tradeToAddToString = MAPPER.writeValueAsString(tradeToAdd);

            //ACT
            MvcResult result = mvc.perform(post("/trade/update/{id}", tradeIdToUpdate).with(csrf()).content(tradeToAddToString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            ModelAndView resultModelAndView = result.getModelAndView();

            //ASSERT
            assertThat(result.getModelAndView().getViewName()).isEqualTo("trade/update");
        }

        @Test
        @DisplayName("Given a Trade Id that doesn't exist, when deleted, then Exception should be emitted with correct message")
        @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
        public void tradeDelete_ShouldEmitCorrectException_WhenWrongTradeId() throws Exception {
            //ARRANGE
            String tradeIdToDelete = "1000";

            //ACT
            assertThatThrownBy(() -> mvc.perform(get("/trade/delete/{id}", tradeIdToDelete)))
                    .hasCauseInstanceOf(IllegalArgumentException.class).hasMessageContaining("Invalid trade Id:" + tradeIdToDelete);

        }
    }
}
