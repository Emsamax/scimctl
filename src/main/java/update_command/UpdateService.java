package update_command;

import cli.ClientConfig;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@ApplicationScoped
@Named("UpdateService")
@Unremovable
public class UpdateService {

    @Inject
    public UpdateService() {
    }

    @Inject
    ClientConfig config;

    public void updateUser(String id, String path) {
        User newUser =
    }
}
