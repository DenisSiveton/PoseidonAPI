package com.nnk.springboot.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * This class serves as a controller layer after the login process.
 * It will use redirect the user to his home page based on his role.
 * @author M Siveton Denis
 * @version 1.0
 */
@Controller
public class HomeController{
	/**
	 * This method sends the user to the Application home page.
	 *
	 * @param model Web UI container.
	 * @return URI: home. Home page to access the login page or to access the user management page
	 */
	@RequestMapping("/")
	public String home(Model model)
	{
		return "home";
	}

	/**
	 * This method redirects a Admin user to their home page.
	 *
	 * @param model Web UI container. Contains all the BidList
	 * @return URI: bidList/list. Home page for Admin user
	 */
	@RequestMapping("/admin/home")
	public String adminHome(Model model)
	{
		return "redirect:/bidList/list";
	}

	/**
	 * This method redirects a Regular user to their home page.
	 *
	 * @param model Web UI container. Contains all the BidList
	 * @return URI: bidList/list. Home page for Regular user
	 */
	@RequestMapping("/user/home")
	public String userHome(Model model)
	{
		return "redirect:/bidList/list";
	}

	/**
	 * This method checks the Role the logged in user has and redirects them depending on it.
	 *
	 * @return URI: #user -> user/home. Redirection for Admin user.
	 * 				#admin ->admin/home. Redirection for Regular user.
	 */
	@RequestMapping("/default")
	public String defaultAfterLogin() {
		Collection<? extends GrantedAuthority> authorities;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		authorities = auth.getAuthorities();
		String myRole = authorities.toArray()[0].toString();
		if (myRole.contains("ADMIN")) {
			return "redirect:/admin/home";
		}
		return "redirect:/user/home";
	}
}
