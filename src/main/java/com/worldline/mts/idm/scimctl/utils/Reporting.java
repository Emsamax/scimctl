package com.worldline.mts.idm.scimctl.utils;

import java.util.HashMap;

import de.captaingoldfish.scim.sdk.common.response.BulkResponse;
import de.captaingoldfish.scim.sdk.common.response.BulkResponseOperation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Reporting {

  @Inject
  OutputUtils utils;

  private int totalOperation = 0;
  private int successfullOperations = 0;
  private int failedOperations = 0;

  public void report(BulkResponse response) {
    var listResponse = response.getBulkResponseOperations();
    var report = new HashMap<String, String>();
    listResponse.stream()
        .forEach(op -> report.put(op.getBulkId().get(), getDetails(op)));

    totalOperation = failedOperations + successfullOperations;
    utils.logMsg(
        "Total operations : " + totalOperation + "\nSuccessfulOperations : " + successfullOperations
            + "\nFailed operations : " + failedOperations);
    for (var entry : report.entrySet()) {
      utils.logMsg(
          "operation n° " + entry.getKey() + " : resource " + entry.getValue() + " created");
    }
    this.failedOperations = 0;
    this.totalOperation = 0;
    this.successfullOperations = 0;
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
