package hu.congressline.pcs.web.rest;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hu.congressline.pcs.config.DefaultProfileUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class ProfileInfoResource {

    private final Environment env;

    @GetMapping("/profile-info")
    public ProfileInfoResponse getActiveProfiles() {
        return new ProfileInfoResponse(DefaultProfileUtil.getActiveProfiles(env), getRibbonEnv());
    }

    private String getRibbonEnv() {
        String[] activeProfiles = DefaultProfileUtil.getActiveProfiles(env);
        String[] displayOnActiveProfiles = env.getProperty("info.display-ribbon-on-profiles", String[].class);

        if (displayOnActiveProfiles == null) {
            return null;
        }

        List<String> ribbonProfiles = new ArrayList<>(Arrays.asList(displayOnActiveProfiles));
        List<String> springBootProfiles = Arrays.asList(activeProfiles);
        ribbonProfiles.retainAll(springBootProfiles);

        if (!ribbonProfiles.isEmpty()) {
            return ribbonProfiles.getFirst();
        }
        return null;
    }

    @Getter
    private static class ProfileInfoResponse {

        private final String[] activeProfiles;
        private final String ribbonEnv;

        ProfileInfoResponse(String[] activeProfiles, String ribbonEnv) {
            this.activeProfiles = activeProfiles;
            this.ribbonEnv = ribbonEnv;
        }
    }
}
