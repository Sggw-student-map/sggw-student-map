package com.gwozdz1uuu.sggwstudentmap.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
public class OpenApiConfig {

    static final String BEARER_SCHEME_NAME = "bearer-jwt";

    @Bean
    public OpenAPI sggwOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("SGGW Student Map API")
                        .version("1.0")
                        .description("""
                                REST API aplikacji mapy dla studentów SGGW.

                                **Uwierzytelnienie:** większość endpointów wymaga nagłówka `Authorization: Bearer <access_token>`.
                                Access token zwraca `POST /auth/login` (pole `token`).
                                **Odświeżanie:** `POST /auth/refresh` używa ciasteczka HTTP-only `refreshToken` (ścieżka `/auth/refresh`).
                                """)
                        .contact(new Contact().name("SGGW Student Map")))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME_NAME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Access token JWT z odpowiedzi logowania (`token`).")));
    }

    @Bean
    public OpenApiCustomizer operationSecurityCustomizer() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }
            for (Map.Entry<String, PathItem> entry : openApi.getPaths().entrySet()) {
                String path = entry.getKey();
                PathItem pathItem = entry.getValue();
                Map<PathItem.HttpMethod, Operation> ops = pathItem.readOperationsMap();
                for (Map.Entry<PathItem.HttpMethod, Operation> op : ops.entrySet()) {
                    applySecurity(op.getValue(), op.getKey(), path);
                }
            }
        };
    }

    private void applySecurity(Operation operation, PathItem.HttpMethod method, String path) {
        if (operation == null) {
            return;
        }
        if (isPublic(method, path)) {
            operation.setSecurity(Collections.emptyList());
        } else {
            operation.setSecurity(List.of(new SecurityRequirement().addList(BEARER_SCHEME_NAME)));
        }
    }

    private boolean isPublic(PathItem.HttpMethod method, String path) {
        return switch (method) {
            case POST -> "/auth/login".equals(path)
                    || "/auth/refresh".equals(path)
                    || "/users".equals(path);
            case GET -> path.startsWith("/api/places")
                    || path.startsWith("/api/reviews")
                    || path.startsWith("/api/feed");
            default -> false;
        };
    }
}
