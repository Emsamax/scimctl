package com.worldline.mts.idm.scimctl.utils;

import static org.hamcrest.CoreMatchers.startsWithIgnoringCase;

import java.util.HashMap;

import org.antlr.v4.parse.ANTLRParser.ruleReturns_return;
import org.bouncycastle.jcajce.provider.asymmetric.ec.GMSignatureSpi.sm3WithSM2;

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
    System.out.println(response.toPrettyString());
    var listResponse = response.getBulkResponseOperations();
    var report = new HashMap<String, BulkResponseOperation>();
    listResponse.stream()
        .forEach(op -> {
          counter(op);
          report.put(op.getBulkId().get(), op);
        });

    totalOperation = failedOperations + successfullOperations;
    utils.logMsg(
        "Total operations : " + totalOperation + "\nSuccessfulOperations : " + successfullOperations
            + "\nFailed operations : " + failedOperations);
    for (var entry : report.entrySet()) {
      utils.logMsg(
          "operation n° " + entry.getKey() + "(" + getCreatedResource(entry.getValue()) + "):  "
              + getDetails(entry.getValue()) + " created");
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
    return op.get("id").asText();
  }

  private boolean contain(String match, String... str) {
    for (String s : str) {
      if (s.equals(match)) {
        return true;
      }
    }
    return false;
  }

  private String getCreatedResource(BulkResponseOperation op) {
    if (op.get("location") == null) {
      return "Resource";
    }
    var location = op.get("location").asText().split("/");
    if (contain("Users", location))
      return "User";
    if (contain("Groups", location))
      return "Group";
    return "Resouce";
  }

  private void counter(BulkResponseOperation op) {
    var status = op.getStatus();
    if (status == 409) {
      failedOperations++;
      return;
    }
    successfullOperations++;
  }

}
