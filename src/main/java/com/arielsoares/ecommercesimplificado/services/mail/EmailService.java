package com.arielsoares.ecommercesimplificado.services.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;
	
	@Value("${spring.mail.username}")
	String from;

	public void sendSimpleEmail(String toEmail, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(toEmail);
		message.setText(body);
		message.setSubject(subject);

		mailSender.send(message);
	}
}
