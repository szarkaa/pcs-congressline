package hu.congressline.pcs.web.rest.vm;

import com.fasterxml.jackson.annotation.JsonCreator;

import ch.qos.logback.classic.Logger;
import lombok.Data;

@Data
public class LoggerVM {

    private String name;
    private String level;

    public LoggerVM(Logger logger) {
        this.name = logger.getName();
        this.level = logger.getEffectiveLevel().toString();
    }

    @JsonCreator
    public LoggerVM() {
    }

}
