package com.nnk.springboot.IntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnk.springboot.controllers.BidListController;
import com.nnk.springboot.controllers.CurveController;
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.domain.CurvePoint;
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
public class CurvePointControllerITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CurveController curveController;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeEach
    public void setup_data(){

    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void homeCurvePoint_ShouldReturnCurvePointList() throws Exception {
        MvcResult result = mvc.perform(get("/curvePoint/list"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        ArrayList<CurvePoint> expectedCurvePoint = (ArrayList) resultModelAndView.getModel().get("curvePoints");

        assertThat(resultModelAndView.getViewName().equals("curvePoint/list"));
        assertThat(expectedCurvePoint.size()).isEqualTo(3);
        assertThat(expectedCurvePoint.get(2).getCurveId()).isEqualTo(2);
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void curvePointAdd_shouldReturnCorrectURI() throws Exception {
        MvcResult result = mvc.perform(get("/curvePoint/add"))
                .andReturn();
        assertThat(result.getModelAndView().getViewName()).isEqualTo("curvePoint/add");
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void curvePointValidate_ShouldReturnUpdatedCurvePointListWithNewCurvePointList() throws Exception {
        //ARRANGE
        CurvePoint curvePointToAdd = new CurvePoint(1,1.0,20.0);

        String curvePointToString = MAPPER.writeValueAsString(curvePointToAdd);

        //ACT
            //first request that checks the redirect send the proper URI
        mvc.perform(post("/curvePoint/validate").with(csrf()).content(curvePointToString)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(redirectedUrl("/curvePoint/list"));

            //second request must return updated bidList list plus added BidList
        MvcResult result = mvc.perform(get("/curvePoint/list"))
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();
        ArrayList<CurvePoint> expectedUpdatedCurvePoint = (ArrayList) resultModelAndView.getModel().get("curvePoints");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("curvePoint/list");
        assertThat(expectedUpdatedCurvePoint.size()).isEqualTo(4);
        assertThat(expectedUpdatedCurvePoint.get(3).getTerm()).isEqualTo(curvePointToAdd.getTerm());
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void curvePointUpdate_ShouldReturnFormWithCurvePointInfoForUpdate() throws Exception {
        //ARRANGE
        String curvePointIdToUpdate = "1";

        //ACT
        MvcResult result = mvc.perform(get("/curvePoint/update/{id}", curvePointIdToUpdate)).andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        CurvePoint curvePointToUpdate = (CurvePoint) resultModelAndView.getModel().get("curvePoint");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("curvePoint/update");
        assertThat(curvePointToUpdate.getValue()).isEqualTo(8.3);
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void curvePointUpdate_ShouldSaveNewCurvePointAndReturnUpdatedCurvePointList() throws Exception {
        //ARRANGE
        String curvePointIdToUpdate = "3";
        CurvePoint curvePointToUpdateWithUpdatedInfo = new CurvePoint(1,1.0,20.0);
        curvePointToUpdateWithUpdatedInfo.setId(Integer.parseInt(curvePointIdToUpdate));
        String curvePointToString = MAPPER.writeValueAsString(curvePointToUpdateWithUpdatedInfo);

        //ACT
            //first request that checks the request was properly redirected
        mvc.perform(post("/curvePoint/update/{id}", curvePointIdToUpdate).with(csrf()).content(curvePointToString)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(redirectedUrl("/curvePoint/list"));

            //second request must return updated BidList list
        MvcResult result = mvc.perform(get("/curvePoint/list"))
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();
        ArrayList<CurvePoint> expectedUpdatedCurvePoint = (ArrayList) resultModelAndView.getModel().get("curvePoints");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("curvePoint/list");
        assertThat(expectedUpdatedCurvePoint.size()).isEqualTo(3);
        assertThat(expectedUpdatedCurvePoint.get(2).getCurveId()).isEqualTo(curvePointToUpdateWithUpdatedInfo.getCurveId());
        assertThat(expectedUpdatedCurvePoint.get(2).getValue()).isEqualTo(curvePointToUpdateWithUpdatedInfo.getValue());
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void curvePointDelete_shouldReturnUpdatedListMinusDeletedCurvePoint() throws Exception {
        //ARRANGE
        String curvePointIdToDelete = "1";

        //ACT
            //first request that checks the request was properly redirected
        mvc.perform(get("/curvePoint/delete/{id}", curvePointIdToDelete).with(csrf()))
                .andExpect(redirectedUrl("/curvePoint/list"));;

            //second request must return updated BidList list minus deleted BidList
        MvcResult result = mvc.perform(get("/curvePoint/list"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        ArrayList<CurvePoint> expectedUpdatedCurvePointList = (ArrayList) resultModelAndView.getModel().get("curvePoints");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("curvePoint/list");
        assertThat(expectedUpdatedCurvePointList.size()).isEqualTo(2);
        assertThat(expectedUpdatedCurvePointList.get(0).getValue()).isEqualTo(1.0);
    }

    @Nested
    @Tag("ErrorHandlingCasesTests")
    @DisplayName("Cover and handle borderline cases when user sends partial and wrong data")
    class ErrorHandlingCasesTest{

        @Test
        @DisplayName("Given a CurvePoint with missing information, when added, then user should be redirected to previous Form")
        @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
        public void curvePointValidate_ShouldReturnCorrectURI_WhenErrorInCurvePointEntry() throws Exception {
            //ARRANGE
            CurvePoint curvePointToAdd = new CurvePoint(null,2.0,20.0);

            String curvePointToString = MAPPER.writeValueAsString(curvePointToAdd);

            //ACT
            MvcResult result = mvc.perform(post("/curvePoint/validate").with(csrf()).content(curvePointToString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            ModelAndView resultModelAndView = result.getModelAndView();

            //ASSERT
            assertThat(resultModelAndView.getViewName()).isEqualTo("curvePoint/add");
        }

        @Test
        @DisplayName("Given a CurvePoint Id that doesn't exist, when updated, then Exception should be emitted with correct message")
        @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
        public void curvePointUpdate_ShouldEmitCorrectException_WhenWrongCurvePointId() throws Exception {
            //ARRANGE
            String curvePointIdToUpdate = "1000";

            //ACT
            assertThatThrownBy(() -> mvc.perform(get("/curvePoint/update/{id}", curvePointIdToUpdate)))
                    .hasCauseInstanceOf(IllegalArgumentException.class).hasMessageContaining("Invalid curvePoint Id:" + curvePointIdToUpdate);

        }

        @Test
        @DisplayName("Given a CurvePoint with missing information, when updated, then user should be redirected to previous Form")
        @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
        public void curvePointUpdate_ShouldReturnToForm_WhenWrongCurvePointSubmitted() throws Exception {
            //ARRANGE
            String curvePointIdToUpdate = "3";
            CurvePoint curvePointToAdd = new CurvePoint(null,2.0,20.0);
            curvePointToAdd.setId(Integer.parseInt(curvePointIdToUpdate));
            String curvePointToString = MAPPER.writeValueAsString(curvePointToAdd);

            //ACT
            //first request that checks the request was properly redirected
            MvcResult result = mvc.perform(post("/curvePoint/update/{id}", curvePointIdToUpdate).with(csrf()).content(curvePointToString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            ModelAndView resultModelAndView = result.getModelAndView();

            //ASSERT
            assertThat(result.getModelAndView().getViewName()).isEqualTo("curvePoint/update");
        }

        @Test
        @DisplayName("Given a CurvePoint Id that doesn't exist, when deleted, then Exception should be emitted with correct message")
        @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
        public void curvePointDelete_ShouldEmitCorrectException_WhenWrongCurvePointId() throws Exception {
            //ARRANGE
            String curvePointIdToDelete = "1000";

            //ACT
            assertThatThrownBy(() -> mvc.perform(get("/curvePoint/delete/{id}", curvePointIdToDelete)))
                    .hasCauseInstanceOf(IllegalArgumentException.class).hasMessageContaining("Invalid curvePoint Id:" + curvePointIdToDelete);

        }
    }
}
