package ut4.sqlite.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;

// Clase padre abstracta de la jerarquía. Con JOINED guardo los datos comunes en la tabla naves
// y cada subclase tiene su propia tabla unida por la PK. A diferencia de SINGLE_TABLE,
// no hay columna discriminadora ni columnas a NULL.
@Entity
@Table(name = "naves")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Nave implements Serializable {

    // Este id se reutiliza como PK en la tabla hija
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nave_id")
    private int id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "modelo", nullable = false)
    private String modelo;

    @Column(name = "estado_sistema", nullable = false)
    private String estadoSistema;

    // Muchas naves pueden tener el mismo planeta. Con LAZY el planeta no se carga hasta que se usa,
    // por eso en las consultas que lo necesito uso JOIN FETCH.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planeta_origen_id")
    private Planeta planetaOrigen;

    // Constructor vacío que necesita Hibernate
    public Nave() {
    }

    public Nave(String nombre, String modelo, String estadoSistema, Planeta planetaOrigen) {
        this.nombre = nombre;
        this.modelo = modelo;
        this.estadoSistema = estadoSistema;
        this.planetaOrigen = planetaOrigen;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getEstadoSistema() { return estadoSistema; }
    public void setEstadoSistema(String estadoSistema) { this.estadoSistema = estadoSistema; }

    public Planeta getPlanetaOrigen() { return planetaOrigen; }
    public void setPlanetaOrigen(Planeta planetaOrigen) { this.planetaOrigen = planetaOrigen; }

    // No incluyo el planeta: al ser LAZY daría error fuera de la sesión
    @Override
    public String toString() {
        return "[" + getClass().getSimpleName() + "] ID: " + id + " | " + nombre
             + " (" + modelo + ") - Estado: " + estadoSistema;
    }
}
