package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * This class serves as a controller layer for the User entity.
 * It will use the CRUD methods for data persistence
 * @author M Siveton Denis
 * @version 1.0
 */
@Controller
public class UserController {
    @Autowired
    private UserRepository userRepository;

    /**
     * This methods retrieves all Users from database and list them for the user.
     *
     * @param model Web UI container. Contains all the Users
     * @return URI user/list. Show table with all Users
     */
    @RequestMapping("/user/list")
    public String home(Model model)
    {
        model.addAttribute("users", userRepository.findAll());
        return "user/list";
    }

    /**
     * This method returns a form to add a User.
     *
     * @param user Entity that will be generated with the form then added to the Database
     * @return URI user/add. Show form with input for new User
     */
    @GetMapping("/user/add")
    public String addUser(User user) {
        return "user/add";
    }

    /**
     * This method checks if the data from the form are consistent and valid for a new User.
     * If the checks pass then add the new User to the database.
     *      It then retrieves all Users from database and list them in the UI for the user.
     * If the checks fail, the user is redirected to the form for a second attempt with an error message
     *      explaining why the request failed.
     *
     * @param user Entity constructed from the form. It will be added to the database
     * @param result Form result. May contain errors if data don't comply
     * @param model Web UI container. Contains all the Users
     * @return URI user/list. Show table with updated Users
     * @return In case of error : URI user/add. Returns to the form for a second attempt
     */
    @PostMapping("/user/validate")
    public String validate(@Valid @RequestBody User user, BindingResult result, Model model) {
        if (!result.hasErrors()) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPassword(encoder.encode(user.getPassword()));
            userRepository.save(user);
            model.addAttribute("users", userRepository.findAll());
            return "redirect:/user/list";
        }
        return "user/add";
    }

    /**
     * This method retrieves the data of the User from the database.
     * It then generates a form filled with the data for modification.
     *
     * @param id Id of the User the user wants to update
     * @param model Web UI container. Contains the data of the desired User
     * @return URI user/update. Show form with input filled with User's data
     */
    @GetMapping("/user/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        user.setPassword("");
        model.addAttribute("user", user);
        return "user/update";
    }

    /**
     * This method checks if the data from the form are consistent and valid to update the User.
     * If the checks pass then it updates the User into the Database using the updated data from the form.
     *      It then retrieves all Users from database and list them in the UI for the user.
     * If the checks fail, the user is redirected to the form for a second attempt with an error message
     *      explaining why the request failed.
     *
     * @param id Id of the User the user wants to update
     * @param user Entity constructed from the form. It will update the existing User in the Database
     * @param result Form result. May contain errors if data don't comply
     * @param model Web UI container. Contains all the Users
     * @return URI user/list. Show table with updated Users
     * @return In case of error : URI user/update. Returns to the form for a second attempt
     */
    @PatchMapping("/user/update/{id}")
    public String updateUser(@PathVariable("id") Integer id, @Valid @RequestBody User user,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "user/update";
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        user.setId(id);
        userRepository.save(user);
        model.addAttribute("users", userRepository.findAll());
        return "redirect:/user/list";
    }

    /**
     * This method deletes a User from the Database.
     * It then retrieves all Users from database and list them in the UI for the user.
     *
     * @param id Id of the User the user wants to delete
     * @param model Web UI container. Contains all the remaining Users
     * @return URI user/list. Show table with updated Users
     */
    @DeleteMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        userRepository.delete(user);
        model.addAttribute("users", userRepository.findAll());
        return "redirect:/user/list";
    }
}
