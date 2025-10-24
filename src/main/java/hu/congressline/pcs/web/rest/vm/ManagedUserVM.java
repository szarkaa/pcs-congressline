package hu.congressline.pcs.web.rest.vm;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import hu.congressline.pcs.domain.User;
import hu.congressline.pcs.service.dto.UserDTO;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ManagedUserVM extends UserDTO {

    public static final int PASSWORD_MIN_LENGTH = 4;
    public static final int PASSWORD_MAX_LENGTH = 100;

    private Long id;

    private String createdBy;

    private ZonedDateTime createdDate;

    private String lastModifiedBy;

    private ZonedDateTime lastModifiedDate;

    private Set<CongressVM> congresses;

    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    public ManagedUserVM(User user) {
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
    public ManagedUserVM(Long id, String login, String password, String firstName, String lastName,
                         String email, boolean activated, String langKey, Set<String> authorities,
                         String createdBy, ZonedDateTime createdDate, String lastModifiedBy, ZonedDateTime lastModifiedDate) {
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
        return "ManagedUserVM{" + "id=" + id + "} " + super.toString();
    }
}
