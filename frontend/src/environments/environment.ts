export const environment = {
  baseUrl: 'https://sggw-student-map-515453688242.europe-west3.run.app',
  apiBaseUrl: 'https://sggw-student-map-515453688242.europe-west3.run.app/api',
  authBaseUrl: 'https://sggw-student-map-515453688242.europe-west3.run.app/auth',
  /**
   * Swagger UI jest serwowany przez Spring Boot. Na `ng serve` (port 4200) ścieżki *.html, chmurowa wersja z frontu
   * są przejmowane przez middleware SPA zanim zadziała proxy — iframe na backend jest niezawodny.
   */
  swaggerUiUrl: 'https://sggw-student-map-515453688242.europe-west3.run.app/swagger-ui/index.html'
};
