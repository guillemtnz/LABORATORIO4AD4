package ut4.sqlite.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

// Subclase de Nave con su propia tabla. Al guardarla, Hibernate inserta en naves y luego en destructores_estelares.
@Entity
@Table(name = "destructores_estelares")
@PrimaryKeyJoinColumn(name = "destructor_id") // PK de la tabla hija y FK hacia naves.nave_id
public class DestructorEstelar extends Nave {

    @Column(name = "capacidad_escudos", nullable = false)
    private int capacidadEscudos;

    public DestructorEstelar() {
    }

    public DestructorEstelar(String nombre, String modelo, String estadoSistema, Planeta planetaOrigen, int capacidadEscudos) {
        super(nombre, modelo, estadoSistema, planetaOrigen);
        this.capacidadEscudos = capacidadEscudos;
    }

    public int getCapacidadEscudos() { return capacidadEscudos; }
    public void setCapacidadEscudos(int capacidadEscudos) { this.capacidadEscudos = capacidadEscudos; }

    @Override
    public String toString() {
        return super.toString() + " - Escudos: " + capacidadEscudos;
    }
}
