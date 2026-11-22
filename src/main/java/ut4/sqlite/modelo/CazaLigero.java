package ut4.sqlite.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

// Subclase de Nave con su propia tabla. Al guardarla, Hibernate inserta en naves y luego en cazas_ligeros.
@Entity
@Table(name = "cazas_ligeros")
@PrimaryKeyJoinColumn(name = "caza_id") // PK de la tabla hija y FK hacia naves.nave_id
public class CazaLigero extends Nave {

    // int y no Integer: con JOINED esta columna nunca es NULL
    @Column(name = "potencia_fuego", nullable = false)
    private int potenciaFuego;

    public CazaLigero() {
    }

    public CazaLigero(String nombre, String modelo, String estadoSistema, Planeta planetaOrigen, int potenciaFuego) {
        super(nombre, modelo, estadoSistema, planetaOrigen);
        this.potenciaFuego = potenciaFuego;
    }

    public int getPotenciaFuego() { return potenciaFuego; }
    public void setPotenciaFuego(int potenciaFuego) { this.potenciaFuego = potenciaFuego; }

    @Override
    public String toString() {
        return super.toString() + " - Potencia de fuego: " + potenciaFuego;
    }
}
