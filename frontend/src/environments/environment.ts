export const environment = {
  apiBaseUrl: 'https://sggw-student-map-515453688242.europe-west3.run.app',
  authBaseUrl: 'https://sggw-student-map-515453688242.europe-west3.run.app/auth',
  /**
   * Swagger UI jest serwowany przez Spring Boot. Na `ng serve` (port 4200) ścieżki *.html
   * są przejmowane przez middleware SPA zanim zadziała proxy — iframe na backend jest niezawodny.
   */
  swaggerUiUrl: 'https://sggw-student-map-515453688242.europe-west3.run.app/swagger-ui/index.html'
};
