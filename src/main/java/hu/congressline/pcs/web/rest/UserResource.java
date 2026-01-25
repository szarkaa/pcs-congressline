package hu.congressline.pcs.web.rest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import hu.congressline.pcs.config.Constants;
import hu.congressline.pcs.domain.User;
import hu.congressline.pcs.repository.UserRepository;
import hu.congressline.pcs.security.AuthoritiesConstants;
import hu.congressline.pcs.service.UserService;
import hu.congressline.pcs.service.dto.ManagedUserDTO;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.util.PaginationUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class UserResource {
    private static final String ENTITY_NAME = "userManagement";
    private static final String USER_EXISTS = "userexists";
    private static final String EMAIL_EXISTS = "emailexists";
    private static final String LOGIN_ALREADY_IN_USE = "Login already in use";

    private final UserRepository userRepository;
    //private final MailService mailService;
    private final UserService userService;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/users")
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<?> create(@RequestBody ManagedUserDTO managedUserDTO, HttpServletRequest request) throws URISyntaxException {
        log.debug("REST request to save User : {}", managedUserDTO);

        //Lowercase the user login before comparing with database
        if (userRepository.findOneByLogin(managedUserDTO.getLogin().toLowerCase()).isPresent()) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, USER_EXISTS, LOGIN_ALREADY_IN_USE))
                .body(null);
        } else if (userRepository.findOneByEmail(managedUserDTO.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert("currency", EMAIL_EXISTS, "Email already in use"))
                .body(null);
        } else {
            User newUser = userService.createUser(managedUserDTO);
            String baseUrl = request.getScheme() // "http"
                + "://"                              // "://"
                + request.getServerName()            // "myhost"
                + ":"                                // ":"
                + request.getServerPort()            // "80"
                + request.getContextPath();          // "/myContextPath" or "" if deployed in root context
            //mailService.sendCreationEmail(newUser, baseUrl);
            return ResponseEntity.created(new URI("/api/users/" + newUser.getLogin()))
                .headers(HeaderUtil.createAlert("userManagement.created", newUser.getLogin()))
                .body(newUser);
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/users")
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<ManagedUserDTO> update(@RequestBody ManagedUserDTO managedUserDTO) {
        log.debug("REST request to update User : {}", managedUserDTO);
        Optional<User> existingUser = userRepository.findOneByEmail(managedUserDTO.getEmail());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(managedUserDTO.getId())) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, EMAIL_EXISTS, "E-mail already in use"))
                .body(null);
        }
        existingUser = userRepository.findOneByLogin(managedUserDTO.getLogin().toLowerCase());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(managedUserDTO.getId())) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, USER_EXISTS, LOGIN_ALREADY_IN_USE))
                .body(null);
        }
        userService.updateUser(managedUserDTO.getId(), managedUserDTO.getLogin(), managedUserDTO.getFirstName(),
            managedUserDTO.getLastName(), managedUserDTO.getEmail(), managedUserDTO.isActivated(),
            managedUserDTO.getLangKey(), managedUserDTO.getAuthorities(), managedUserDTO.getCongresses());

        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert("userManagement.updated", managedUserDTO.getLogin()))
            .body(new ManagedUserDTO(userService.getUserWithAuthorities(managedUserDTO.getId())));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/users")
    public ResponseEntity<List<ManagedUserDTO>> getAll(Pageable pageable)
        throws URISyntaxException {
        Page<User> page = userService.findAllEagerly(pageable);
        List<ManagedUserDTO> dtos = page.getContent().stream()
            .map(ManagedUserDTO::new)
            .collect(Collectors.toList());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/users");
        return new ResponseEntity<>(dtos, headers, HttpStatus.OK);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
    public ResponseEntity<ManagedUserDTO> getByLogin(@PathVariable String login) {
        log.debug("REST request to get User : {}", login);
        return userService.getUserWithAuthoritiesByLogin(login)
                .map(ManagedUserDTO::new)
                .map(managedUserDTO -> new ResponseEntity<>(managedUserDTO, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> delete(@PathVariable String login) {
        log.debug("REST request to delete User: {}", login);
        try {
            userService.deleteUser(login);
            return ResponseEntity.ok().headers(HeaderUtil.createAlert("userManagement.deleted", login)).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constration violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }

    }
}
