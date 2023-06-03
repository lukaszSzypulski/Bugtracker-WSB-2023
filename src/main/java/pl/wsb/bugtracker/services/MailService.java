package pl.wsb.bugtracker.services;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import wsb.bugtracker.models.Mail;

@Service
@Slf4j
public class MailService {

    final private JavaMailSender javaMailSender;


    @Autowired
    public MailService(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }

    public void sendMail(Mail mail){
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setTo(mail.getRecipient());
            mimeMessageHelper.setSubject(mail.getSubject());
            mimeMessageHelper.setText(mail.getContent());

            mimeMessageHelper.addAttachment(mail.getAttachment().getOriginalFilename(), mail.getAttachment());

            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            log.info("Nie udalo sie wyslac wiadomosci");
        }

    }
}
