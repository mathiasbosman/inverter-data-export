package be.mathiasbosman.inverterdataexport.domain;

import lombok.Getter;
import org.slf4j.event.Level;

/**
 * Exception used for validations.
 */
@Getter
public class ExporterException extends RuntimeException {

  private final Level logLevel;

  public ExporterException(Level logLevel, String message, Object... args) {
    super(String.format(message, args));
    this.logLevel = logLevel;
  }
}
