package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * This class serves as a controller layer for the BidList entity.
 * It will use the CRUD methods for data persistence
 * @author M Siveton Denis
 * @version 1.0
 */
@Controller
public class BidListController {
    // TODO: Inject Bid service --> DONE
    @Autowired
    private BidListRepository bidListRepository;

    /**
     * This methods retrieves all BidLists from database and list them for the user.
     *
     * @param model Web UI container. Contains all the BidLists
     * @return URI bidList/list. Show table with all BidLists
     */
    @RequestMapping("/bidList/list")
    public String home(Model model)
    {
        // TODO: call service find all bids to show to the view --> DONE
        model.addAttribute("bidLists", bidListRepository.findAll());
        return "bidList/list";
    }

    /**
     * This method returns a form to add a BidList.
     *
     * @param bid Entity that will be generated with the form then added to the Database
     * @return URI bidList/add. Show form with input for new BidList
     */
    @GetMapping("/bidList/add")
    public String addBidForm(BidList bid) {
        return "bidList/add";
    }

    /**
     * This method checks if the data from the form are consistent and valid for a new BidList.
     * If the checks pass then add the new BidList to the database.
     *      It then retrieves all BidLists from database and list them in the UI for the user.
     * If the checks fail, the user is redirected to the form for a second attempt with an error message
     *      explaining why the request failed.
     *
     * @param bid Entity constructed from the form. It will be added to the database
     * @param result Form result. May contain errors if data don't comply
     * @param model Web UI container. Contains all the BidLists
     * @return URI bidList/list. Show table with updated BidLists
     * @return In case of error : URI bidList/add. Returns to the form for a second attempt
     */
    @PostMapping(path = "/bidList/validate")
    public String validate(@Valid BidList bid, BindingResult result, Model model) {
        // TODO: check data valid and save to db, after saving return bid list --> DONE
        if (!result.hasErrors()) {
            bidListRepository.save(bid);
            model.addAttribute("bidLists", bidListRepository.findAll());
            return "redirect:/bidList/list";
        }
        return "bidList/add";
    }

    /**
     * This method retrieves the data of the BidList from the database.
     * It then generates a form filled with the data for modification.
     *
     * @param id Id of the BidList the user wants to update
     * @param model Web UI container. Contains the data of the desired BidList
     * @return URI bidList/update. Show form with input filled with BidList's data
     */
    @GetMapping("/bidList/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        // TODO: get Bid by Id and to model then show to the form --> DONE
        BidList bidList = bidListRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid bidList Id:" + id));
        model.addAttribute("bidList", bidList);
        return "bidList/update";
    }

    /**
     * This method checks if the data from the form are consistent and valid to update the BidList.
     * If the checks pass then it updates the BidList into the Database using the updated data from the form.
     *      It then retrieves all BidLists from database and list them in the UI for the user.
     * If the checks fail, the user is redirected to the form for a second attempt with an error message
     *      explaining why the request failed.
     *
     * @param id Id of the BidList the user wants to update
     * @param bidList Entity constructed from the form. It will update the existing BidList in the Database
     * @param result Form result. May contain errors if data don't comply
     * @param model Web UI container. Contains all the BidLists
     * @return URI bidList/list. Show table with updated BidLists
     * @return In case of error : URI bidList/update. Returns to the form for a second attempt
     */
    @PostMapping("/bidList/update/{id}")
    public String updateBid(@PathVariable("id") Integer id, @Valid BidList bidList,
                             BindingResult result, Model model) {
        // TODO: check required fields, if valid call service to update Bid and return list Bid --> DONE
        if (result.hasErrors()) {
            model.addAttribute("bidList", bidList);
            return "bidList/update";
        }
        bidListRepository.save(bidList);
        model.addAttribute("bidLists", bidListRepository.findAll());
        return "redirect:/bidList/list";
    }

    /**
     * This method deletes a BidList from the Database.
     * It then retrieves all BidLists from database and list them in the UI for the user.
     *
     * @param id Id of the BidList the user wants to delete
     * @param model Web UI container. Contains all the remaining BidLists
     * @return URI bidList/list. Show table with updated BidLists
     */
    @GetMapping("/bidList/delete/{id}")
    public String deleteBid(@PathVariable("id") Integer id, Model model) {
        // TODO: Find Bid by Id and delete the bid, return to Bid list --> DONE
        BidList bidList = bidListRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid bidList Id:" + id));
        bidListRepository.delete(bidList);
        model.addAttribute("bidLists", bidListRepository.findAll());
        return "redirect:/bidList/list";
    }
}
