package com.worldline.mts.idm.scimctl.config;

import org.jboss.logging.Logger;

import io.quarkus.oidc.client.OidcClientException;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

public class AppLifeCycleBean {
  private static final Logger LOGGER = Logger.getLogger(AppLifeCycleBean.class);

  /**
   * Inject a bean used in the callbacks.
   */
  @Inject
  ExceptionMapper bean;

  void onStart(@Observes StartupEvent ev) {
    try{
      LOGGER.info("The application is starting");
    }catch(OidcClientException e){
      System.err.println("zfzvqQRVQEVR  ");
    }
    
    
  }
}