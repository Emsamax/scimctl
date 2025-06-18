package com.worldline.mts.idm.scimctl.utils;

import java.util.HashMap;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

import de.captaingoldfish.scim.sdk.common.response.BulkResponse;
import de.captaingoldfish.scim.sdk.common.response.BulkResponseOperation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Reporting {

  @Inject
  OutputUtils utils;

  private static final Logger LOGGER = Logger.getLogger(Reporting.class);

  private int totalOperation = 0;
  private int successfullOperations = 0;
  private int failedOperations = 0;

  public void report(BulkResponse response) {
    var listResponse = response.getBulkResponseOperations();
    var report = new HashMap<String, String>();
    listResponse.stream()
        .forEach(op -> report.put(op.getBulkId().get(), getDetails(op)));

    totalOperation = failedOperations + successfullOperations;
    utils.logMsg(LOGGER, Logger.Level.INFO,
        "Total operations : " + totalOperation + "\nSuccessfulOperations : " + successfullOperations
            + "\nFailed operations : " + failedOperations);
    for (var entry : report.entrySet()) {
      utils.logMsg(LOGGER, Level.INFO, "operation n° " + entry.getKey() + " : resource " + entry.getValue() + " created");
    }
  }

  private String getDetails(BulkResponseOperation op) {
    var status = op.getStatus();
    if (status == 409) {
      failedOperations++;
      return op.get("response").get("detail").asText();
    }
    successfullOperations++;
    return op.get("bulkId").asText();
  }

}
