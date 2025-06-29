import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'list',
    loadComponent: () => import('./issue-list/issue-list.component').then(m => m.IssueListComponent),
  },
  {
    path: '',
    redirectTo: '/list', // Redirige a la ruta '/list' si no se especifica ruta
    pathMatch: 'full',  // Asegúrate de usar 'full' para una coincidencia exacta
  },
];
