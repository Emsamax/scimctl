package com.worldline.mts.idm.scimctl.commands.schema_cmd;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@ApplicationScoped
@CommandLine.Command(name = "schema")
public class ScimSchema implements Runnable{

    private static Logger LOGGER = LoggerFactory.getLogger(ScimSchema.class);

    @Inject
    SchemaService schemaService;

    @Override
    public void run() {
        try {
            LOGGER.info("get SCHEMA : `{}`", schemaService.getSchema());
        }catch (BadRequestException e){
            LOGGER.error("bad request : `{}`",  e.getMessage());
        }

    }
}
