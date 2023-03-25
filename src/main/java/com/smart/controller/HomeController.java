package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.repository.UserRepository;


@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
      @Autowired
	private UserRepository userRepository;
	
	@RequestMapping("/")
	public String home(Model model)
	{ 
		model.addAttribute("title","Home: Smart Contact manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model)
	{ 
		model.addAttribute("title","About: Smart Contact manager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model)
	{ 
		model.addAttribute("title","SignUp: Smart Contact manager");
		model.addAttribute("user",new User());
		return "signup";
	}
	
	@GetMapping("/signin")
	public String login(Model model)
	{ 
		model.addAttribute("title","Login: Smart Contact manager");
		return "login";
	}
	

	
	
	@RequestMapping(value="/do_register",method = RequestMethod.POST)
	public String register(@Valid @ModelAttribute("user") User user,BindingResult result1 ,@RequestParam(value="aggrement",defaultValue = "false") boolean aggrement ,Model model,HttpSession session )
	{ 
		try {
			
			if(!aggrement)
			{
				throw new Exception("You Not Select Agreement Please Select..");
			}
			if(result1.hasErrors())
			{
				model.addAttribute("user",user);
				return "signup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImage("default.png");
			
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println("user"+user);
			System.out.println("Aggrement"+aggrement);
		    User result=userRepository.save(user);
			model.addAttribute("user",result);
			
			
			session.setAttribute("message",new Message("Registerd Succesfully !!","alert-success"));
			return "signup";	
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message",new Message("Somthing Went Wrong !!"+e.getMessage(), "alert-danger"));
			return "signup";
		}
		
	}
}
