package com.selimssevgi.boot.captcha.internal;

import com.google.code.kaptcha.Producer;
import com.selimssevgi.boot.captcha.api.CaptchaCookieNotFound;
import com.selimssevgi.boot.captcha.api.CaptchaHeaderNotFound;
import com.selimssevgi.boot.captcha.api.CaptchaNotMatchedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.Cookie;
import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End to end testing for captcha functionality.
 *
 * @author selimssevgi
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CaptchaEndToEndTest {

  private static final String GET_CAPTCHA_ENDPOINT = "/non-secured/captcha";

  private static final String FIX_TEST_CAPTCHA_TEXT = "eyK0b";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private CaptchaProperties captchaProperties;

  @SpyBean
  private CaptchaAspect captchaAspect;

  @MockBean
  private Producer mockedCaptchaProvider;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  /**
   * Sets up captcha provider for producing fix captcha text and image.
   */
  @Before
  public void setUp() {
    doReturn(FIX_TEST_CAPTCHA_TEXT)
        .when(mockedCaptchaProvider).createText();

    BufferedImage mockBi = new BufferedImage(1, 1, 1);

    doReturn(mockBi)
        .when(mockedCaptchaProvider).createImage(FIX_TEST_CAPTCHA_TEXT);

  }

  @Test
  public void providesEndpointForCaptcha() throws Exception {
    mockMvc.perform(get(GET_CAPTCHA_ENDPOINT))
        .andExpect(status().isOk());
  }

  @Test
  public void setsCaptchaCookie() throws Exception {
    mockMvc.perform(get(GET_CAPTCHA_ENDPOINT))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.IMAGE_JPEG))
        .andExpect(cookie().exists(captchaProperties.getCookieName()));
  }

  @Test
  public void doesNothingIfCaptchaNotRequired() throws Exception {
    mockMvc.perform(get("/captcha-not-required"))
        .andExpect(status().isOk());

    verify(captchaAspect, never()).validateCaptcha();
  }

  @Test
  public void preventsAccessWhenCaptchaRequiredButCookieNotProvided() throws Exception {
    try {
      mockMvc.perform(get("/captcha-required")
          .header(captchaProperties.getHeaderName(), "enteredCaptcha"));
      failBecauseExceptionWasNotThrown(CaptchaCookieNotFound.class);
    } catch (NestedServletException nse) {
      assertThat(nse).hasCauseExactlyInstanceOf(CaptchaCookieNotFound.class);
    }
  }

  @Test
  public void preventsAccessWhenCaptchaRequiredButHeaderNotProvided() throws Exception {
    try {
      mockMvc.perform(get("/captcha-required")
          .cookie(new Cookie(captchaProperties.getCookieName(), "shownCaptchaText")));
      failBecauseExceptionWasNotThrown(CaptchaHeaderNotFound.class);
    } catch (NestedServletException nse) {
      assertThat(nse).hasCauseExactlyInstanceOf(CaptchaHeaderNotFound.class);
    }
  }

  @Test
  public void preventsAccessWhenShownAndEnteredCaptchaDoNotMatch() throws Exception {
    Cookie setCookie = askForCaptcha();
    try {
      mockMvc.perform(get("/captcha-required")
          .header(captchaProperties.getHeaderName(), "wrongCaptcha")
          .cookie(setCookie));
      failBecauseExceptionWasNotThrown(CaptchaNotMatchedException.class);
    } catch (NestedServletException nse) {
      assertThat(nse).hasCauseExactlyInstanceOf(CaptchaNotMatchedException.class);
    }
  }

  @Test
  public void allowAccessWhenShownAndEnteredCaptchaMatch() throws Exception {
    Cookie setCookie = askForCaptcha();
    mockMvc.perform(get("/captcha-required")
        .header(captchaProperties.getHeaderName(), FIX_TEST_CAPTCHA_TEXT)
        .cookie(setCookie))
        .andExpect(status().isOk());
  }

  private Cookie askForCaptcha() throws Exception {
    MvcResult mvcResult =
        mockMvc.perform(get(GET_CAPTCHA_ENDPOINT))
            .andReturn();

    return mvcResult.getResponse().getCookie(captchaProperties.getCookieName());
  }
}
