import { Component, inject } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-api-docs',
  standalone: true,
  template: `<iframe [src]="swaggerUrl" title="Swagger UI — dokumentacja REST API"></iframe>`,
  styles: `
    :host {
      display: block;
      height: 100vh;
    }
    iframe {
      border: 0;
      width: 100%;
      height: 100%;
    }
  `
})
export class ApiDocsComponent {
  private readonly sanitizer = inject(DomSanitizer);

  readonly swaggerUrl: SafeResourceUrl = this.sanitizer.bypassSecurityTrustResourceUrl(
    environment.swaggerUiUrl
  );
}
