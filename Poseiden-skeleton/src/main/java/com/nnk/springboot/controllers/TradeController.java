package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * This class serves as a controller layer for the Trade entity.
 * It will use the CRUD methods for data persistence
 * @author M Siveton Denis
 * @version 1.0
 */
@Controller
public class TradeController {
    // TODO: Inject Trade service --> DONE
    @Autowired
    private TradeRepository tradeRepository;

    /**
     * This methods retrieves all Trades from database and list them for the user.
     *
     * @param model Web UI container. Contains all the Trades
     * @return URI trade/list. Show table with all Trades
     */
    @RequestMapping("/trade/list")
    public String home(Model model)
    {
        // TODO: find all Trade, add to model --> DONE
        model.addAttribute("trades", tradeRepository.findAll());
        return "trade/list";
    }

    /**
     * This method returns a form to add a Trade.
     *
     * @param trade Entity that will be generated with the form then added to the Database
     * @return URI trade/add. Show form with input for new Trade
     */
    @GetMapping("/trade/add")
    public String addUser(Trade trade) {
        return "trade/add";
    }

    /**
     * This method checks if the data from the form are consistent and valid for a new Trade.
     * If the checks pass then add the new Trade to the database.
     *      It then retrieves all Trades from database and list them in the UI for the user.
     * If the checks fail, the user is redirected to the form for a second attempt with an error message
     *      explaining why the request failed.
     *
     * @param trade Entity constructed from the form. It will be added to the database
     * @param result Form result. May contain errors if data don't comply
     * @param model Web UI container. Contains all the Trades
     * @return URI trade/list. Show table with updated Trades
     * @return In case of error : URI trade/add. Returns to the form for a second attempt
     */
    @PostMapping("/trade/validate")
    public String validate(@Valid Trade trade, BindingResult result, Model model) {
        // TODO: check data valid and save to db, after saving return Trade list --> DONE
        if (!result.hasErrors()) {
            tradeRepository.save(trade);
            model.addAttribute("trades", tradeRepository.findAll());
            return "redirect:/trade/list";
        }
        return "trade/add";
    }

    /**
     * This method retrieves the data of the Trade from the database.
     * It then generates a form filled with the data for modification.
     *
     * @param id Id of the Trade the user wants to update
     * @param model Web UI container. Contains the data of the desired Trade
     * @return URI trade/update. Show form with input filled with Trade's data
     */
    @GetMapping("/trade/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        // TODO: get Trade by Id and to model then show to the form --> DONE
        Trade trade = tradeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid trade Id:" + id));
        model.addAttribute("trade", trade);
        return "trade/update";
    }

    /**
     * This method checks if the data from the form are consistent and valid to update the Trade.
     * If the checks pass then it updates the Trade into the Database using the updated data from the form.
     *      It then retrieves all Trades from database and list them in the UI for the user.
     * If the checks fail, the user is redirected to the form for a second attempt with an error message
     *      explaining why the request failed.
     *
     * @param id Id of the Trade the user wants to update
     * @param trade Entity constructed from the form. It will update the existing Trade in the Database
     * @param result Form result. May contain errors if data don't comply
     * @param model Web UI container. Contains all the Trades
     * @return URI trade/list. Show table with updated Trades
     * @return In case of error : URI trade/update. Returns to the form for a second attempt
     */
    @PostMapping("/trade/update/{id}")
    public String updateTrade(@PathVariable("id") Integer id, @Valid Trade trade,
                             BindingResult result, Model model) {
        // TODO: check required fields, if valid call service to update Trade and return Trade list --> DONE
        if (result.hasErrors()) {
            model.addAttribute("trade", trade);
            return "trade/update";
        }
        tradeRepository.save(trade);
        model.addAttribute("trades", tradeRepository.findAll());
        return "redirect:/trade/list";
    }

    /**
     * This method deletes a Trade from the Database.
     * It then retrieves all Trades from database and list them in the UI for the user.
     *
     * @param id Id of the Trade the user wants to delete
     * @param model Web UI container. Contains all the remaining Trades
     * @return URI trade/list. Show table with updated Trades
     */
    @GetMapping("/trade/delete/{id}")
    public String deleteTrade(@PathVariable("id") Integer id, Model model) {
        // TODO: Find Trade by Id and delete the Trade, return to Trade list --> DONE
        Trade trade = tradeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid trade Id:" + id));
        tradeRepository.delete(trade);
        model.addAttribute("trades", tradeRepository.findAll());
        return "redirect:/trade/list";
    }
}
