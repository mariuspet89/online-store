package eu.accesa.onlinestore.configuration;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

@Configuration
public class InternationalizationConfig {

    /*
     * The LocaleResolver interface has implementations that determine the current locale based on the session,
     * cookies, the Accept-Language header, or a fixed value.
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new CustomAcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        return localeResolver;
    }

    private static class CustomAcceptHeaderLocaleResolver extends AcceptHeaderLocaleResolver {

        private final List<Locale> supportedLocales = List.of(
                Locale.ENGLISH, Locale.GERMAN, new Locale("ro", ""));

        @NotNull
        @Override
        public Locale resolveLocale(HttpServletRequest request) {
            String acceptLanguage = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);
            if (StringUtils.isEmptyOrWhitespace(acceptLanguage)) {
                return Locale.ENGLISH;
            }

            Locale matchingLocale = Locale.lookup(Locale.LanguageRange.parse(acceptLanguage), supportedLocales);
            if (matchingLocale == null) {
                return Locale.ENGLISH;
            }

            return matchingLocale;
        }
    }
}
