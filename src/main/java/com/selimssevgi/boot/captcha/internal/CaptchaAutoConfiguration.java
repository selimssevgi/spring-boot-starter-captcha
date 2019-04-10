package com.selimssevgi.boot.captcha.internal;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * AutoConfigures captcha components.
 *
 * @author selimssevgi
 */
@Configuration
@ComponentScan(basePackageClasses = CaptchaInternalPackageMarker.class)
@ConditionalOnProperty(name = "captcha.enabled", havingValue = "true", matchIfMissing = true)
public class CaptchaAutoConfiguration {

  /**
   * Creates a captcha provider from third-party library.
   */
  @Bean
  public Producer captchaProvider(CaptchaProperties props) {
    Properties properties = new Properties();
    properties.putAll(props.getKaptchaProperties());

    DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
    defaultKaptcha.setConfig(new Config(properties));
    return defaultKaptcha;
  }
}
