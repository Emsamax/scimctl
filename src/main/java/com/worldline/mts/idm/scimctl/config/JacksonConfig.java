package com.worldline.mts.idm.scimctl.config;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;
import jdk.jfr.Name;

@ApplicationScoped
@Unremovable
public class JacksonConfig {
  @Name("csvMapper")
  @Produces
  @ApplicationScoped
  public CsvMapper csvMapper() {
    return new CsvMapper();
  }
}
