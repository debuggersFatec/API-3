package com.api_3.auth_service.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.api_3.auth_service.repository.UserRepository;

@Service
public class PasswordResetService {

	private final String PREFIX = "password_reset:";
	private final String ATTEMPT_PREFIX = "password_reset_attempts:";

	@Value("${front.base.url}")
	private String baseUrl;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private AuthService authService; 

	@Autowired
	private EmailService emailService;
    
	@Autowired
	private UserRepository userRepository;

	public String recoverPassword(String email) {

		String token = UUID.randomUUID().toString().replace("-", "");

		if (userRepository.findByEmailIgnoreCase(email).isEmpty()) {
			System.out.println("Solicitação de redefinição para e-mail não existente: " + email);
			return null;
		}

		redisTemplate.opsForValue().set(PREFIX + token, email, 15, TimeUnit.MINUTES);
		redisTemplate.opsForValue().set(ATTEMPT_PREFIX + token, "0");

		String resetUrl = baseUrl + "/reset-password/" + token;

		String text = String.format("""
			<p>Olá,</p>
			<p>Você solicitou a redefinição de senha da sua conta FASTASK.</p>
			<p>Clique no link abaixo para criar uma nova senha:</p>
			<a href="%s" style="color: #ffffff; background-color: #007bff; padding: 10px 15px; text-decoration: none; border-radius: 5px; display: inline-block;">Redefinir Senha</a>
			<p>Se você não solicitou isso, por favor, ignore este e-mail.</p>
			<p>Este link expira em 15 minutos.</p>
			""", resetUrl);
		String subject = "FASTASK - Redefinição de Senha";
        
		emailService.sendEmailViaGmail(email, subject, text);

		System.out.println("URL de redefinição de senha: " + resetUrl);
		return resetUrl;
	}

	public void resetPassword(String newPassword, String token) {
		String redisKey = PREFIX + token;
		String attemptsKey = ATTEMPT_PREFIX + token;

		String email = validateToken(token);

		Long attempts = redisTemplate.opsForValue().increment(attemptsKey);

		if (attempts != null && attempts == 1) {
			redisTemplate.expire(attemptsKey, 15, TimeUnit.MINUTES);
		}

		if (attempts != null && attempts > 3) {
			redisTemplate.delete(redisKey);
			redisTemplate.delete(attemptsKey);
			throw new IllegalArgumentException("Muitas tentativas inválidas. O link expirou.");
		}

		authService.updatePassword(email, newPassword);

		redisTemplate.delete(redisKey);
		redisTemplate.delete(attemptsKey);

		System.out.println("Senha redefinida com sucesso para: " + email);
	}

	public String validateToken(String token) {
		String redisKey = PREFIX + token;
		String email = redisTemplate.opsForValue().get(redisKey);
		if (email == null) {
			throw new IllegalArgumentException("Link inválido ou expirado.");
		}
		return email;
	}
}