import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { HttpClientModule } from '@angular/common/http';  // Importa HttpClientModule aquí
import { provideHttpClient } from '@angular/common/http';  // Proveedor para HttpClient
import { provideRouter } from '@angular/router';
import { routes } from './app/app.routes';

bootstrapApplication(AppComponent, {
  providers: [
    HttpClientModule,
    provideHttpClient(),
    provideRouter(routes)
  ]
})
.catch(err => console.error(err));
