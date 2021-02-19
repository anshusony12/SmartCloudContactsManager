package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder PasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping("/")
	public String home(Model m ) {
		m.addAttribute("title", "Home-SmartContactManager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model m ) {
		m.addAttribute("title", "About-SmartContactManager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model m ) {
		m.addAttribute("title", "Register-SmartContactManager");
		m.addAttribute("user", new User());
		return "signup";
	}
	
//	do register handller
	@PostMapping("/do-register")
	public String registratioin(@Valid @ModelAttribute("user") User user,BindingResult result1,@RequestParam(value = "agreement", defaultValue = "false") boolean agreement,Model m, HttpSession session) {
		try {
			if(!agreement) {
				System.out.println("You have not agreed terms and condition");
				throw new Exception("You have not agreed terms and condition");

			}
			if(result1.hasErrors()) {
				m.addAttribute("user", user);
				return "signup";
			}
			m.addAttribute("user", new User());
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(PasswordEncoder.encode(user.getPassword()));
			User result = this.userRepository.save(user);
			System.out.println(user);
			session.setAttribute("message", new Message("Successfully registered !! ", "alert-success"));
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			m.addAttribute("user", user);
			System.out.println("Something went wrong!!");
			session.setAttribute("message", new Message("Something went wrong !! "+e.getMessage(), "alert-danger"));
				
		}
		return "signup";
	}
	
	// login controller
	@RequestMapping("/signin")
	public String customLogin(Model m) {
		m.addAttribute("title", "Login-SmartContactManager");
		return "login";
	}
}
