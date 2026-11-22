package ut4.sqlite.controlador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ut4.sqlite.modelo.CazaLigero;
import ut4.sqlite.modelo.DestructorEstelar;
import ut4.sqlite.modelo.Nave;
import ut4.sqlite.modelo.Planeta;
import ut4.sqlite.util.HibernateUtil;

// Controlador con todas las operaciones sobre SQLite.
// En las que modifican datos pongo el rollback dentro del bloque de la sesión: si el catch
// estuviera fuera del try-with-resources, la sesión ya estaría cerrada y el rollback fallaría.
public class HoloNetControl {

    // Operación 1: recibo una Nave y Hibernate inserta en naves y en su tabla hija
    public void registrarNuevaNave(Nave nave) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.persist(nave);

                tx.commit();
                System.out.println("Nave registrada con ID " + nave.getId() + ": " + nave);
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                System.err.println("Error al registrar la nave: " + e.getMessage());
            }
        }
    }

    // Operación 2: la nave llega detached (modificada fuera de sesión).
    // merge copia sus cambios en una instancia gestionada y en el commit se hace el UPDATE.
    public void actualizarEstadoDesconectado(Nave naveModificada) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Nave naveGestionada = session.merge(naveModificada);

                tx.commit();
                System.out.println("Nave sincronizada mediante merge: " + naveGestionada);
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                System.err.println("Error al sincronizar la nave desconectada: " + e.getMessage());
            }
        }
    }

    // Operación 3: HQL sobre la subclase CazaLigero. Con JOIN FETCH traigo el planeta en la misma
    // consulta, así puedo usarlo con la sesión cerrada sin LazyInitializationException.
    public List<CazaLigero> obtenerCazasPorPotenciaMinima(int potenciaMinima) {
        List<CazaLigero> cazas = new ArrayList<>();

        String hql = "SELECT c FROM CazaLigero c "
                   + "JOIN FETCH c.planetaOrigen "
                   + "WHERE c.potenciaFuego >= :potenciaRef "
                   + "ORDER BY c.potenciaFuego DESC";

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<CazaLigero> query = session.createQuery(hql, CazaLigero.class);
            query.setParameter("potenciaRef", potenciaMinima);

            cazas = query.getResultList();

        } catch (Exception e) {
            System.err.println("Error en la consulta de cazas por potencia: " + e.getMessage());
        }
        return cazas;
    }

    // Operación 4: busco la nave con find (devuelve el tipo real) y la borro con remove.
    // Hibernate borra primero la fila de la tabla hija y luego la de naves.
    public boolean darDeBajaNave(int idNave) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Nave nave = session.find(Nave.class, idNave);
                if (nave == null) {
                    tx.rollback();
                    System.out.println("No existe ninguna nave con ID " + idNave + ".");
                    return false;
                }

                session.remove(nave);

                tx.commit();
                System.out.println("Nave dada de baja: " + nave);
                return true;
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                System.err.println("Error al dar de baja la nave: " + e.getMessage());
                return false;
            }
        }
    }

    // Operación 5: carga del CSV en una sola transacción. Si falla una línea hago rollback
    // y no se guarda nada. Devuelvo las naves importadas (0 si hay rollback).
    public int importarFlotaDesdeCSV(String rutaRecurso) {
        // Leo el fichero desde resources (classpath)
        InputStream is = HoloNetControl.class.getClassLoader().getResourceAsStream(rutaRecurso);
        if (is == null) {
            System.err.println("No se encontró el fichero de despliegue: " + rutaRecurso);
            return 0;
        }

        int importadas = 0;
        int numLinea = 1;
        try (Session session = HibernateUtil.getSessionFactory().openSession();
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            Transaction tx = session.beginTransaction();
            try {
                String linea = br.readLine(); // Me salto la cabecera

                while ((linea = br.readLine()) != null) {
                    numLinea++;
                    if (linea.isBlank()) continue;

                    String[] datos = linea.split(";");
                    if (datos.length != 6) {
                        throw new IllegalArgumentException("línea " + numLinea + " con " + datos.length + " campos (se esperaban 6)");
                    }
                    String tipo = datos[0].trim();
                    String nombre = datos[1].trim();
                    String modelo = datos[2].trim();
                    String estado = datos[3].trim();
                    int idPlaneta = Integer.parseInt(datos[4].trim());
                    int valorEspecifico = Integer.parseInt(datos[5].trim());

                    // getReference no hace SELECT: solo necesito el id para la FK
                    Planeta planeta = session.getReference(Planeta.class, idPlaneta);

                    // Creo un caza o un destructor según el TIPO
                    Nave nave;
                    switch (tipo.toUpperCase()) {
                        case "CAZA" -> nave = new CazaLigero(nombre, modelo, estado, planeta, valorEspecifico);
                        case "DESTRUCTOR" -> nave = new DestructorEstelar(nombre, modelo, estado, planeta, valorEspecifico);
                        default -> throw new IllegalArgumentException("tipo de nave desconocido '" + tipo + "' en la línea " + numLinea);
                    }

                    session.persist(nave);
                    importadas++;
                    System.out.println("  + " + nave);
                }

                tx.commit();
                System.out.println("[HoloNet] Despliegue desde CSV completado en SQLite: " + importadas + " naves.");
                return importadas;

            } catch (Exception e) {
                // Deshago también las naves de las líneas anteriores
                if (tx.isActive()) tx.rollback();
                System.err.println("Fallo crítico en la carga masiva (rollback, no se guarda nada): " + e.getMessage());
                return 0;
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar el fichero CSV: " + e.getMessage());
            return 0;
        }
    }

    // ---------------- Métodos auxiliares para las pruebas ----------------

    public Planeta buscarPlaneta(int idPlaneta) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.find(Planeta.class, idPlaneta);
        }
    }

    public Nave buscarNave(int idNave) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.find(Nave.class, idNave);
        }
    }

    // Consulta sobre Nave: devuelve cazas y destructores. LEFT JOIN para incluir naves sin planeta.
    public List<Nave> listarFlota() {
        List<Nave> flota = new ArrayList<>();

        String hql = "SELECT n FROM Nave n "
                   + "LEFT JOIN FETCH n.planetaOrigen "
                   + "ORDER BY n.id";

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            flota = session.createQuery(hql, Nave.class).getResultList();
        } catch (Exception e) {
            System.err.println("Error al listar la flota: " + e.getMessage());
        }
        return flota;
    }
}
