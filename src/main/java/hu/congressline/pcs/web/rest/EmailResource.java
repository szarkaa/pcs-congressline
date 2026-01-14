package hu.congressline.pcs.web.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.service.CongressService;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.EmailVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class EmailResource {

    //private final MailService mailService;
    private final CongressService congressService;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/email/send")
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody EmailVM email) throws URISyntaxException {
        log.debug("REST request to send email: {}", email);
        final Congress congress = congressService.getById(Long.valueOf(email.getCongressId()));
        //mailService.sendEmail(congress.getContactEmail(), email.getEmail(), "Congressline Kft.", email.getBody(), false, true);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityCreationAlert("emailDialog", email.getEmail())).build();
    }

}
