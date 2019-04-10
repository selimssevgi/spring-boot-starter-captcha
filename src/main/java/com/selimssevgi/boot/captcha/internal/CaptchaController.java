package com.selimssevgi.boot.captcha.internal;

import com.google.code.kaptcha.Producer;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Provides a single endpoint for showing and refreshing captcha image.
 *
 * @author selimssevgi
 */
@RestController
@AllArgsConstructor
public class CaptchaController {

  private final Producer captchaProvider;

  private final CaptchaProperties captchaProperties;

  /**
   * Sends back a captcha image for showing client, and cookie for later validation.
   */
  @GetMapping("/non-secured/captcha")
  public void getCaptcha(HttpServletResponse response) throws IOException {

    response.setDateHeader("Expires", 0);
    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
    response.addHeader("Cache-Control", "post-check=0, pre-check=0");
    response.setHeader("Pragma", "no-cache");
    response.setContentType(MediaType.IMAGE_JPEG_VALUE);

    String captchaText = captchaProvider.createText();

    Cookie captchaCookie = toCookie(captchaText);
    response.addCookie(captchaCookie);

    BufferedImage captchaImage = captchaProvider.createImage(captchaText);

    try (ServletOutputStream outputStream = response.getOutputStream()) {
      ImageIO.write(captchaImage, "jpg", outputStream);
      outputStream.flush();
    }
  }

  private Cookie toCookie(String captchaText) {
    Cookie cookie = new Cookie(captchaProperties.getCookieName(), captchaText);
    cookie.setHttpOnly(true);
    cookie.setMaxAge(captchaProperties.getCookieMaxAgeInSeconds());
    cookie.setPath("/");
    return cookie;
  }
}
