package com.worldline.mts.idm.scimctl.config;

import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.Serial;
import java.io.Serializable;

@ApplicationScoped
@Unremovable
public class CustomExceptionHandler extends Exception implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  public CustomExceptionHandler() {
    super();
  }

  public CustomExceptionHandler(String message) {
    super(message);
  }

  public CustomExceptionHandler(String message, Throwable cause) {
    super(message, cause);
  }

  public CustomExceptionHandler(String message, Exception e) {
    super(message, e);
  }
}
