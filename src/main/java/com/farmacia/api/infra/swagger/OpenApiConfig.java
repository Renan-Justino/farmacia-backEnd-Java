package com.farmacia.api.infra.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração centralizada do OpenAPI/Swagger para a API.
 * Define:
 * - Metadados da API (título, versão, descrição)
 * - Esquemas de segurança (JWT Bearer)
 * - Organização de Tags por domínio
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Farmácia API",
                version = "v1",
                description = "Plataforma integrada para gestão farmacêutica, controle de inventário e operações de venda."
        ),
        security = @SecurityRequirement(name = "bearerAuth"),
        tags = {
                @Tag(name = "Auth", description = "Gerenciamento de autenticação e tokens JWT"),
                @Tag(name = "Cliente", description = "Operações de cadastro, manutenção e consulta de clientes"),
                @Tag(name = "Categoria", description = "Classificação e organização de produtos"),
                @Tag(name = "Medicamento", description = "Catálogo de produtos, especificações e preços"),
                @Tag(name = "Venda", description = "Transações comerciais e histórico de pedidos"),
                @Tag(name = "Estoque", description = "Monitoramento de entradas e saídas de estoque"),
                @Tag(name = "Alerta", description = "Serviços de monitoramento proativo para itens críticos e validade")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Insira o token JWT obtido no login para autenticar as requisições."
)
public class OpenApiConfig {
        // Classe apenas de configuração; todas as definições estão nas anotações.
}
