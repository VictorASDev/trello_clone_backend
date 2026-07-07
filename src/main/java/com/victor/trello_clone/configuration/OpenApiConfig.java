package com.victor.trello_clone.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Kanban Board API")
                        .version("v1")
                        .description("API REST para gerenciamento de quadros Kanban, permitindo criar boards, listas e cartões, organizar tarefas por colunas, atribuir responsáveis e acompanhar o fluxo de trabalho de equipes de forma simples e eficiente.")
                        .termsOfService("https://github.com/VictorASDev")
                        .license(new License()
                                .name("Apache License 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")
                        )
                );
    }
}
