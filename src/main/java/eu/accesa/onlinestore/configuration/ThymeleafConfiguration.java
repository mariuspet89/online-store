package eu.accesa.onlinestore.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.nio.charset.StandardCharsets;

@Configuration
public class ThymeleafConfiguration {

    /*
     * Template files can be shipped within the JAR file, which is the simplest way to maintain cohesion between
     * templates and their input data.
     *
     * To locate templates from the JAR, we use the ClassLoaderTemplateResolver. Our templates are in the
     * main/resources/mail-templates directory, so we set the "prefix" attribute relative to the resource directory.
     */
    @Bean
    public ITemplateResolver thymeleafTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("mail-templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return templateResolver;
    }

    /*
     * The final step is to create the factory method for the Thymeleaf engine. We'll need to tell the engine which
     * TemplateResolver we've chosen, which we can inject via a parameter to the bean factory method.
     *
     * Here, the resolver we created earlier is injected automatically by Spring into the template engine factory method.
     */
    @Bean
    public SpringTemplateEngine thymeleafTemplateEngine(ITemplateResolver thymeleafTemplateResolver) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(thymeleafTemplateResolver);
        return templateEngine;
    }
}
