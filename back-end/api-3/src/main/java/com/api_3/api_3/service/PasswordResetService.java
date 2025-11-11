package com.api_3.api_3.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.api_3.api_3.repository.UserRepository;

@Service
public class PasswordResetService {

    // Prefixo para organizar as chaves no Redis
    private final String PREFIX = "password_reset:";
    private final String ATTEMPT_PREFIX = "password_reset_attempts:";

    // Pega a URL do frontend que definimos no application.properties
    @Value("${front.base.url}")
    private String baseUrl;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // Usaremos o AuthService para ATUALIZAR a senha
    @Autowired
    private AuthService authService; 

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserRepository userRepository;

    public String recoverPassword(String email) {

        String token = UUID.randomUUID().toString().replace("-", "");

        // Verifica se o usuário existe ANTES de qualquer coisa
        if (userRepository.findByEmailIgnoreCase(email).isEmpty()) {
            // Não retorne um erro aqui. Isso evita que atacantes descubram
            // quais e-mails estão cadastrados. Apenas finja que funcionou.
            System.out.println("Solicitação de redefinição para e-mail não existente: " + email);
            return null;
        }

        // 3. Armazena o token no Redis (token -> email) com expiração de 15 minutos
        redisTemplate.opsForValue().set(PREFIX + token, email, 15, TimeUnit.MINUTES);
        // Armazena um contador de tentativas para este token
        redisTemplate.opsForValue().set(ATTEMPT_PREFIX + token, "0");

        String resetUrl = baseUrl + "/reset-password/" + token;

        // 5. Prepara e envia o e-mail
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

        // Valida o token (busca o e-mail no Redis)
        String email = validateToken(token);

        //Controla tentativas para evitar força bruta no mesmo token
        Long attempts = redisTemplate.opsForValue().increment(attemptsKey);

        if (attempts != null && attempts == 1) {
            redisTemplate.expire(attemptsKey, 15, TimeUnit.MINUTES);
        }

        if (attempts != null && attempts > 3) {
            // Invalida o token se houver muitas tentativas
            redisTemplate.delete(redisKey);
            redisTemplate.delete(attemptsKey);
            throw new IllegalArgumentException("Muitas tentativas inválidas. O link expirou.");
        }

        // Se tudo estiver OK, chama o AuthService para atualizar a senha no MongoDB
        authService.updatePassword(email, newPassword);

        // Remove o token do Redis para que não possa ser usado novamente
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