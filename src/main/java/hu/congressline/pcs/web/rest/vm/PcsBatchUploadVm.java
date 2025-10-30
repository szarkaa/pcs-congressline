package hu.congressline.pcs.web.rest.vm;

import java.io.Serial;
import java.io.Serializable;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PcsBatchUploadVm implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Size(max = 200)
    private String name;
    @Size(max = 5000000)
    private byte[] file;
    @Size(max = 200)
    private String fileContentType;
    private Long congressId;
}
