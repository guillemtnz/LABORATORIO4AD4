package ut4.sqlite.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

// Crea una única SessionFactory para todo el programa, a partir de hibernate.cfg.xml.
public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
            System.out.println("[HibernateUtil] SessionFactory inicializada sobre SQLite.");
        } catch (Throwable ex) {
            System.err.println("Error al inicializar la SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    // La llamo al final de cada Main para liberar la conexión con holonet.db
    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            System.out.println("[HibernateUtil] SessionFactory cerrada.");
        }
    }
}
