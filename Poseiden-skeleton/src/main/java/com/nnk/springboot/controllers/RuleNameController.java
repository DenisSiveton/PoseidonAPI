package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RuleNameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * This class serves as a controller layer for the RuleName entity.
 * It will use the CRUD methods for data persistence
 * @author M Siveton Denis
 * @version 1.0
 */
@Controller
public class RuleNameController {
    // TODO: Inject RuleName service --> DONE
    @Autowired
    private RuleNameRepository ruleNameRepository;

    /**
     * This methods retrieves all RuleNames from database and list them for the user.
     *
     * @param model Web UI container. Contains all the RuleNames
     * @return URI ruleName/list. Show table with all RuleNames
     */
    @RequestMapping("/ruleName/list")
    public String home(Model model)
    {
        // TODO: find all RuleName, add to model --> DONE
        model.addAttribute("ruleNames", ruleNameRepository.findAll());
        return "ruleName/list";
    }

    /**
     * This method returns a form to add a RuleName.
     *
     * @param ruleName Entity that will be generated with the form then added to the Database
     * @return URI ruleName/add. Show form with input for new RuleName
     */
    @GetMapping("/ruleName/add")
    public String addRuleForm(RuleName ruleName) {
        return "ruleName/add";
    }

    /**
     * This method checks if the data from the form are consistent and valid for a new RuleName.
     * If the checks pass then add the new RuleName to the database.
     *      It then retrieves all RuleNames from database and list them in the UI for the user.
     * If the checks fail, the user is redirected to the form for a second attempt with an error message
     *      explaining why the request failed.
     *
     * @param ruleName Entity constructed from the form. It will be added to the database
     * @param result Form result. May contain errors if data don't comply
     * @param model Web UI container. Contains all the RuleNames
     * @return URI bidList/list. Show table with updated RuleNames
     * @return In case of error : URI ruleName/add. Returns to the form for a second attempt
     */
    @PostMapping("/ruleName/validate")
    public String validate(@Valid @RequestBody RuleName ruleName, BindingResult result, Model model) {
        // TODO: check data valid and save to db, after saving return RuleName list --> DONE
        if (!result.hasErrors()) {
            ruleNameRepository.save(ruleName);
            model.addAttribute("ruleNames", ruleNameRepository.findAll());
            return "redirect:/ruleName/list";
        }
        return "ruleName/add";
    }

    /**
     * This method retrieves the data of the RuleName from the database.
     * It then generates a form filled with the data for modification.
     *
     * @param id Id of the RuleName the user wants to update
     * @param model Web UI container. Contains the data of the desired RuleName
     * @return URI ruleName/update. Show form with input filled with RuleName's data
     */
    @GetMapping("/ruleName/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        // TODO: get RuleName by Id and to model then show to the form --> DONE
        RuleName ruleName = ruleNameRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ruleName Id:" + id));
        model.addAttribute("ruleName", ruleName);
        return "ruleName/update";
    }

    /**
     * This method checks if the data from the form are consistent and valid to update the RuleName.
     * If the checks pass then it updates the RuleName into the Database using the updated data from the form.
     *      It then retrieves all RuleNames from database and list them in the UI for the user.
     * If the checks fail, the user is redirected to the form for a second attempt with an error message
     *      explaining why the request failed.
     *
     * @param id Id of the RuleName the user wants to update
     * @param ruleName Entity constructed from the form. It will update the existing RuleName in the Database
     * @param result Form result. May contain errors if data don't comply
     * @param model Web UI container. Contains all the RuleNames
     * @return URI ruleName/list. Show table with updated RuleName
     * @return In case of error : URI ruleName/update. Returns to the form for a second attempt
     */
    @PatchMapping("/ruleName/update/{id}")
    public String updateRuleName(@PathVariable("id") Integer id, @Valid @RequestBody RuleName ruleName,
                             BindingResult result, Model model) {
        // TODO: check required fields, if valid call service to update RuleName and return RuleName list --> DONE
        if (result.hasErrors()) {
            return "ruleName/update";
        }
        ruleNameRepository.save(ruleName);
        model.addAttribute("ruleNames", ruleNameRepository.findAll());
        return "redirect:/ruleName/list";
    }

    /**
     * This method deletes a RuleName from the Database.
     * It then retrieves all RuleNames from database and list them in the UI for the user.
     *
     * @param id Id of the BidList the user wants to delete
     * @param model Web UI container. Contains all the remaining RuleNames
     * @return URI ruleName/list. Show table with updated RuleNames
     */
    @DeleteMapping("/ruleName/delete/{id}")
    public String deleteRuleName(@PathVariable("id") Integer id, Model model) {
        // TODO: Find RuleName by Id and delete the RuleName, return to Rule list --> DONE
        RuleName ruleName = ruleNameRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ruleName Id:" + id));
        ruleNameRepository.delete(ruleName);
        model.addAttribute("ruleNames", ruleNameRepository.findAll());
        return "redirect:/ruleName/list";
    }
}
