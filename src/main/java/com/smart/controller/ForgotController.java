package com.smart.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.services.EmailService;

@Controller
public class ForgotController {
	Random random = new Random(1000);
	@Autowired
	private EmailService emailService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
//	 email id form open handler
	@RequestMapping("/forgot")
	public String openEmailForm(Model m) {
		m.addAttribute("title", "Forgot-Password");
		return "forgot_email_form";
	}
	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email") String email, Model m,HttpSession session) {
		m.addAttribute("title", "OTP-Verification");
		System.out.println(email);
//		generating otp of four digits 
		int otp = random.nextInt(9999);
		System.out.println(otp);
//		send otp 
		String subject = "OTP form SCM";
		String message = "<h1> OTP : "+otp+"</h1>";
		String to = email;
		boolean flag = this.emailService.sendEmail(subject, message, to);
		if(flag) {
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
		}
		else {
			session.setAttribute("message", new Message("check your email address !!", "alert-danger"));
			return "forgot_email_form";
		}
		
	}
//	verify otp handller
	@PostMapping("/verify-otp")
	public String verifyOTP(@RequestParam("otp") int otp, HttpSession session, Model m) {
		int myotp = (int)session.getAttribute("myotp");
		String email = (String)session.getAttribute("email");
		if(myotp == otp) {
			User user = this.userRepository.getUserByUsername(email);
			if(user==null) {
				// send error message 
				session.setAttribute("message", new Message("email does not exit in our database !!", "alert-danger"));
				return "forgot_email_form";
			}
			else {
//				send change password form
				m.addAttribute("title", "ChangePassword");
				return "password_change_form";
			}
			
		}
		else {
			session.setAttribute("message", new Message("invalid otp !!", "alert-danger"));
			return "verify_otp";
		}
	}
//	change password form 
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword") String newpassword,HttpSession session) {
		
		String email = (String)session.getAttribute("email");
		User user = this.userRepository.getUserByUsername(email);
		user.setPassword(this.passwordEncoder.encode(newpassword));
		this.userRepository.save(user);
		return "redirect:/signin?change=password changed successfully..!";
	}
}
