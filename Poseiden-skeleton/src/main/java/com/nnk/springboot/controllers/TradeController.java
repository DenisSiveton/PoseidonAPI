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

@Controller
public class TradeController {
    // TODO: Inject Trade service --> DONE
    @Autowired
    private TradeRepository tradeRepository;

    @RequestMapping("/trade/list")
    public String home(Model model)
    {
        // TODO: find all Trade, add to model --> DONE
        model.addAttribute("trades", tradeRepository.findAll());
        return "trade/list";
    }

    @GetMapping("/trade/add")
    public String addUser(Trade trade) {
        return "trade/add";
    }

    @PostMapping("/trade/validate")
    public String validate(@Valid @RequestBody Trade trade, BindingResult result, Model model) {
        // TODO: check data valid and save to db, after saving return Trade list --> DONE
        if (!result.hasErrors()) {
            tradeRepository.save(trade);
            model.addAttribute("trades", tradeRepository.findAll());
            return "redirect:/trade/list";
        }
        return "trade/add";
    }

    @GetMapping("/trade/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model) {
        // TODO: get Trade by Id and to model then show to the form --> DONE
        Trade trade = tradeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid trade Id:" + id));
        model.addAttribute("trade", trade);
        return "trade/update";
    }

    @PatchMapping("/trade/update/{id}")
    public String updateTrade(@PathVariable("id") Integer id, @Valid @RequestBody Trade trade,
                             BindingResult result, Model model) {
        // TODO: check required fields, if valid call service to update Trade and return Trade list --> DONE
        if (result.hasErrors()) {
            return "trade/update";
        }
        tradeRepository.save(trade);
        model.addAttribute("trades", tradeRepository.findAll());
        return "redirect:/trade/list";
    }

    @DeleteMapping("/trade/delete/{id}")
    public String deleteTrade(@PathVariable("id") Integer id, Model model) {
        // TODO: Find Trade by Id and delete the Trade, return to Trade list --> DONE
        Trade trade = tradeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid trade Id:" + id));
        tradeRepository.delete(trade);
        model.addAttribute("trades", tradeRepository.findAll());
        return "redirect:/trade/list";
    }
}
