import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import java.util.List;

public class CloudMigration {

    public static void main(String[] args) {
        SessionFactory localFactory = new Configuration()
            .configure("hibernate.cfg.xml")
            .addAnnotatedClass(Student.class)
            .buildSessionFactory();

        SessionFactory cloudFactory = new Configuration()
            .configure("hibernate-cloud.cfg.xml")
            .addAnnotatedClass(Student.class)
            .buildSessionFactory();

        Session localSession = localFactory.openSession();
        Session cloudSession = cloudFactory.openSession();

        try {
            localSession.beginTransaction();
            List<Student> students = localSession.createQuery("from Student", Student.class).getResultList();
            localSession.getTransaction().commit();

            cloudSession.beginTransaction();
            for (Student student : students) {
                cloudSession.save(student);
            }
            cloudSession.getTransaction().commit();

            System.out.println("Data migrated successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            localSession.close();
            cloudSession.close();
            localFactory.close();
            cloudFactory.close();
        }
    }
}
