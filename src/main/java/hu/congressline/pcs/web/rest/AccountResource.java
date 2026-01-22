package hu.congressline.pcs.web.rest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import hu.congressline.pcs.config.PcsProperties;
import hu.congressline.pcs.domain.PersistentToken;
import hu.congressline.pcs.domain.User;
import hu.congressline.pcs.repository.PersistentTokenRepository;
import hu.congressline.pcs.repository.UserRepository;
import hu.congressline.pcs.security.SecurityUtils;
import hu.congressline.pcs.service.MailService;
import hu.congressline.pcs.service.UserService;
import hu.congressline.pcs.service.dto.UserDTO;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.KeyAndPasswordVM;
import hu.congressline.pcs.web.rest.vm.ManagedUserVM;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AccountResource {
    private static final String INCORRECT_PASSWORD = "Incorrect password";

    private final UserRepository userRepository;
    private final UserService userService;
    private final PersistentTokenRepository persistentTokenRepository;
    private final MailService mailService;
    private final PcsProperties properties;

    @SuppressWarnings("MissingJavadocMethod")
    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<?> registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM, HttpServletRequest request) {

        HttpHeaders textPlainHeaders = new HttpHeaders();
        textPlainHeaders.setContentType(MediaType.TEXT_PLAIN);

        return userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase())
            .map(user -> new ResponseEntity<>("login already in use", textPlainHeaders, HttpStatus.BAD_REQUEST))
            .orElseGet(() -> userRepository.findOneByEmail(managedUserVM.getEmail())
                .map(user -> new ResponseEntity<>("e-mail address already in use", textPlainHeaders, HttpStatus.BAD_REQUEST))
                .orElseGet(() -> {
                    User user = userService.createUser(managedUserVM.getLogin(), managedUserVM.getPassword(),
                        managedUserVM.getFirstName(), managedUserVM.getLastName(), managedUserVM.getEmail().toLowerCase(),
                        managedUserVM.getLangKey());
                    String baseUrl = request.getScheme() // "http"
                        + "://"                          // "://"
                        + request.getServerName()        // "myhost"
                        + ":"                            // ":"
                        + request.getServerPort()        // "80"
                        + request.getContextPath();      // "/myContextPath" or "" if deployed in root context

                    //mailService.sendActivationEmail(user, baseUrl);
                    return new ResponseEntity<>(HttpStatus.CREATED);
                })
            );
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam("key") String key) {
        return userService.activateRegistration(key)
            .map(user -> new ResponseEntity<String>(HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/account")
    public ResponseEntity<UserDTO> getAccount() {
        return Optional.ofNullable(userService.getUserWithAuthorities())
            .map(user -> new ResponseEntity<>(new UserDTO(user), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/account")
    public ResponseEntity<String> saveAccount(@Valid @RequestBody UserDTO userDTO) {
        Optional<User> existingUser = userRepository.findOneByEmail(userDTO.getEmail());
        if (existingUser.isPresent() && !existingUser.get().getLogin().equalsIgnoreCase(userDTO.getLogin())) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("user-management", "emailexists",
                "Email already in use")).body(null);
        }

        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .map(u -> {
                userService.updateUser(
                    userDTO.getFirstName(),
                    userDTO.getLastName(),
                    userDTO.getEmail(),
                    userDTO.getLangKey()
                );
                return new ResponseEntity<String>(HttpStatus.OK);
            })
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @RequestMapping(value = "/account/change_password", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> changePassword(@RequestBody String password) {
        if (!checkPasswordLength(password)) {
            return new ResponseEntity<>(INCORRECT_PASSWORD, HttpStatus.BAD_REQUEST);
        }
        userService.changePassword(password);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/account/sessions")
    public ResponseEntity<List<PersistentToken>> getCurrentSessions() {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .map(user -> new ResponseEntity<>(
                persistentTokenRepository.findByUser(user),
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/account/sessions/{series}")
    public void invalidateSession(@PathVariable String series) throws UnsupportedEncodingException {
        String decodedSeries = URLDecoder.decode(series, StandardCharsets.UTF_8);
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin).flatMap(u -> persistentTokenRepository.findByUser(u).stream()
                .filter(persistentToken -> Objects.equals(persistentToken.getSeries(), decodedSeries))
                .findAny()).ifPresent(persistentTokenRepository::delete);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @RequestMapping(value = "/account/reset-password/init", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> requestPasswordReset(@RequestBody String mail, HttpServletRequest request) {
        return userService.requestPasswordReset(mail)
            .map(user -> {
                mailService.sendPasswordResetMail(user);
                return new ResponseEntity<>("e-mail was sent", HttpStatus.OK);
            }).orElse(new ResponseEntity<>("e-mail address not registered", HttpStatus.BAD_REQUEST));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @RequestMapping(value = "/account/reset-password/finish", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
            return new ResponseEntity<>(INCORRECT_PASSWORD, HttpStatus.BAD_REQUEST);
        }

        return userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey())
            .map(user -> new ResponseEntity<String>(HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) && password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH && password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
    }
}
