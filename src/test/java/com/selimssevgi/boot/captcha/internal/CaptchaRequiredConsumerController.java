package com.selimssevgi.boot.captcha.internal;

import com.selimssevgi.boot.captcha.api.CaptchaRequired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simulates client usage of captcha annotation.
 *
 * @author selimssevgi
 */
@RestController
public class CaptchaRequiredConsumerController {

  @CaptchaRequired
  @GetMapping("/captcha-required")
  public void requiresCaptcha() {}

  @GetMapping("/captcha-not-required")
  public void doesNotRequireCaptcha() {}
}
