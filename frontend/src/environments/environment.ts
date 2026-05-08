export const environment = {
  apiBaseUrl: '/api',
  authBaseUrl: '/auth',
  /**
   * Swagger UI jest serwowany przez Spring Boot. Na `ng serve` (port 4200) ścieżki *.html
   * są przejmowane przez middleware SPA zanim zadziała proxy — iframe na backend jest niezawodny.
   */
  swaggerUiUrl: 'http://localhost:8080/swagger-ui/index.html'
};
