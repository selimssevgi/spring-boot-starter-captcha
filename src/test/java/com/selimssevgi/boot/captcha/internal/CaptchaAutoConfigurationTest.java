package com.selimssevgi.boot.captcha.internal;

import com.google.code.kaptcha.Producer;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests auto-configuration.
 *
 * @author selimssevgi
 */
public class CaptchaAutoConfigurationTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(CaptchaAutoConfiguration.class));

  @Test
  public void enablesCaptchaAutoConfigurationByDefault() {
    this.contextRunner
        .run(context -> {
          assertThat(context).hasSingleBean(CaptchaController.class);
          assertThat(context).hasSingleBean(CaptchaProperties.class);
          assertThat(context).hasSingleBean(CaptchaAspect.class);
          assertThat(context).hasSingleBean(Producer.class);
        });
  }

  @Test
  public void canBeDisabled() {
    this.contextRunner
        .withPropertyValues("captcha.enabled=false")
        .run(context -> {
          assertThat(context).doesNotHaveBean(CaptchaController.class);
          assertThat(context).doesNotHaveBean(CaptchaProperties.class);
          assertThat(context).doesNotHaveBean(CaptchaAspect.class);
          assertThat(context).doesNotHaveBean(Producer.class);
        });

  }
}
