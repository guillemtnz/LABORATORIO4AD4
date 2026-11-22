package ut4.sqlite.pruebas;

import java.util.List;
import ut4.sqlite.controlador.HoloNetControl;
import ut4.sqlite.modelo.CazaLigero;
import ut4.sqlite.modelo.Nave;
import ut4.sqlite.modelo.Planeta;
import ut4.sqlite.util.HibernateUtil;

// Pruebas de las operaciones 1 a 4 (después de init_holonet_sqlite.sql).
// Creo un caza, lo modifico fuera de sesión, lo consulto y lo borro, así la BD queda igual.
public class MainHoloNet {

    public static void main(String[] args) {

        HoloNetControl control = new HoloNetControl();

        try {
            System.out.println("\n=== Flota inicial en holonet.db ===");
            mostrarFlota(control.listarFlota());

            // Operación 1: alta de un caza nuevo en Yavin IV
            System.out.println("\n1. Alta polimórfica de un nuevo caza:");
            Planeta yavin = control.buscarPlaneta(2);
            CazaLigero nuevoCaza = new CazaLigero("Caza A-Wing", "RZ-1", "Estable", yavin, 90);
            control.registrarNuevaNave(nuevoCaza);

            // Operación 2: la sesión ya se cerró, así que el caza está detached.
            // Lo modifico y lo sincronizo con merge.
            System.out.println("\n2. Modificación fuera de sesión y sincronización con merge:");
            nuevoCaza.setEstadoSistema("Mantenimiento");
            nuevoCaza.setPotenciaFuego(95);
            control.actualizarEstadoDesconectado(nuevoCaza);
            System.out.println("Comprobación en BD: " + control.buscarNave(nuevoCaza.getId()));

            // Operación 3: cazas con potencia >= 80
            int potenciaMinima = 80;
            System.out.println("\n3. Cazas con potencia de fuego >= " + potenciaMinima + ":");
            List<CazaLigero> cazas = control.obtenerCazasPorPotenciaMinima(potenciaMinima);
            if (!cazas.isEmpty()) {
                for (CazaLigero caza : cazas) {
                    // Puedo leer el planeta sin sesión gracias al JOIN FETCH
                    System.out.println(caza + " | Planeta: " + caza.getPlanetaOrigen().getNombre()
                                     + " (" + caza.getPlanetaOrigen().getAfiliacion() + ")");
                }
            } else {
                System.out.println("No hay cazas con esa potencia mínima.");
            }

            // Operación 4: borro el caza creado y pruebo con un id que no existe
            System.out.println("\n4. Baja de la nave con ID " + nuevoCaza.getId() + ":");
            boolean borrada = control.darDeBajaNave(nuevoCaza.getId());
            System.out.println("¿Eliminada? " + borrada);

            System.out.println("\n4b. Baja de una nave inexistente (ID 999):");
            System.out.println("¿Eliminada? " + control.darDeBajaNave(999));

            System.out.println("\n=== Flota final en holonet.db ===");
            mostrarFlota(control.listarFlota());

        } finally {
            HibernateUtil.shutdown();
            System.out.println("======================================================");
        }
    }

    private static void mostrarFlota(List<Nave> flota) {
        for (Nave nave : flota) {
            String planeta = nave.getPlanetaOrigen() != null ? nave.getPlanetaOrigen().getNombre() : "Sin planeta";
            System.out.println(nave + " | Planeta: " + planeta);
        }
    }
}
