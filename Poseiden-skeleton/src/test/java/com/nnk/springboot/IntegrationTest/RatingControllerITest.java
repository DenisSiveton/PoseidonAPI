package com.nnk.springboot.IntegrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nnk.springboot.controllers.BidListController;
import com.nnk.springboot.controllers.RatingController;
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.domain.Rating;
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
public class RatingControllerITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RatingController ratingController;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeEach
    public void setup_data(){

    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void homeRating_ShouldReturnRatingList() throws Exception {
        MvcResult result = mvc.perform(get("/rating/list"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        ArrayList<Rating> expectedRating = (ArrayList) resultModelAndView.getModel().get("ratings");

        assertThat(resultModelAndView.getViewName().equals("rating/list"));
        assertThat(expectedRating.size()).isEqualTo(3);
        assertThat(expectedRating.get(2).getOrderNumber()).isEqualTo(3);
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void ratingAdd_shouldReturnCorrectURI() throws Exception {
        MvcResult result = mvc.perform(get("/rating/add"))
                .andReturn();
        assertThat(result.getModelAndView().getViewName()).isEqualTo("rating/add");
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void ratingValidate_ShouldReturnUpdatedRatingListWithNewRating() throws Exception {
        //ARRANGE
        Rating ratingToAdd = new Rating("moodys 4", "sAndPRating 4", "fitch 4",4);

        String ratingToString = MAPPER.writeValueAsString(ratingToAdd);

        //ACT
            //first request that checks the redirect send the proper URI
        mvc.perform(post("/rating/validate").with(csrf()).content(ratingToString)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(redirectedUrl("/rating/list"));

            //second request must return updated Rating list plus added Rating
        MvcResult result = mvc.perform(get("/rating/list"))
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();
        ArrayList<Rating> expectedUpdatedRatingList = (ArrayList) resultModelAndView.getModel().get("ratings");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("rating/list");
        assertThat(expectedUpdatedRatingList.size()).isEqualTo(4);
        assertThat(expectedUpdatedRatingList.get(3).getOrderNumber()).isEqualTo(ratingToAdd.getOrderNumber());
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void bidListUpdate_ShouldReturnFormWithBidListInfoForUpdate() throws Exception {
        //ARRANGE
        String ratingIdToDelete = "1";

        //ACT
        MvcResult result = mvc.perform(get("/rating/update/{id}", ratingIdToDelete)).andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        Rating ratingToUpdate = (Rating) resultModelAndView.getModel().get("rating");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("rating/update");
        assertThat(ratingToUpdate.getOrderNumber()).isEqualTo(1);
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void bidListUpdate_ShouldReturnUpdatedBidList() throws Exception {
        //ARRANGE
        String ratingIdToUpdate = "3";
        Rating ratingToUpdate = new Rating("moodys updated", "sAndPRating updated", "fitch updated",30);
        ratingToUpdate.setId(Integer.parseInt(ratingIdToUpdate));
        String bidToString = MAPPER.writeValueAsString(ratingToUpdate);

        //ACT
            //first request that checks the request was properly redirected
        mvc.perform(patch("/rating/update/{id}", ratingIdToUpdate).with(csrf()).content(bidToString)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(redirectedUrl("/rating/list"));

            //second request must return updated Rating list
        MvcResult result = mvc.perform(get("/rating/list"))
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();
        ArrayList<Rating> expectedUpdatedRatingList = (ArrayList) resultModelAndView.getModel().get("ratings");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("rating/list");
        assertThat(expectedUpdatedRatingList.size()).isEqualTo(3);
        assertThat(expectedUpdatedRatingList.get(2).getSandPRating()).isEqualTo("sAndPRating updated");
        assertThat(expectedUpdatedRatingList.get(2).getOrderNumber()).isEqualTo(30);
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void bidListDelete_shouldReturnUpdatedListMinusDeletedBidList() throws Exception {
        //ARRANGE
        String ratingIdToDelete = "1";

        //ACT
            //first request that checks the request was properly redirected
        mvc.perform(delete("/rating/delete/{id}", ratingIdToDelete).with(csrf()))
                .andExpect(redirectedUrl("/rating/list"));;

            //second request must return updated Rating list minus deleted Rating
        MvcResult result = mvc.perform(get("/rating/list"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        ArrayList<Rating> expectedUpdatedRatingList = (ArrayList) resultModelAndView.getModel().get("ratings");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("rating/list");
        assertThat(expectedUpdatedRatingList.size()).isEqualTo(2);
        assertThat(expectedUpdatedRatingList.get(1).getOrderNumber()).isEqualTo(3);
    }
}
