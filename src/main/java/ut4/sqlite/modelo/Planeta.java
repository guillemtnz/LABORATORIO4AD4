package ut4.sqlite.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

// Entidad Planeta: mapeo estándar contra la tabla planetas.
@Entity
@Table(name = "planetas")
public class Planeta implements Serializable {

    // IDENTITY porque el id lo genera SQLite (AUTOINCREMENT)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planeta_id")
    private int id;

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    @Column(name = "clima")
    private String clima;

    @Column(name = "afiliacion")
    private String afiliacion;

    // Constructor vacío que necesita Hibernate
    public Planeta() {
    }

    public Planeta(String nombre, String clima, String afiliacion) {
        this.nombre = nombre;
        this.clima = clima;
        this.afiliacion = afiliacion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getClima() { return clima; }
    public void setClima(String clima) { this.clima = clima; }

    public String getAfiliacion() { return afiliacion; }
    public void setAfiliacion(String afiliacion) { this.afiliacion = afiliacion; }

    @Override
    public String toString() {
        return "[Planeta] ID: " + id + " | " + nombre + " (" + clima + ") - Afiliación: " + afiliacion;
    }
}
