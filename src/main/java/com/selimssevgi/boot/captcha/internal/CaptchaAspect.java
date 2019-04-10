package com.selimssevgi.boot.captcha.internal;

import com.selimssevgi.boot.captcha.api.CaptchaCookieNotFound;
import com.selimssevgi.boot.captcha.api.CaptchaHeaderNotFound;
import com.selimssevgi.boot.captcha.api.CaptchaNotMatchedException;
import lombok.AllArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

/**
 * Pointcuts the request handlers that are marked by the annotation.
 *
 * @author selimssevgi
 */
@Aspect
@Component
@AllArgsConstructor
public class CaptchaAspect {

  private final CaptchaProperties captchaProperties;

  /**
   * Compares shown and entered captcha values before letting it go.
   * Expects shown captcha value from a specific cookie.
   * Expects entered captcha value from a specific header.
   * If both value matches, does nothing, and let the request continue.
   * If they do not match, it throws specific case exceptions.
   */
  @Before("@annotation(com.selimssevgi.boot.captcha.api.CaptchaRequired)")
  public void validateCaptcha() {
    ServletRequestAttributes currentRequestAttributes =
        (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    HttpServletRequest request = currentRequestAttributes.getRequest();

    String enteredCaptchaText =
        getEnteredCaptcha(request)
            .orElseThrow(CaptchaHeaderNotFound::new);

    String shownCaptchaText =
        getShowedCaptcha(request)
            .orElseThrow(CaptchaCookieNotFound::new);

    if (!enteredCaptchaText.equals(shownCaptchaText)) {
      throw new CaptchaNotMatchedException();
    }

  }

  private Optional<String> getShowedCaptcha(HttpServletRequest request) {
    Cookie[] cookies =
        Optional.ofNullable(request.getCookies()) // getCookie may return null
            .orElse(new Cookie[0]);

    return Arrays.stream(cookies)
        .filter(cookie -> captchaProperties.getCookieName().equals(cookie.getName()))
        .findAny()
        .map(Cookie::getValue);
  }

  private Optional<String> getEnteredCaptcha(HttpServletRequest request) {
    String headerName = captchaProperties.getHeaderName();

    String enteredCaptcha = request.getHeader(headerName);

    return Optional.ofNullable(enteredCaptcha);
  }
}
