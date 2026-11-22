package ut4.sqlite.util;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ut4.sqlite.modelo.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class CargadorFlotaCSV {

    public static void importarDesdeRecursos(String nombreFichero) {
        InputStream is = CargadorFlotaCSV.class.getClassLoader().getResourceAsStream(
nombreFichero);
        if (is == null) {
            System.err.println("No se encontró el fichero de despliegue: " + nombreFichero);
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession();
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            Transaction tx = session.beginTransaction();
            try {
                String linea = br.readLine(); // Salto la cabecera

                while ((linea = br.readLine()) != null) {
                    String[] datos = linea.split(";");
                    String tipo = datos[0];
                    String nombre = datos[1];
                    String modelo = datos[2];
                    String estado = datos[3];
                    int idPlaneta = Integer.parseInt(datos[4]);
                    int valorEspecifico = Integer.parseInt(datos[5]);

                    //1: Recuperar la referencia al Planeta usando session.getReference() o session.find()
                    Planeta planeta = session.getReference(Planeta.class, idPlaneta);

                    //2: Instanciar CazaLigero o DestructorEstelar según el valor de 'tipo'
                    Nave nave;
                    if (tipo.equals("CAZA")) {
                        nave = new CazaLigero(nombre, modelo, estado, planeta, valorEspecifico);
                    } else if (tipo.equals("DESTRUCTOR")) {
                        nave = new DestructorEstelar(nombre, modelo, estado, planeta, valorEspecifico);
                    } else {
                        throw new IllegalArgumentException("Tipo de nave desconocido: " + tipo);
                    }

                    //3: Persistir la entidad polimórfica en SQLite mediante session.persist()
                    session.persist(nave);
                }

                tx.commit();
                System.out.println("[HoloNet] Despliegue desde CSV completado en SQLite.");

            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                System.err.println("Fallo crítico en la carga masiva: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar el fichero CSV: " + e.getMessage());
        }
    }
}
