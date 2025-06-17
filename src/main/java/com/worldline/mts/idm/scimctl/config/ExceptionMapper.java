package com.worldline.mts.idm.scimctl.config;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import io.quarkus.oidc.client.OidcClientException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;


@ApplicationScoped
public class ExceptionMapper {

  private final String name;

  ExceptionMapper() {
    this.name = "exceptionMapperBean";
  }

  @ServerExceptionMapper
  public RestResponse<String> mapException(OidcClientException e) {
    return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, "Oidc server not available");
  }

  @ServerExceptionMapper
  public RestResponse<String> mapException(java.net.ConnectException e) {
    return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, "Oidc server not available");
  }

}