package com.nnk.springboot.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@Controller
public class HomeController
{
	@RequestMapping("/")
	public String home(Model model)
	{
		return "home";
	}

	@RequestMapping("/admin/home")
	public String adminHome(Model model)
	{
		return "redirect:/bidList/list";
	}

	@RequestMapping("/default")
	public String defaultAfterLogin() {
		Collection<? extends GrantedAuthority> authorities;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		authorities = auth.getAuthorities();
		String myRole = authorities.toArray()[0].toString();
		if (myRole.equals("ADMIN")) {
			return "redirect:/admin/home";
		}
		return "redirect:/bidList/list";
	}
}
