package pe.edu.vallegrande.api_reniec.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dni")
public class Dni {
    @Id
    private Long id;

    @Column("dni")
    private String dni;

    @Column("nombres")
    private String nombres;

    @Column("apellidoPaterno")
    private String apellidoPaterno;

    @Column("apellidoMaterno")
    private String apellidoMaterno;

    @Column("codVerifica")
    private String codVerifica;

    @Column("status")
    private String status;
}
