package hu.congressline.pcs.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import hu.congressline.pcs.domain.Authority;
import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.User;
import hu.congressline.pcs.repository.AuthorityRepository;
import hu.congressline.pcs.repository.PersistentTokenRepository;
import hu.congressline.pcs.repository.UserRepository;
import hu.congressline.pcs.security.AuthoritiesConstants;
import hu.congressline.pcs.security.RandomUtil;
import hu.congressline.pcs.security.SecurityUtils;
import hu.congressline.pcs.web.rest.vm.ManagedUserVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserService {
    private static final String NO_LOGGED_IN_USER_FOUND = "No logged in user found!";
    private static final String AUTHORITY_NOT_FOUND = "Authority not found: ";

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final CongressService congressService;
    private final PersistentTokenRepository persistentTokenRepository;
    private final AuthorityRepository authorityRepository;

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Page<User> findAllEagerly(Pageable pageable) {
        Page<Long> idPage = userRepository.findAllIds(pageable);

        if (idPage.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, idPage.getTotalElements());
        }

        List<User> users = userRepository.findAllEagerlyByIdIn(idPage.getContent());

        // Preserve page order (IN (...) does not guarantee ordering)
        Map<Long, User> byId = users.stream().collect(Collectors.toMap(User::getId, Function.identity()));

        List<User> ordered = idPage.getContent().stream()
            .map(byId::get)
            .filter(Objects::nonNull)
            .toList();

        return new PageImpl<>(ordered, pageable, idPage.getTotalElements());
    }

    @SuppressWarnings("MissingJavadocMethod")
    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                userRepository.save(user);
                log.debug("Activated user: {}", user);
                return user;
            });
    }

    @SuppressWarnings("MissingJavadocMethod")
    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return userRepository.findOneByResetKey(key).filter(user -> {
            Instant oneDayAgo = Instant.now().minusSeconds(24 * 60 * 60);
            return user.getResetDate().isAfter(oneDayAgo);
        }).map(user -> {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetKey(null);
            user.setResetDate(null);
            userRepository.save(user);
            return user;
        });
    }

    @SuppressWarnings("MissingJavadocMethod")
    public Optional<User> requestPasswordReset(String mail) {
        return userRepository.findOneByEmail(mail)
            .filter(User::isActivated)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                userRepository.save(user);
                return user;
            });
    }

    @SuppressWarnings("MissingJavadocMethod")
    public User createUser(String login, String password, String firstName, String lastName, String email, String langKey) {
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(login);
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setLangKey(langKey);
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        Authority authority = authorityRepository.findById(AuthoritiesConstants.USER).orElse(null);
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        log.debug("Created Info for User: {}", newUser);
        return newUser;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public User createUser(ManagedUserVM viewModel) {
        User user = new User();
        user.setLogin(viewModel.getLogin());
        user.setFirstName(viewModel.getFirstName());
        user.setLastName(viewModel.getLastName());
        user.setEmail(viewModel.getEmail());
        if (viewModel.getLangKey() == null) {
            user.setLangKey("en"); // default language
        } else {
            user.setLangKey(viewModel.getLangKey());
        }
        if (viewModel.getAuthorities() != null) {
            Set<Authority> authorities = new HashSet<>();
            viewModel.getAuthorities().forEach(
                    authority -> authorities.add(authorityRepository.findById(authority).orElseThrow(() -> new IllegalArgumentException(AUTHORITY_NOT_FOUND + authority)))
            );
            user.setAuthorities(authorities);
        }

        if (viewModel.getCongressIds() != null) {
            Set<Congress> congresses = new HashSet<>();
            viewModel.getCongressIds().forEach(congressId -> congresses.add(congressService.getById(congressId)));
            user.setCongresses(congresses);
        }
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(RandomUtil.generateResetKey());
        user.setResetDate(Instant.now());
        user.setActivated(true);
        userRepository.save(user);
        log.debug("Created Information for User: {}", user);
        return user;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void updateUser(String firstName, String lastName, String email, String langKey) {
        userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalArgumentException(NO_LOGGED_IN_USER_FOUND))).ifPresent(u -> {
            u.setFirstName(firstName);
            u.setLastName(lastName);
            u.setEmail(email);
            u.setLangKey(langKey);
            userRepository.save(u);
            log.debug("Updated information for User: {}", u);
        });
    }

    @SuppressWarnings({"MissingJavadocMethod", "ParameterNumber"})
    public void updateUser(Long id, String login, String firstName, String lastName, String email,
        boolean activated, String langKey, Set<String> authorities, Set<Long> congressIds) {

        userRepository.findOneById(id)
            .ifPresent(u -> {
                u.setLogin(login);
                u.setFirstName(firstName);
                u.setLastName(lastName);
                u.setEmail(email);
                u.setActivated(activated);
                u.setLangKey(langKey);
                Set<Authority> managedAuthorities = u.getAuthorities();
                managedAuthorities.clear();
                authorities.forEach(
                    authority -> managedAuthorities.add(authorityRepository.findById(authority)
                            .orElseThrow(() -> new IllegalArgumentException(AUTHORITY_NOT_FOUND + authority)))
                );
                Set<Congress> managedCongresses = u.getCongresses();
                managedCongresses.clear();
                congressIds.forEach(congressId -> managedCongresses.add(congressService.getById(congressId))
                );

                log.debug("Changed Information for User: {}", u);
            });
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void deleteUser(String login) {
        userRepository.findOneByLogin(login).ifPresent(u -> {
            userRepository.delete(u);
            log.debug("Deleted User: {}", u);
        });
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void changePassword(String password) {
        userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalArgumentException(NO_LOGGED_IN_USER_FOUND))).ifPresent(u -> {
            String encryptedPassword = passwordEncoder.encode(password);
            u.setPassword(encryptedPassword);
            userRepository.save(u);
            log.debug("Changed password for User: {}", u);
        });
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneByLogin(login).map(u -> {
            u.getAuthorities().size();
            u.getCongresses().size();
            return u;
        });
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public User getUserWithAuthorities(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found by id: " + id));
        user.getAuthorities().size(); // eagerly load the association
        user.getCongresses().size();
        return user;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public User getUserWithAuthorities() {
        Optional<User> optionalUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new IllegalArgumentException(NO_LOGGED_IN_USER_FOUND)));
        User user = null;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            user.getAuthorities().size(); // eagerly load the association
            user.getCongresses().size();
        }
        return user;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeOldPersistentTokens() {
        LocalDate now = LocalDate.now();
        persistentTokenRepository.findByTokenDateBefore(now.minusMonths(1)).forEach(token -> {
            log.debug("Deleting token {}", token.getSeries());
            User user = token.getUser();
            user.getPersistentTokens().remove(token);
            persistentTokenRepository.delete(token);
        });
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS));
        for (User user : users) {
            log.debug("Deleting not activated user {}", user.getLogin());
            userRepository.delete(user);
        }
    }
}
