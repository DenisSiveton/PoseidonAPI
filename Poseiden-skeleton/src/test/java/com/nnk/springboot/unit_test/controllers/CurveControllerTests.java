package com.nnk.springboot.unit_test.controllers;


import com.nnk.springboot.Application;
import com.nnk.springboot.controllers.CurveController;
import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;
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
public class CurveControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CurveController curveController;

    @MockBean
    private CurvePointRepository curvePointRepository;

    static List<CurvePoint> curvePointList;

    @BeforeEach
    public void setUpData(){
        curvePointList = new ArrayList<>();
        curvePointList.add(new CurvePoint(1,5.0,2.5));
        curvePointList.add(new CurvePoint(2,10.0,25.0));
    }

    @Test
    @WithMockUser()
    public void home_shouldReturnListOfCurvePoint_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        when(curvePointRepository.findAll()).thenReturn(curvePointList);
        // ACT
        MvcResult result = mockMvc.perform(get("/curvePoint/list"))
                .andExpect(status().isOk())
                .andReturn();

        //ASSERT
        assertThat(result.getModelAndView()).isNotNull();
        verify(curvePointRepository, times(1)).findAll();
        List<CurvePoint> expectedCurvePoint = (List) result.getModelAndView().getModel().get("curvePoints");
        assertThat(result.getModelAndView().getViewName()).isEqualTo("curvePoint/list");
        assertThat(expectedCurvePoint.size()).isEqualTo(2);
    }

    @Test
    @WithMockUser()
    public void addCurvePointForm_shouldReturnCorrectViewName() throws Exception {
        // ACT
        MvcResult result = mockMvc.perform(get("/curvePoint/add"))
                .andExpect(status().isOk())
                .andReturn();

        //ASSERT
        assertThat(result.getModelAndView()).isNotNull();
        assertThat(result.getModelAndView().getViewName()).isEqualTo("curvePoint/add");
    }

    @Test
    @WithMockUser()
    public void curvePointValidate_ShouldReturnUpdatedCurvePointWithNewCurvePoint_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        CurvePoint curvePointToAdd = new CurvePoint(3,1.5,20.0);
        curvePointList.add(curvePointToAdd);
            //Mock called methods
        when(curvePointRepository.save(any(CurvePoint.class))).thenReturn(curvePointToAdd);
        when(curvePointRepository.findAll())
                .thenReturn(curvePointList, curvePointList);
        //ACT
        //first request that checks the redirect send the proper URI
        mockMvc.perform(post("/curvePoint/validate").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("curveId", String.valueOf(curvePointToAdd.getCurveId()))
                .param("term", String.valueOf(curvePointToAdd.getTerm()))
                .param("value", String.valueOf(curvePointToAdd.getValue()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/curvePoint/list"));

        //second request after redirection must return updated curvePoint list plus added CurvePoint
        MvcResult result = mockMvc.perform(get("/curvePoint/list"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();
        List<CurvePoint> expectedUpdatedCurvePoint = (List) resultModelAndView.getModel().get("curvePoints");
        assertThat(resultModelAndView.getViewName()).isEqualTo("curvePoint/list");
        assertThat(expectedUpdatedCurvePoint.size()).isEqualTo(3);
        assertThat(expectedUpdatedCurvePoint.get(2).getValue()).isEqualTo(curvePointToAdd.getValue());

            // Mocked calls
        verify(curvePointRepository, times(1)).save(any(CurvePoint.class));
        verify(curvePointRepository, times(2)).findAll();
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void curvePointUpdate_ShouldReturnFormWithCurvePointInfoForUpdate_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        String curvePointIdToUpdate = "1";

        when(curvePointRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(curvePointList.get(0)));

        //ACT
        MvcResult result = mockMvc.perform(get("/curvePoint/update/{id}", curvePointIdToUpdate)).andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        assertThat(resultModelAndView).isNotNull();
        CurvePoint curvePointToUpdate = (CurvePoint) resultModelAndView.getModel().get("curvePoint");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("curvePoint/update");
        assertThat(curvePointToUpdate.getValue()).isEqualTo(2.5);
        verify(curvePointRepository, times(1)).findById(1);
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void curvePointUpdate_ShouldReturnUpdatedCurvePoint_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        String userIdToUpdate = "1";
        CurvePoint curvePointToUpdate = new CurvePoint(3,10.1,33.0);
        curvePointToUpdate.setId(Integer.parseInt(userIdToUpdate));

        curvePointList.remove(0);
        curvePointList.add(0,curvePointToUpdate);

        when(curvePointRepository.save(any(CurvePoint.class))).thenReturn(curvePointToUpdate);
        when(curvePointRepository.findAll()).thenReturn(curvePointList, curvePointList);

        //ACT
            //first request that checks the request was properly redirected
        mockMvc.perform(post("/curvePoint/update/{id}", userIdToUpdate).with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("id", String.valueOf(curvePointToUpdate.getId()))
                .param("curveId", String.valueOf(curvePointToUpdate.getCurveId()))
                .param("term", String.valueOf(curvePointToUpdate.getTerm()))
                .param("value", String.valueOf(curvePointToUpdate.getValue()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/curvePoint/list"));

            //second request must return updated CurvePoint list
        MvcResult result = mockMvc.perform(get("/curvePoint/list"))
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();

        List<CurvePoint> expectedUpdatedCurvePoint = (List) resultModelAndView.getModel().get("curvePoints");

        assertThat(result.getModelAndView().getViewName()).isEqualTo("curvePoint/list");
        assertThat(expectedUpdatedCurvePoint.size()).isEqualTo(2);
        assertThat(expectedUpdatedCurvePoint.get(0).getTerm()).isEqualTo(10.1);
        assertThat(expectedUpdatedCurvePoint.get(0).getValue()).isEqualTo(33.0);

            // Mocked calls
        verify(curvePointRepository, times(1)).save(any(CurvePoint.class));
        verify(curvePointRepository, times(2)).findAll();
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void curvePointDelete_shouldReturnUpdatedListMinusDeletedCurvePoint_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        String curvePointIdToDelete = "1";

        when(curvePointRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(curvePointList.get(0)));
        doNothing().when(curvePointRepository).delete(curvePointList.get(0));
        CurvePoint curvePointDelete = curvePointList.remove(0);
        when(curvePointRepository.findAll()).thenReturn(curvePointList, curvePointList);

        //ACT
        //first request that checks the request was properly redirected
        mockMvc.perform(get("/curvePoint/delete/{id}", curvePointIdToDelete).with(csrf()))
                .andExpect(redirectedUrl("/curvePoint/list"));

        //second request must return updated CurvePoint list minus deleted CurvePoint
        MvcResult result = mockMvc.perform(get("/curvePoint/list"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        assertThat(resultModelAndView).isNotNull();
        List<CurvePoint> expectedUpdatedCurvePoint = (List) resultModelAndView.getModel().get("curvePoints");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("curvePoint/list");
        assertThat(expectedUpdatedCurvePoint.size()).isEqualTo(1);
        assertThat(expectedUpdatedCurvePoint.get(0).getCurveId()).isEqualTo(2);

        // Mocked calls
        verify(curvePointRepository, times(1)).findById(1);
        verify(curvePointRepository, times(1)).delete(curvePointDelete);
        verify(curvePointRepository, times(2)).findAll();
    }
}
