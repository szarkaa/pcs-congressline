package hu.congressline.pcs.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "pcs_file")
public class PcsFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 100)
    @Column(name = "name", length = 100)
    private String name;

    @Size(max = 100)
    @Column(name = "file_content_type", nullable = false, length = 100)
    private String fileContentType;

    @Lob
    @Size(max = 5000000)
    @Column(name = "file", nullable = false, length = 500000)
    private byte[] file;

    @Column(name = "online_registration_id", nullable = false)
    private Long onlineRegistrationId;

    @Override
    public String toString() {
        return "PcsFile{" + "id=" + id + ", name='" + name + "'" + ", fileContentType='" + fileContentType + "'}";
    }
}
