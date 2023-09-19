package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * This class serves as a controller layer for the Rating entity.
 * It will use the CRUD methods for data persistence
 * @author M Siveton Denis
 * @version 1.0
 */
@Controller
public class RatingController {
    // TODO: Inject Rating service --> DONE
    @Autowired
    private RatingRepository ratingRepository;

    /**
     * This methods retrieves all Ratings from database and list them for the user.
     *
     * @param model Web UI container. Contains all the Ratings
     * @return URI rating/list. Show table with all Ratings
     */
    @RequestMapping("/rating/list")
    public String home(Model model)
    {
        // TODO: find all Rating, add to model --> DONE
        model.addAttribute("ratings", ratingRepository.findAll());
        return "rating/list";
    }

    /**
     * This method returns a form to add a Rating.
     *
     * @param rating Entity that will be generated with the form then added to the Database
     * @return URI rating/add. Show form with input for new Rating
     */
    @GetMapping("/rating/add")
    public String addRatingForm(Rating rating) {
        return "rating/add";
    }


    /**
     * This method checks if the data from the form are consistent and valid for a new Rating.
     * If the checks pass then add the new Rating to the database.
     *      It then retrieves all Ratings from database and list them in the UI for the user.
     * If the checks fail, the user is redirected to the form for a second attempt with an error message
     *      explaining why the request failed.
     *
     * @param rating Entity constructed from the form. It will be added to the database
     * @param result Form result. May contain errors if data don't comply
     * @param model Web UI container. Contains all the Ratings
     * @return URI rating/list. Show table with updated Ratings
     * @return In case of error : URI rating/add. Returns to the form for a second attempt
     */
    @PostMapping("/rating/validate")
    public String validate(@Valid @RequestBody Rating rating, BindingResult result, Model model) {
        // TODO: check data valid and save to db, after saving return Rating list --> DONE
        if (!result.hasErrors()) {
            ratingRepository.save(rating);
            model.addAttribute("ratings", ratingRepository.findAll());
            return "redirect:/rating/list";
        }
        return "rating/add";
    }

    /**
     * This method retrieves the data of the Rating form the database.
     * It then generates a form filled with the data for modification.
     *
     * @param id Id of the Rating the user wants to update
     * @param model Web UI container. Contains the data of the desired Rating
     * @return URI rating/add. Show form with input filled with Rating's data
     */
    @GetMapping("/rating/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        // TODO: get Rating by Id and to model then show to the form --> DONE
        Rating rating = ratingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid rating Id:" + id));
        model.addAttribute("rating", rating);
        return "rating/update";
    }

    /**
     * This method checks if the data from the form are consistent and valid to update the Rating.
     * If the checks pass then it updates the Rating into the Database using the updated data from the form.
     *      It then retrieves all Ratings from database and list them in the UI for the user.
     * If the checks fail, the user is redirected to the form for a second attempt with an error message
     *      explaining why the request failed.
     *
     * @param id Id of the Rating the user wants to update
     * @param rating Entity constructed from the form. It will update the existing Rating in the Database
     * @param result Form result. May contain errors if data don't comply
     * @param model Web UI container. Contains all the Ratings
     * @return URI rating/list. Show table with updated Ratings
     * @return In case of error : URI rating/update. Returns to the form for a second attempt
     */
    @PatchMapping("/rating/update/{id}")
    public String updateRating(@PathVariable("id") Integer id, @Valid @RequestBody Rating rating,
                             BindingResult result, Model model) {
        // TODO: check required fields, if valid call service to update Rating and return Rating list --> DONE
        if (result.hasErrors()) {
            return "rating/update";
        }
        ratingRepository.save(rating);
        model.addAttribute("ratings", ratingRepository.findAll());
        return "redirect:/rating/list";
    }

    /**
     * This method deletes a Rating from the Database.
     * It then retrieves all Ratings from database and list them in the UI for the user.
     *
     * @param id Id of the Rating the user wants to delete
     * @param model Web UI container. Contains all the remaining Ratings
     * @return URI rating/list. Show table with updated Ratings
     */
    @DeleteMapping("/rating/delete/{id}")
    public String deleteRating(@PathVariable("id") Integer id, Model model) {
        // TODO: Find Rating by Id and delete the Rating, return to Rating list --> DONE
        Rating rating = ratingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid rating Id:" + id));
        ratingRepository.delete(rating);
        model.addAttribute("ratings", ratingRepository.findAll());
        return "redirect:/rating/list";
    }
}
