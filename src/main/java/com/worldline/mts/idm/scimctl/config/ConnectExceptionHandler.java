package com.worldline.mts.idm.scimctl.config;

import java.net.ConnectException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class ConnectExceptionHandler implements ExceptionMapper<ConnectException> {

  @Override
  public Response toResponse(ConnectException exception) {
    System.err.println("oidc server not available");
    return Response.status(Response.Status.UNAUTHORIZED).build();
  }

}
