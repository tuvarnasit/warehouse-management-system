package bg.tuvarna.sit.wms.dao;

import bg.tuvarna.sit.wms.entities.RentalAgreement;
import bg.tuvarna.sit.wms.entities.Tenant;
import bg.tuvarna.sit.wms.exceptions.RentalAgreementDAOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

/**
 * A DAO class for the RentalAgreement entity.
 * Provides methods for performing CRUD operations to the database.
 */
public class RentalAgreementDAO {

  private EntityManagerFactory entityManagerFactory;

  public RentalAgreementDAO(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  /**
   * Persists a new RentalRequest entity to the database within a transaction.
   * If an error occurs during the transaction it is rolled back.
   *
   * @param rentalAgreement the RentalAgreement entity to be saved
   * @throws RentalAgreementDAOException if an error occurs while persisting the entity
   */
  public void save(RentalAgreement rentalAgreement) throws RentalAgreementDAOException {

    EntityManager em = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = em.getTransaction();

    try {
      transaction.begin();
      Tenant tenant = rentalAgreement.getTenant();
      if (tenant.getId() == null) {
        em.persist(tenant);
      }
      em.persist(rentalAgreement);
      transaction.commit();

    } catch (Exception e) {
      throw handleExceptionAndRollback(transaction, "Error saving RentalAgreement entity", e);
    } finally {
      em.close();
    }
  }

  /**
   * Handles exceptions and rolls back the transaction.
   * If an exception occurs during the transaction, the transaction is rolled back.
   *
   * @param transaction the EntityTransaction instance
   * @param message     the error message
   * @param e           the exception to propagate
   * @return a new RentalAgreementDAOException with the given message and cause
   */
  private RentalAgreementDAOException handleExceptionAndRollback(EntityTransaction transaction, String message, Exception e) {

    if (transaction.isActive()) {
      transaction.rollback();
    }
    return new RentalAgreementDAOException(message, e);
  }
}
