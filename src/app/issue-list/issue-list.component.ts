import { Component, OnInit } from '@angular/core';
import Swal from 'sweetalert2';
import { Issue, IssueService } from '../../services/issue.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import * as bootstrap from 'bootstrap';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-issue-list',
  templateUrl: './issue-list.component.html',
  styleUrls: ['./issue-list.component.scss'],
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
})
export class IssueListComponent implements OnInit {
  issues: Issue[] = []; 
  filteredIssues: Issue[] = []; // Lista filtrada por el buscador
  showActive: boolean = true; 
  dni: string = '';// Controla si estamos mostrando activos o inactivos
  dniToUpdate: string = '';
  selectedIssueId: number = 0;
  dniInfo: any = null;
  errorMessage: string = '';
  selectedIssue: Issue | null = null;
  constructor(private issueService: IssueService) {}


  ngOnInit(): void {
    this.loadIssues();
  }

  loadIssues(): void {
    this.issueService.getAllDniRecords().subscribe({
      next: (data) => {
        this.issues = data;
        this.filteredIssues = data; 
      },
      error: () => Swal.fire('Error', 'No se pudo cargar la lista de dni.', 'error'),
    });
  }

  // Alternar entre activos e inactivos
  toggleActiveInactive(): void {
    this.showActive = !this.showActive;
    if (this.showActive) {
      this.filteredIssues = this.issues.filter(issue => issue.status === 'A');
    } else {
      this.filteredIssues = this.issues.filter(issue => issue.status === 'I');
    }
  }

  restoreDni(id: number): void {
    Swal.fire({
      title: '¿Estás seguro?',
      text: 'Este dni será restaurado a estado activo.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, restaurar',
      cancelButtonText: 'Cancelar',
    }).then((result) => {
      if (result.isConfirmed) {
        this.issueService.restoreDni(id).subscribe({
          next: () => {
            this.loadIssues(); // Recarga la lista de registros desde el servidor
            Swal.fire('Restaurado', 'El dni ha sido restaurado a estado activo.', 'success');
          },
          error: () => Swal.fire('Error', 'No se pudo restaurar el dni.', 'error'),
        });
      }
    });
  }
  

  deleteLogical(id: number): void {
    Swal.fire({
      title: '¿Estás seguro?',
      text: 'No podrás deshacer esta acción.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
    }).then((result) => {
      if (result.isConfirmed) {
        this.issueService.deleteLogical(id).subscribe({
          next: () => {
            this.loadIssues(); // Recarga la lista de registros desde el servidor
            Swal.fire('Eliminado', 'El dni fue eliminado con éxito.', 'success');
          },
          error: () => Swal.fire('Error', 'No se pudo eliminar el dni.', 'error'),
        });
      }
    });
  }
  

  openDniModal(): void {
    Swal.fire({
      title: 'Consultar DNI',
      input: 'text',
      inputLabel: 'Ingrese el DNI',
      inputPlaceholder: 'DNI',
      showCancelButton: true,
      confirmButtonText: 'Consultar',
      cancelButtonText: 'Cancelar',
      preConfirm: (dni) => {
        // Validación para asegurar que el DNI es válido
        if (!dni) {
          Swal.showValidationMessage('Por favor ingrese un DNI válido');
          return false;  // Retorna false si el DNI no es válido
        }
  
        // Llamada al servicio para consultar el DNI
        this.issueService.consultDni(dni).subscribe({
          next: (result) => {
            this.dniInfo = result;  // Almacena la información del DNI
  
            // Comprobamos si el modal existe antes de mostrarlo
            const modalElement = document.getElementById('dniModal');
            if (modalElement) {
              const modal = new bootstrap.Modal(modalElement);
              modal.show(); // Muestra el modal con los datos actualizados
            } else {
              console.error('No se encontró el modal con el ID "dniModal".');
            }
          },
          error: (err) => {
            Swal.showValidationMessage('Hubo un error al consultar el DNI');
            console.error('Error al consultar DNI:', err);
          }
        });
  
        return true; // Continuamos con la ejecución normal
      },
    });
  }
  
  
  
  
  
  
openEditDniModal(id: number, currentDni: string): void {
  this.selectedIssueId = id;
  this.dniToUpdate = currentDni; // Set the current DNI to be updated in the modal
  // Open the modal
  const modalElement = document.getElementById('editDniModal');
  if (modalElement) {
    const modal = new bootstrap.Modal(modalElement);
    modal.show();
  }
}

updateDni(): void {
  if (this.dniToUpdate.trim() === '') {
    Swal.fire('Error', 'El DNI no puede estar vacío', 'error');
    return;
  }

  this.issueService.updateDni(this.selectedIssueId, this.dniToUpdate).subscribe(
    (response) => {
      Swal.fire('Éxito', 'DNI actualizado correctamente', 'success');

      // Usar un pequeño retraso para asegurarnos de que el modal se cierra después de que se muestra el mensaje de éxito
      setTimeout(() => {
        this.closeModal('editDniModal'); // Llamar la función para cerrar el modal
      }, 500); // 500ms de retraso para dar tiempo al mensaje

      // Recargar los registros de los issues
      this.loadIssues();
    },
    (error) => {
      Swal.fire('Error', 'Hubo un error al actualizar el DNI', 'error');
    }
  );
}

// Función para cerrar el modal de forma explícita
closeModal(modalId: string): void {
  const modalElement = document.getElementById(modalId);
  if (modalElement) {
    const modal = bootstrap.Modal.getInstance(modalElement);  // Obtener instancia activa del modal
    if (modal) {
      modal.hide();  // Usar el método hide() para cerrar el modal
    }
  } else {
    console.error(`No se encontró el modal con el id ${modalId}`);
  }
}




}
