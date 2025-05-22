package com.worldline.mts.idm.scimctl.config;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;


import java.io.IOException;

@Provider
public class ScimctlExceptionHandler implements ExceptionMapper<Throwable> {

  @Inject
  Logger LOGGER ;

  @Override
  public Response toResponse(Throwable exception) {
    Response.Status status = null;

    if(exception instanceof JsonProcessingException) {
      LOGGER.log(Logger.Level.valueOf("ERROR"), "Json processing exception", exception);
      status = Response.Status.BAD_REQUEST;
    }
    if(exception instanceof BadRequestException) {
      LOGGER.log(Logger.Level.valueOf("ERROR"), "Bad request exception", exception);
      status = Response.Status.BAD_REQUEST;
    }
    if(exception instanceof ClassCastException) {
      LOGGER.log(Logger.Level.valueOf("ERROR"), "Class cast exception", exception);
      status = Response.Status.BAD_REQUEST;
    }
    if(exception instanceof IllegalArgumentException) {
      LOGGER.log(Logger.Level.valueOf("ERROR"), "Illegal argument(s) exception", exception);
    }
    if(exception instanceof RuntimeException) {
      LOGGER.log(Logger.Level.valueOf("ERROR"), "Runtime exception", exception);
      status = Response.Status.INTERNAL_SERVER_ERROR;
    }
    if(exception instanceof IOException) {
      LOGGER.log(Logger.Level.valueOf("ERROR"), "IOException", exception);
      status = Response.Status.INTERNAL_SERVER_ERROR;
    }
    return Response.status(status).build();
  }
}
