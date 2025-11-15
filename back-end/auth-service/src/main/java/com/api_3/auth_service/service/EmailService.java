package com.api_3.auth_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	// método para enviar o e-mail de redefinição de senha
	public void sendEmailViaGmail(String toEmail, String subject, String htmlBody) {
		MimeMessage message = mailSender.createMimeMessage();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setTo(toEmail);
			helper.setSubject(subject);
			helper.setText(htmlBody, true);
			mailSender.send(message);
		} catch (MessagingException e) {
			throw new RuntimeException("Falha ao enviar e-mail", e);
		}

		System.out.println("E-mail de redefinição enviado para: " + toEmail);
	}
}