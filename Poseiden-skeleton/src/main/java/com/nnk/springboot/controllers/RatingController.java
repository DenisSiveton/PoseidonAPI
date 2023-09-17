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

@Controller
public class RatingController {
    // TODO: Inject Rating service --> DONE
    @Autowired
    private RatingRepository ratingRepository;

    @RequestMapping("/rating/list")
    public String home(Model model)
    {
        // TODO: find all Rating, add to model --> DONE
        model.addAttribute("ratings", ratingRepository.findAll());
        return "rating/list";
    }

    @GetMapping("/rating/add")
    public String addRatingForm(Rating rating) {
        return "rating/add";
    }

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

    @GetMapping("/rating/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        // TODO: get Rating by Id and to model then show to the form --> DONE
        Rating rating = ratingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid rating Id:" + id));
        model.addAttribute("rating", rating);
        return "rating/update";
    }

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

    @DeleteMapping("/rating/delete/{id}")
    public String deleteRating(@PathVariable("id") Integer id, Model model) {
        // TODO: Find Rating by Id and delete the Rating, return to Rating list --> DONE
        Rating rating = ratingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid rating Id:" + id));
        ratingRepository.delete(rating);
        model.addAttribute("ratings", ratingRepository.findAll());
        return "redirect:/rating/list";
    }
}
