package com.nnk.springboot.controllers;

import com.nnk.springboot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * This class serves as a controller layer for login and error handling process.
 * @author M Siveton Denis
 * @version 1.0
 */
@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    /**
     * This method sends the user to the Application login page.
     *
     * @return Web UI container with its view set to "login".
     */
    @GetMapping("/login")
    public ModelAndView login(Model model) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("login");
        return mav;
    }

    /**
     * This method redirects the user to the Application login page after a failed attempt.
     * An error message will explain what happened.
     *
     * @param model Web UI container. Contains the error message when the login fails.
     * @return URI login. Show login page with the error message
     */
    @GetMapping("/login-error")
    public String failLogin(Model model){
        String errorMessage = "The credentials did not match any registered user in the database.";
        model.addAttribute("errorMessage", errorMessage);
        return "login";
    }

    /**
     * This method retrieves all the User and shows them as a list to the user.
     *
     * @return Web UI container with its view set to "user/list".
     */
    @GetMapping("/secure/article-details")
    public ModelAndView getAllUserArticles() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("users", userRepository.findAll());
        mav.setViewName("user/list");
        return mav;
    }

    /**
     * This method is called upon an error. It sends an error message to the user for clarification.
     *
     * @return Web UI container with its view set to "403".
     */
    @GetMapping("/error")
    public ModelAndView error() {
        ModelAndView mav = new ModelAndView();
        String errorMessage= "You are not authorized for the requested data.";
        mav.addObject("errorMsg", errorMessage);
        mav.setViewName("403");
        return mav;
    }
}
