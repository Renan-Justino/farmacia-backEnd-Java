package com.farmacia.api.infra.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração centralizada do ecossistema de documentação OpenAPI (Swagger).
 * * Esta classe define os metadados da API, esquemas de segurança e, principalmente,
 * a ordem lógica de exibição dos recursos através da declaração sequencial de Tags.
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
                @Tag(name = "Auth", description = "Gerenciamento de autenticação, segurança e tokens JWT"),
                @Tag(name = "Cliente", description = "Operações de cadastro, manutenção e consulta de clientes"),
                @Tag(name = "Categoria", description = "Classificação e organização mercadológica de produtos"),
                @Tag(name = "Medicamento", description = "Catálogo técnico de produtos, especificações e preços"),
                @Tag(name = "Venda", description = "Orquestração de transações comerciais e histórico de pedidos"),
                @Tag(name = "Estoque", description = "Monitoramento de movimentações, entradas e saídas físicas"),
                @Tag(name = "Alerta", description = "Serviços de monitoramento proativo para itens críticos e validade")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Insira o token JWT gerado no endpoint de login para autenticar as requisições."
)
public class OpenApiConfig {
        /*
         * Nota de Arquitetura:
         * A ordem definida na lista 'tags' acima dita a prioridade de exibição no Swagger UI.
         * Certifique-se de que cada Controller utilize a anotação @Tag(name = "NOME_EXATO")
         * para que os endpoints sejam agrupados sob as definições e descrições aqui centralizadas.
         */
}