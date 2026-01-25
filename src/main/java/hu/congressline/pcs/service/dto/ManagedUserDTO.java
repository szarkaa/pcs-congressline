package hu.congressline.pcs.service.dto;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

import hu.congressline.pcs.domain.User;
import hu.congressline.pcs.web.rest.vm.CongressVM;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ManagedUserDTO extends UserDTO {

    public static final int PASSWORD_MIN_LENGTH = 4;
    public static final int PASSWORD_MAX_LENGTH = 100;

    private Long id;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private Set<CongressVM> congresses;

    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    public ManagedUserDTO(User user) {
        super(user);
        this.id = user.getId();
        this.createdBy = user.getCreatedBy();
        this.createdDate = user.getCreatedDate();
        this.lastModifiedBy = user.getLastModifiedBy();
        this.lastModifiedDate = user.getLastModifiedDate();
        this.password = null;
        this.congresses = user.getCongresses().stream().filter(congress -> !Boolean.TRUE.equals(congress.getArchive())).map(CongressVM::new).collect(Collectors.toSet());
    }

    @SuppressWarnings("ParameterNumber")
    public ManagedUserDTO(Long id, String login, String password, String firstName, String lastName,
                          String email, boolean activated, String langKey, Set<String> authorities,
                          String createdBy, Instant createdDate, String lastModifiedBy, Instant lastModifiedDate) {
        super(login, firstName, lastName, email, activated, langKey, authorities);
        this.id = id;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.lastModifiedBy = lastModifiedBy;
        this.lastModifiedDate = lastModifiedDate;
        this.password = password;
    }

    @Override
    public String toString() {
        return "ManagedUserDTO{" + "id=" + id + "} " + super.toString();
    }
}
