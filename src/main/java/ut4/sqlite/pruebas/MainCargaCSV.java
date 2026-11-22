package ut4.sqlite.pruebas;

import java.util.List;
import ut4.sqlite.controlador.HoloNetControl;
import ut4.sqlite.modelo.Nave;
import ut4.sqlite.util.HibernateUtil;

// Prueba de la operación 5: importo el CSV y muestro la flota.
public class MainCargaCSV {

    public static void main(String[] args) {

        HoloNetControl control = new HoloNetControl();

        try {
            System.out.println("\n5. Importación masiva desde refuerzos_borde_exterior.csv:");
            int importadas = control.importarFlotaDesdeCSV("refuerzos_borde_exterior.csv");
            System.out.println("Naves importadas: " + importadas);

            System.out.println("\n=== Flota en holonet.db tras la carga ===");
            List<Nave> flota = control.listarFlota();
            for (Nave nave : flota) {
                String planeta = nave.getPlanetaOrigen() != null ? nave.getPlanetaOrigen().getNombre() : "Sin planeta";
                System.out.println(nave + " | Planeta: " + planeta);
            }

        } finally {
            HibernateUtil.shutdown();
            System.out.println("======================================================");
        }
    }
}
