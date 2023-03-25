package com.smart.controller;

import java.io.IOException;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Null;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.repository.UserRepository;
import com.smart.services.EmailService;

@Controller
public class ForgotController {
	
	 Random random=new Random(1000);
     
	 
	 @Autowired
     private BCryptPasswordEncoder bCryptPasswordEncoder; 
	 
	 @Autowired
	 private EmailService emailService;
	 
	 @Autowired
	 private UserRepository userRepository;
	
	@RequestMapping("/forgot")
	public String openEmailForm() throws AddressException, MessagingException, IOException
	{
		
		return "send-email";
	}
	
	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email") String email,HttpSession session,Model model) 
	{
	 System.out.println("Email="+email);
	 int otp=random.nextInt(999999);
	 System.out.println("OTP="+otp);

	  String subject="Otp From S And R Company";
	  String message="<h2>Your One Time Password Is ="+otp+"Please Verify </h2>";
	  String to=email;
	   
	  User user=this.userRepository.getUserByName(email); 
	  
	  System.out.println(user);
	  
	 
	 
		  boolean flag=this.emailService.sendMail(subject, message, to);
		    if(flag)
		    {
		     model.addAttribute("email",email);
		    session.setAttribute("otp",otp);
			session.setAttribute("email",email);

			return "verify-otp";
		    }
	
	    return "send-email";
	}
	
	@PostMapping("/verify-otp")
	public String verifyOTP(@RequestParam("otp") int otp,HttpSession session)
	{
		int myotp=(int)session.getAttribute("otp");
		String email=(String)session.getAttribute("email");
		
		System.out.println("Sesiion Otp"+myotp);
		System.out.println("enter Otp"+otp);
		 
		if(myotp==otp)
		{
			User user=this.userRepository.getUserByName(email);
			System.out.println("User"+user);
			if(user==null)
			{
				session.setAttribute("message",new Message("Please New User SignUp First  !!","alert-danger"));
				return "redirect:/signup";
			}
			return "change-password";
		}
		else
		{
			session.setAttribute("message",new Message("Please enter valid OTP  !!","alert-danger"));
			return "verify-otp";

		}
		
	}
	@PostMapping("/savePassword")
	public String savePassword(@RequestParam("newPassword") String newPassword,HttpSession session)
	{
		String email=(String)session.getAttribute("email");
		User user=this.userRepository.getUserByName(email);
		user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
		this.userRepository.save(user);
		session.setAttribute("message",new Message("Your Password Change Successfully !!","alert-success"));
		return "redirect:/signin";
		
	}
	

}
