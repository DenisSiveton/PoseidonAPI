package com.nnk.springboot.integration_test;

import com.nnk.springboot.controllers.RatingController;
import com.nnk.springboot.domain.Rating;
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
public class RatingControllerITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RatingController ratingController;

    @BeforeEach
    public void setup_data(){

    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void homeRating_ShouldReturnRatingList() throws Exception {
        MvcResult result = mvc.perform(get("/rating/list"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        assertThat(resultModelAndView).isNotNull();
        List<Rating> expectedRating = (List) resultModelAndView.getModel().get("ratings");

        assertThat(resultModelAndView.getViewName()).isEqualTo("rating/list");
        assertThat(expectedRating.size()).isEqualTo(3);
        assertThat(expectedRating.get(2).getOrderNumber()).isEqualTo(3);
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void ratingAdd_shouldReturnCorrectURI() throws Exception {
        //ACT
        MvcResult result = mvc.perform(get("/rating/add"))
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();

        //ASSERT
        assertThat(resultModelAndView).isNotNull();
        assertThat(resultModelAndView.getViewName()).isEqualTo("rating/add");
    }

    @Test
    @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
    public void ratingValidate_ShouldReturnUpdatedRatingListWithNewRating() throws Exception {
        //ARRANGE
        Rating ratingToAdd = new Rating("moodys 4", "sAndPRating 4", "fitch 4",4);

        //ACT
            //first request that checks the redirect send the proper URI
        mvc.perform(post("/rating/validate").with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("moodysRating", ratingToAdd.getMoodysRating())
                .param("sandPRating", ratingToAdd.getSandPRating())
                .param("fitchRating", ratingToAdd.getFitchRating())
                .param("orderNumber", String.valueOf(ratingToAdd.getOrderNumber()))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(redirectedUrl("/rating/list"));

            //second request must return updated Rating list plus added Rating
        MvcResult result = mvc.perform(get("/rating/list"))
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();
        assertThat(resultModelAndView).isNotNull();
        List<Rating> expectedUpdatedRatingList = (List) resultModelAndView.getModel().get("ratings");

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
        assertThat(resultModelAndView).isNotNull();
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

        //ACT
            //first request that checks the request was properly redirected
        mvc.perform(post("/rating/update/{id}", ratingIdToUpdate).with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("id", String.valueOf(ratingToUpdate.getId()))
                .param("moodysRating", ratingToUpdate.getMoodysRating())
                .param("sandPRating", ratingToUpdate.getSandPRating())
                .param("fitchRating", ratingToUpdate.getFitchRating())
                .param("orderNumber", String.valueOf(ratingToUpdate.getOrderNumber()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(redirectedUrl("/rating/list"));

            //second request must return updated Rating list
        MvcResult result = mvc.perform(get("/rating/list"))
                .andReturn();

        ModelAndView resultModelAndView = result.getModelAndView();
        assertThat(resultModelAndView).isNotNull();
        List<Rating> expectedUpdatedRatingList = (List<Rating>) resultModelAndView.getModel().get("ratings");

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
        mvc.perform(get("/rating/delete/{id}", ratingIdToDelete).with(csrf()))
                .andExpect(redirectedUrl("/rating/list"));

            //second request must return updated Rating list minus deleted Rating
        MvcResult result = mvc.perform(get("/rating/list"))
                .andReturn();
        ModelAndView resultModelAndView = result.getModelAndView();
        assertThat(resultModelAndView).isNotNull();
        List<Rating> expectedUpdatedRatingList = (List) resultModelAndView.getModel().get("ratings");

        //ASSERT
        assertThat(result.getModelAndView().getViewName()).isEqualTo("rating/list");
        assertThat(expectedUpdatedRatingList.size()).isEqualTo(2);
        assertThat(expectedUpdatedRatingList.get(1).getOrderNumber()).isEqualTo(3);
    }

    @Nested
    @Tag("ErrorHandlingCasesTests")
    @DisplayName("Cover and handle borderline cases when user sends partial and wrong data")
    class ErrorHandlingCasesTest{

        @Test
        @DisplayName("Given a Rating with missing information, when added, then user should be redirected to previous Form")
        @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
        public void ratingPointValidate_ShouldReturnCorrectURI_WhenErrorInRatingEntry() throws Exception {
            //ARRANGE
            Rating ratingToAdd = new Rating("moodys 4", "sAndPRating 4", "fitch 4",null);

            //ACT
            MvcResult result = mvc.perform(post("/rating/validate").with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .param("moodysRating", ratingToAdd.getMoodysRating())
                    .param("sandPRating", ratingToAdd.getSandPRating())
                    .param("fitchRating", ratingToAdd.getFitchRating())
                    .param("orderNumber", String.valueOf(ratingToAdd.getOrderNumber()))
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            ModelAndView resultModelAndView = result.getModelAndView();

            //ASSERT
            assertThat(resultModelAndView).isNotNull();
            assertThat(resultModelAndView.getViewName()).isEqualTo("rating/add");
        }

        @Test
        @DisplayName("Given a Rating Id that doesn't exist, when updated, then Exception should be emitted with correct message")
        @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
        public void ratingUpdate_ShouldEmitCorrectException_WhenWrongRatingId(){
            //ARRANGE
            String ratingIdToUpdate = "1000";

            //ACT
            assertThatThrownBy(() -> mvc.perform(get("/rating/update/{id}", ratingIdToUpdate)))
                    .hasCauseInstanceOf(IllegalArgumentException.class).hasMessageContaining("Invalid rating Id:" + ratingIdToUpdate);

        }

        @Test
        @DisplayName("Given a Rating with missing information, when updated, then user should be redirected to previous Form")
        @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
        public void ratingUpdate_ShouldReturnToForm_WhenWrongRatingSubmitted() throws Exception {
            //ARRANGE
            String ratingIdToUpdate = "3";
            Rating ratingToUpdate = new Rating("moodys 4", "sAndPRating 4", "fitch 4",null);
            ratingToUpdate.setId(Integer.parseInt(ratingIdToUpdate));

            //ACT
            MvcResult result = mvc.perform(post("/rating/update/{id}", ratingIdToUpdate).with(csrf())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .param("id", String.valueOf(ratingToUpdate.getId()))
                    .param("moodysRating", ratingToUpdate.getMoodysRating())
                    .param("sandPRating", ratingToUpdate.getSandPRating())
                    .param("fitchRating", ratingToUpdate.getFitchRating())
                    .param("orderNumber", String.valueOf(ratingToUpdate.getOrderNumber()))
                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            ModelAndView resultModelAndView = result.getModelAndView();


            //ASSERT
            assertThat(resultModelAndView).isNotNull();
            assertThat(resultModelAndView.getViewName()).isEqualTo("rating/update");
        }

        @Test
        @DisplayName("Given a Rating Id that doesn't exist, when deleted, then Exception should be emitted with correct message")
        @WithMockUser(username = "Usertest", password = "userMDP", roles = "USER")
        public void ratingDelete_ShouldEmitCorrectException_WhenWrongRatingId(){
            //ARRANGE
            String ratingIdToDelete = "1000";

            //ACT
            assertThatThrownBy(() -> mvc.perform(get("/rating/delete/{id}", ratingIdToDelete)))
                    .hasCauseInstanceOf(IllegalArgumentException.class).hasMessageContaining("Invalid rating Id:" + ratingIdToDelete);

        }
    }
}
