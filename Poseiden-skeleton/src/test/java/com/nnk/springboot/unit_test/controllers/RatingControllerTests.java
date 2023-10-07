package com.nnk.springboot.unit_test.controllers;


import com.nnk.springboot.Application;
import com.nnk.springboot.controllers.CurveController;
import com.nnk.springboot.controllers.RatingController;
import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.RatingRepository;
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
public class RatingControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RatingController ratingController;

    @MockBean
    private RatingRepository ratingRepository;

    static List<Rating> ratingList;

    @BeforeEach
    public void setUpData(){
        ratingList = new ArrayList<>();
        ratingList.add(new Rating("moodys1","SPR1","fitch1", 1));
        ratingList.add(new Rating("moodys2","SPR2","fitch2", 2));
    }

    @Test
    @WithMockUser()
    public void home_shouldReturnListOfRating_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        when(ratingRepository.findAll()).thenReturn(ratingList);
        // ACT
        MvcResult result = mockMvc.perform(get("/rating/list"))
                .andExpect(status().isOk())
                .andReturn();

        //ASSERT
        assertThat(result.getModelAndView()).isNotNull();
        verify(ratingRepository, times(1)).findAll();
        List<Rating> expectedRating = (List) result.getModelAndView().getModel().get("ratings");
        assertThat(result.getModelAndView().getViewName()).isEqualTo("rating/list");
        assertThat(expectedRating.size()).isEqualTo(2);
    }

    @Test
    @WithMockUser()
    public void addRatingForm_shouldReturnCorrectViewName() throws Exception {
        // ACT
        MvcResult result = mockMvc.perform(get("/rating/add"))
                .andExpect(status().isOk())
                .andReturn();

        //ASSERT
        assertThat(result.getModelAndView()).isNotNull();
        assertThat(result.getModelAndView().getViewName()).isEqualTo("rating/add");
    }

    @Test
    @WithMockUser()
    public void ratingValidate_ShouldReturnUpdatedRatingWithNewRating_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        Rating ratingToAdd = new Rating("moodys3","SPR3","fitch3", 3);
        ratingList.add(ratingToAdd);
            //Mock called methods
        when(ratingRepository.save(any(Rating.class))).thenReturn(ratingToAdd);
        when(ratingRepository.findAll())
                .thenReturn(ratingList, ratingList);
        //ACT
        //first request that checks the redirect send the proper URI
        mockMvc.perform(post("/rating/validate").with(csrf())
               .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                   .param("moodysRating", ratingToAdd.getMoodysRating())
                   .param("sandPRating", ratingToAdd.getSandPRating())
                   .param("fitchRating", ratingToAdd.getFitchRating())
                   .param("orderNumber", String.valueOf(ratingToAdd.getOrderNumber()))
                   .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/rating/list"));

        //second request after redirection must return updated rating list plus added Rating
        MvcResult result = mockMvc.perform(get("/rating/list"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();
        List<Rating> expectedUpdatedRating = (List) resultModelAndView.getModel().get("ratings");
        assertThat(resultModelAndView.getViewName()).isEqualTo("rating/list");
        assertThat(expectedUpdatedRating.size()).isEqualTo(3);
        assertThat(expectedUpdatedRating.get(2).getOrderNumber()).isEqualTo(ratingToAdd.getOrderNumber());

            // Mocked calls
        verify(ratingRepository, times(1)).save(any(Rating.class));
        verify(ratingRepository, times(2)).findAll();
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void ratingUpdate_ShouldReturnFormWithRatingInfoForUpdate_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        String ratingIdToUpdate = "1";

        when(ratingRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(ratingList.get(0)));

        //ACT
        MvcResult result = mockMvc.perform(get("/rating/update/{id}", ratingIdToUpdate)).andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        assertThat(resultModelAndView).isNotNull();
        Rating ratingToUpdate = (Rating) resultModelAndView.getModel().get("rating");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("rating/update");
        assertThat(ratingToUpdate.getOrderNumber()).isEqualTo(ratingList.get(0).getOrderNumber());
        verify(ratingRepository, times(1)).findById(1);
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void ratingUpdate_ShouldReturnUpdatedRating_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        String userIdToUpdate = "1";
        Rating ratingToUpdate = new Rating("moodys3","SPR3","fitch3", 3);
        ratingToUpdate.setId(Integer.parseInt(userIdToUpdate));

        ratingList.remove(0);
        ratingList.add(0,ratingToUpdate);

        when(ratingRepository.save(any(Rating.class))).thenReturn(ratingToUpdate);
        when(ratingRepository.findAll()).thenReturn(ratingList, ratingList);

        //ACT
            //first request that checks the request was properly redirected
        mockMvc.perform(post("/rating/update/{id}", userIdToUpdate).with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .param("id", String.valueOf(ratingToUpdate.getId()))
                    .param("moodysRating", ratingToUpdate.getMoodysRating())
                    .param("sandPRating", ratingToUpdate.getSandPRating())
                    .param("fitchRating", ratingToUpdate.getFitchRating())
                    .param("orderNumber", String.valueOf(ratingToUpdate.getOrderNumber()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rating/list"));

            //second request must return updated Rating list
        MvcResult result = mockMvc.perform(get("/rating/list"))
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();

        List<Rating> expectedUpdatedRating = (List) resultModelAndView.getModel().get("ratings");

        assertThat(result.getModelAndView().getViewName()).isEqualTo("rating/list");
        assertThat(expectedUpdatedRating.size()).isEqualTo(2);
        assertThat(expectedUpdatedRating.get(0).getOrderNumber()).isEqualTo(3);
        assertThat(expectedUpdatedRating.get(0).getMoodysRating()).isEqualTo("moodys3");

            // Mocked calls
        verify(ratingRepository, times(1)).save(any(Rating.class));
        verify(ratingRepository, times(2)).findAll();
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void ratingDelete_shouldReturnUpdatedListMinusDeletedRating_andMockedMethodsShouldBeCalledTheRightAmountOfTime() throws Exception {
        //ARRANGE
        String ratingIdToDelete = "1";

        when(ratingRepository.findById(1)).thenReturn(java.util.Optional.ofNullable(ratingList.get(0)));
        doNothing().when(ratingRepository).delete(ratingList.get(0));
        Rating ratingDelete = ratingList.remove(0);
        when(ratingRepository.findAll()).thenReturn(ratingList, ratingList);

        //ACT
        //first request that checks the request was properly redirected
        mockMvc.perform(get("/rating/delete/{id}", ratingIdToDelete).with(csrf()))
                .andExpect(redirectedUrl("/rating/list"));

        //second request must return updated Rating list minus deleted Rating
        MvcResult result = mockMvc.perform(get("/rating/list"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        assertThat(resultModelAndView).isNotNull();
        List<Rating> expectedUpdatedRating = (List) resultModelAndView.getModel().get("ratings");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("rating/list");
        assertThat(expectedUpdatedRating.size()).isEqualTo(1);
        assertThat(expectedUpdatedRating.get(0).getMoodysRating()).isEqualTo("moodys2");

        // Mocked calls
        verify(ratingRepository, times(1)).findById(1);
        verify(ratingRepository, times(1)).delete(ratingDelete);
        verify(ratingRepository, times(2)).findAll();
    }
}
