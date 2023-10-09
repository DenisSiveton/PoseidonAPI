package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.CurvePointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * This class serves as a controller layer for the CurvePoint entity.
 * It will use the CRUD methods for data persistence
 * @author M Siveton Denis
 * @version 1.0
 */
@Controller
public class CurveController {
    // TODO: Inject Curve Point service --> DONE
    @Autowired
    private CurvePointRepository curvePointRepository;

    /**
     * This methods retrieves all CurvePoints from database and list them for the user.
     *
     * @param model Web UI container. Contains all the CurvePoints
     * @return URI curvePoint/list. Show table with all CurvePoints
     */
    @RequestMapping("/curvePoint/list")
    public String home(Model model)
    {
        // TODO: find all Curve Point, add to model --> DONE
        model.addAttribute("curvePoints", curvePointRepository.findAll());
        return "curvePoint/list";
    }

    /**
     * This method returns a form to add a CurvePoint.
     *
     * @param curvePoint Entity that will be generated with the form then added to the Database
     * @return URI curvePoint/add. Show form with input for new CurvePoint
     */
    @GetMapping("/curvePoint/add")
    public String addBidForm(CurvePoint curvePoint) {
        return "curvePoint/add";
    }

    /**
     * This method checks if the data from the form are consistent and valid for a new CurvePoint.
     * If the checks pass then add the new CurvePoint to the database.
     *      It then retrieves all CurvePoints from database and list them in the UI for the user.
     * If the checks fail, the user is redirected to the form for a second attempt with an error message
     *      explaining why the request failed.
     *
     * @param curvePoint Entity constructed from the form. It will be added to the database
     * @param result Form result. May contain errors if data don't comply
     * @param model Web UI container. Contains all the CurvePoints
     * @return URI curvePoint/list. Show table with updated CurvePoints
     * @return In case of error : URI curvePoint/add. Returns to the form for a second attempt
     */
    @PostMapping(path = "/curvePoint/validate", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public String validate(@Valid CurvePoint curvePoint, BindingResult result, Model model) {
        // TODO: check data valid and save to db, after saving return Curve list -- > DONE
        if (!result.hasErrors()) {
            curvePointRepository.save(curvePoint);
            model.addAttribute("curvePoints", curvePointRepository.findAll());
            return "redirect:/curvePoint/list";
        }
        return "curvePoint/add";
    }

    /**
     * This method retrieves the data of the CurvePoint form the database.
     * It then generates a form filled with the data for modification.
     *
     * @param id Id of the CurvePoint the user wants to update
     * @param model Web UI container. Contains the data of the desired CurvePoint
     * @return URI curvePoint/add. Show form with input filled with CurvePoint's data
     */
    @GetMapping("/curvePoint/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        // TODO: get CurvePoint by Id and to model then show to the form
        CurvePoint curvePoint = curvePointRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid curvePoint Id:" + id));
        model.addAttribute("curvePoint", curvePoint);
        return "curvePoint/update";
    }

    /**
     * This method checks if the data from the form are consistent and valid to update the CurvePoint.
     * If the checks pass then it updates the CurvePoint into the Database using the updated data from the form.
     *      It then retrieves all CurvePoint from database and list them in the UI for the user.
     * If the checks fail, the user is redirected to the form for a second attempt with an error message
     *      explaining why the request failed.
     *
     * @param id Id of the CurvePoint the user wants to update
     * @param curvePoint Entity constructed from the form. It will update the existing CurvePoint in the Database
     * @param result Form result. May contain errors if data don't comply
     * @param model Web UI container. Contains all the CurvePoints
     * @return URI curvePoint/list. Show table with updated CurvePoints
     * @return In case of error : URI curvePoint/update. Returns to the form for a second attempt
     */
    @PostMapping(path = "/curvePoint/update/{id}", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public String updateCurvePoint(@PathVariable("id") Integer id, @Valid CurvePoint curvePoint,
                             BindingResult result, Model model) {
        // TODO: check required fields, if valid call service to update Curve and return Curve list
        if (result.hasErrors()) {
            model.addAttribute("curvePoint", curvePoint);
            return "curvePoint/update";
        }
        curvePointRepository.save(curvePoint);
        model.addAttribute("curvePoints", curvePointRepository.findAll());
        return "redirect:/curvePoint/list";
    }

    /**
     * This method deletes a CurvePoint from the Database.
     * It then retrieves all CurvePoints from database and list them in the UI for the user.
     *
     * @param id Id of the CurvePoint the user wants to delete
     * @param model Web UI container. Contains all the remaining CurvePoints
     * @return URI curvePoint/list. Show table with updated CurvePoints
     */
    @GetMapping("/curvePoint/delete/{id}")
    public String deleteCurvePoint(@PathVariable("id") Integer id, Model model) {
        // TODO: Find Curve by Id and delete the Curve, return to Curve list
        CurvePoint curvePoint = curvePointRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid curvePoint Id:" + id));
        curvePointRepository.delete(curvePoint);
        model.addAttribute("curvePoints", curvePointRepository.findAll());
        return "redirect:/curvePoint/list";
    }
}
