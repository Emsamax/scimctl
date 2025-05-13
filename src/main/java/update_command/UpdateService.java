package update_command;

import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@ApplicationScoped
@Named("UpdateService")
@Unremovable
public class UpdateService {
}
