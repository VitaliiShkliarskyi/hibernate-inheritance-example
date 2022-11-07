package core.basesyntax.dao.machine;

import java.time.LocalDate;
import java.util.List;
import core.basesyntax.dao.AbstractDao;
import core.basesyntax.model.machine.Machine;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class MachineDaoImpl extends AbstractDao implements MachineDao {
    public MachineDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Machine save(Machine machine) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.save(machine);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Can't save machine to DB " + machine, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return machine;
    }

    @Override
    public List<Machine> findByAgeOlderThan(int age) {
        try (Session session = sessionFactory.openSession()) {
            Query<Machine> query = session.createQuery("FROM Machine "
                    + "WHERE year < :year", Machine.class);
            query.setParameter("year", LocalDate.now().getYear() - age);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Can't get machine older than " + age, e);
        }
    }
}
