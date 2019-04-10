package com.selimssevgi.boot.captcha.internal;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * Properties to configure captcha functionality.
 *
 * @author selimssevgi
 */
@Data
@Component
@ConfigurationProperties(prefix = "saptcha")
public class CaptchaProperties {

  /**
   * use Constants for keys.
   */
  private Map<String, String> kaptchaProperties;

  private String headerName = "CAPTCHA_HEADER";
  private String cookieName = "CAPTCHA_COOKIE";
  //TODO:selimssevgi: check spring properties for Duration
  private int cookieMaxAgeInSeconds = 60 * 60;

  /**
   * Do not return null.
   */
  public Map<String, String> getKaptchaProperties() {
    if (kaptchaProperties == null) {
      return Collections.emptyMap();
    }
    return kaptchaProperties;
  }
}
