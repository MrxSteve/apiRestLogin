package com.login.backend.services.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Data;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@Data
public class EmailService {
    private final JavaMailSender javaMailSender;

    /**
     * Envía un correo electrónico basado en una plantilla HTML.
     *
     * @param to            Dirección de correo del destinatario
     * @param subject       Asunto del correo
     * @param templatePath  Ruta al archivo HTML de la plantilla
     * @param dynamicLink   Enlace dinámico para el correo (activación o recuperación)
     * @throws MessagingException Si ocurre un error al enviar el correo
     * @throws IOException        Si ocurre un error al leer el archivo HTML
     */
    public void sendHtmlEmail(String to, String subject, String templatePath, String dynamicLink) throws MessagingException, IOException {
        // Leer la plantilla HTML desde el archivo
        String htmlContent = new String(Files.readAllBytes(Paths.get(templatePath)));

        // Reemplazar los marcadores de posición con valores dinámicos
        htmlContent = htmlContent.replace("{{dynamicLink}}", dynamicLink);

        // Configurar el mensaje MIME
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // Habilitar contenido HTML

        // Enviar el correo
        javaMailSender.send(message);
    }
}
