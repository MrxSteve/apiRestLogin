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

    /**
     * Envía un correo electrónico basado en una plantilla HTML con información adicional.
     *
     * @param to            Dirección de correo del destinatario
     * @param subject       Asunto del correo
     * @param templatePath  Ruta al archivo HTML de la plantilla
     * @param username      Nombre de usuario
     * @param loginTime     Hora de inicio de sesión
     * @param loginDate     Fecha de inicio de sesión
     * @throws MessagingException Si ocurre un error al enviar el correo
     * @throws IOException        Si ocurre un error al leer el archivo HTML
     */
    public void sendHtmlEmail(String to, String subject, String templatePath, String username, String loginTime, String loginDate) throws MessagingException, IOException {
        // Leer la plantilla HTML desde el archivo
        String htmlContent = new String(Files.readAllBytes(Paths.get(templatePath)));

        // Reemplazar los marcadores de posición con valores dinámicos
        htmlContent = htmlContent.replace("{{username}}", username)
                .replace("{{loginTime}}", loginTime)
                .replace("{{loginDate}}", loginDate);

        // Configurar el mensaje MIME
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // Habilitar contenido HTML

        // Enviar el correo
        javaMailSender.send(message);
    }

    /**
     * Envía un correo electrónico de confirmación de activación de cuenta basado en una plantilla HTML.
     *
     * @param to           Dirección de correo del destinatario
     * @param subject      Asunto del correo
     * @param templatePath Ruta al archivo HTML de la plantilla
     * @param username     Nombre de usuario
     * @throws MessagingException Si ocurre un error al enviar el correo
     * @throws IOException        Si ocurre un error al leer el archivo HTML
     */
    public void sendAccountActivatedEmail(String to, String subject, String templatePath, String username) throws MessagingException, IOException {
        // Leer la plantilla HTML desde el archivo
        String htmlContent;
        try {
            htmlContent = new String(Files.readAllBytes(Paths.get(templatePath)));
        } catch (IOException e) {
            throw new IOException("Error al leer la plantilla del correo: " + templatePath, e);
        }

        // Verificar que la plantilla contiene el marcador requerido
        if (!htmlContent.contains("{{username}}")) {
            throw new IllegalArgumentException("La plantilla no contiene el marcador {{username}}");
        }

        // Reemplazar el marcador con el nombre de usuario
        htmlContent = htmlContent.replace("{{username}}", username);

        // Configurar el mensaje MIME
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        // Enviar el correo
        javaMailSender.send(message);
    }

}