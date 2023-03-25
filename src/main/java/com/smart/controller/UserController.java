package com.smart.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {
     @Autowired
	private UserRepository userRepository;
     
     @Autowired
     private ContactRepository contactRepository; 
     
     @Autowired
     private BCryptPasswordEncoder bCryptPasswordEncoder;
     
     
     @ModelAttribute
    public void addCommonData(Model model,Principal principal)
    {
    	 String userName=principal.getName();
 		System.out.println(userName);
 		User user=userRepository.getUserByUserName(userName);
 		model.addAttribute("title","Dashboard: Smart Contact Manager");
 		
 		model.addAttribute("user",user);
 		System.out.println(user);
    }
     
	@RequestMapping("/dashboard")
	private String dashboard(Model model,Principal principal)
	{
		
		return"user/dashboard";
	}
	
	
	@RequestMapping("/add-contact")
	private String addNewContact(Model model)
	{
		
		model.addAttribute("title","Add Contact: Smart Contact Manager");
		
		model.addAttribute("contact",new Contact());
		
		
		return"user/add-contact";
	}
	
	@PostMapping("/add_new_contact")
	private String addcontact(@ModelAttribute Contact contact,@RequestParam("contactImage") MultipartFile file,Principal principal,HttpSession session)
	{   
		try {
		String name=principal.getName();
		User user=userRepository.getUserByUserName(name);
		
		if(file.isEmpty())
		{
			contact.setImage("contact.png");
		}
		else {
		contact.setImage(file.getOriginalFilename());
		File saveFile=new ClassPathResource("static/img").getFile();
		Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
		
		Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
		System.out.println("File Uploaded succesfully..");
		
		}
		contact.setUser(user);
		user.getContacts().add(contact);
	    this.userRepository.save(user);
		System.out.println("DAta"+contact);
		}catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Something Wents Wrong !!","alert-danger"));

		}
		session.setAttribute("message", new Message("Contact Added Succesfully !!","alert-success"));
		
		return "user/add-contact";
	}
	
	@GetMapping("/show-contacts/{page}")
	private String showContacts(@PathVariable("page") Integer page,Model model,Principal pricipal)
	{
		model.addAttribute("title","contact save page");
		
		String userName=pricipal.getName();
		
		User user= userRepository.getUserByUserName(userName);
		
		Pageable pageable= PageRequest.of(page, 2);
		
		Page<Contact> contacts=contactRepository.findContactsByUser(user.getId(),pageable);
		
		model.addAttribute("contact",contacts);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages",contacts.getTotalPages());
		
		
		
		return "user/show-contacts";
	}
	//for showing single contact
	@GetMapping("/contact/{cid}")
	private String showContactDetail(@PathVariable("cid")Integer cid,Model model,Principal principal) {
		
		java.util.Optional<Contact> optionalContact= contactRepository.findById(cid);
		
		Contact contact=optionalContact.get();
		
		String userName=principal.getName();
		
		User user= userRepository.getUserByUserName(userName);
		
		if(user.getId()==contact.getUser().getId())
		{
			model.addAttribute("contact",contact);
			model.addAttribute("title",contact.getName());
		}
		
		
		return "user/view-contact-detail";
	}
	// for deleteing contact
	@GetMapping("/delete/{cid}/{page}")
	private String deleteContact(@PathVariable("cid")Integer cid,@PathVariable("page") Integer page,Model model,HttpSession session,Principal principal) throws IOException {
		
		java.util.Optional<Contact> optionalContact= contactRepository.findById(cid);
			
		Contact contact=optionalContact.get();

		File delFile=new ClassPathResource("static/img").getFile();
		
		File file=new File(delFile,contact.getImage());
		
		file.delete();
		
		

		
//			contact.setUser(null);
//			contactRepository.deleteById(cid);
			
			
		User user=this.userRepository.getUserByUserName(principal.getName());
		user.getContacts().remove(contact);
		this.userRepository.save(user);
			
			model.addAttribute("contact",contact);
			model.addAttribute("title",contact.getName());
			session.setAttribute("message", new Message("User Deleted Succesfully !!","alert-success"));
			return "redirect:/user/show-contacts/"+page;


		
	}
	
	//Update Contact Helper
	@PostMapping("/get-update-form/{cId}")
	private String getUpdateContactForm(@PathVariable("cId") Integer cId,Model model)
	{   
		java.util.Optional<Contact> optionalContact= contactRepository.findById(cId);
		
		Contact contact=optionalContact.get();
		model.addAttribute("contact",contact);
		model.addAttribute("title","Update Contact Form");
		return "user/update-form";
	}
	
	
	//Save Updated Detailews Here

	@PostMapping("/update-con-detailes/{cId}")
	private String updateContactDetaile(@PathVariable("cId") Integer cId,@ModelAttribute Contact contact,
			HttpSession session,Principal principal,
			@RequestParam("contactImage") MultipartFile file)
	{   
		
		Contact oldCon=this.contactRepository.findById(cId).get();
		
		try {
			
			
			if(file.isEmpty())
			{
				contact.setImage("contact.png");
			}
			else {
			
				File delFile=new ClassPathResource("static/img").getFile();
				
				File file1=new File(delFile,oldCon.getImage());
				
				file1.delete();
				
				
			contact.setImage(file.getOriginalFilename());
			File saveFile=new ClassPathResource("static/img").getFile();
			Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			
			Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
			System.out.println("File Uploaded succesfully..");
			
			}
			String name=principal.getName();
			User user=userRepository.getUserByUserName(name);
			contact.setUser(user);
			this.contactRepository.save(contact);
			
		}catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Something Wents Wrong !!","alert-danger"));

		}
		session.setAttribute("message", new Message("Contact Updated Succesfully !!","alert-success"));			
		return "redirect:/user/show-contacts/0";
		
	}
	
	
	@GetMapping("/get_user_profile")
	 private String getUserDetailes(Model model)
	 {
		model.addAttribute("title","User Detailes");
		return "user/user_detailes";
	 }
	
	
	// open Setting page
	@GetMapping("/open-setting")
	private String openSetting(Model model)
	{
		model.addAttribute("title","Forgot Password !!");
		return "user/settings";
	}
	
	@PostMapping("/save-password")
	private String saveNewPassword(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword,
			Principal principal,HttpSession session)
	{
		
		try {
			
			User user=this.userRepository.getUserByUserName(principal.getName());
			
			if(bCryptPasswordEncoder.matches( oldPassword,user.getPassword()))
			{
				user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
				this.userRepository.save(user);
				session.setAttribute("message", new Message("Your Password Change Successfully !!","alert-success"));

				return "redirect:/logout";
			}
			else {
				session.setAttribute("message", new Message("Please Enter Correct old Password !!","alert-danger"));
				return "redirect:/user/open-setting";

			}
			
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			session.setAttribute("message", new Message("Something Wents Wrong !!","alert-danger"));
			return "redirect:/user/open-setting";
		}
	}
	
	
}

