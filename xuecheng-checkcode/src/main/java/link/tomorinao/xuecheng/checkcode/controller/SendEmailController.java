package link.tomorinao.xuecheng.checkcode.controller;

import link.tomorinao.xuecheng.checkcode.service.SendEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SendEmailController {

    @Autowired
    SendEmailService sendEmailService;

    @PostMapping("/phone")
    public void sendEMail(@RequestParam("param1") String email) {
        sendEmailService.sendEmail(email);
    }
}
