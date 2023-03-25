package com.smart.services;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.stereotype.Service;


@Service
public class EmailService {

	
	
	
	
	
	
	
	
	
	
	
	
	
	public boolean sendMail(String subject,String message,String to)
	{
		boolean f=false;
		
		String from ="sagarauti2016@gmail.com";
		
		
		//variable for gmail
		String host="smtp.gmail.com";
		
		//get the system properties
		Properties properties=System.getProperties();
		System.out.println("properties"+properties);
		
		//setting important information to properties object
		
		//host set
		properties.put("mail.smtp.host", host);
		
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		
		
		
		//setp 1 to get session obj
		
		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("sagarauti2016@gmail.com","xrpkxpmtuehpdxga");
			}
		
		
		});
		

		
		session.setDebug(true);
		
		//step-2 compose message 
		MimeMessage m=new MimeMessage(session);
		
		try {
			//from email
			m.setFrom(from);
			
			//adding recipitent to message
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			
			//adding subject to message
			m.setSubject(subject);
			
			//adding text to meassage
			m.setText(message);
			
			//send
			Transport.send(m);
			
			System.out.println("send success-----------");
			
			f=true;
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return f;
	}
}
