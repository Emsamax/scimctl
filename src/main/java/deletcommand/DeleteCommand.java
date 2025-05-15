package deletcommand;

import common.SearchCommonOption;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@CommandLine.Command(name = "delete")
public class DeleteCommand implements Runnable {
    @CommandLine.ArgGroup(exclusive = false)
    SearchCommonOption options;

    @Inject
    DeletService service;

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteCommand.class);
    @Override
    public void run() {
        try {
            handleRequest();
        } catch (BadRequestException e) {
            LOGGER.error("bad request `{}`", e);
        } catch (IllegalArgumentException e) {
            LOGGER.error("id does not exist `{}`", e);
        }
    }

    private void handleRequest() {
        if(options == null){
            throw new BadRequestException("no resource specified");
        }
        if(options.id != null){
            service.deletUser(options.id);
        }

    }
}
