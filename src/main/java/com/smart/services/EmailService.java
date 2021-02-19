package com.smart.services;

import org.springframework.stereotype.Service;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {
	
	 public boolean sendEmail(String subject, String message, String to){
	        boolean f = false;
//	        rest of the code
	        String from = "demoanshusony@gmail.com";
//	        variable for gmail
	        String host = "smtp.gmail.com";
//	        getting system properties
	        Properties properties = System.getProperties();

//	        setting important information in properties
//	        host setup
	        properties.put("mail.smtp.host", host);
	        properties.put("mail.smtp.port", "465");
	        properties.put("mail.smtp.ssl.enable", "true");
	        properties.put("mail.smtp.auth", "true");

//	        Step 1: to get session object
	        Session session = Session.getInstance(properties, new Authenticator() {
	            @Override
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication("demoanshusony@gmail.com", "Myeverything$anshu");
	            }
	        });
	        session.setDebug(true);
//	        Step 2 : compose the message
	        MimeMessage mimeMessage = new MimeMessage(session);

	        try {
	//  from email id
	        mimeMessage.setFrom(from);
//	        set reciepent id
	            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
//	            setting subject
	            mimeMessage.setSubject(subject);
//	            set text to message
//	            mimeMessage.setText(message);
	            mimeMessage.setContent(message,"text/html");
	            
//	            Step 3: sending message using Transport class
	            Transport.send(mimeMessage);
	            System.out.println("Sent successfully............");
	            f = true;
	        }
	        catch (Exception e){
	            e.printStackTrace();
	            System.out.println("something went wrong...........");
	        }

	        return  f;
	    }

}
