package br.com.clinicaveterinaria.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração da documentação interativa Swagger / OpenAPI 3.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("VetCare Fauna & Domésticos - API Clínica Veterinária")
                        .description("Sistema Monolítico para Gestão de Clínica e Consultoria Veterinária Especializada em Pets Domésticos, Silvestres e Exóticos.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe VetCare")
                                .email("suporte@vetcare.com.br")
                                .url("https://vetcare.com.br"))
                        .license(new License()
                                .name("Proprietário / Uso Interno")
                                .url("https://vetcare.com.br/licenca")));
    }
}
