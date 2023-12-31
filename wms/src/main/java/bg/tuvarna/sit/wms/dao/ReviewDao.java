package bg.tuvarna.sit.wms.dao;

import bg.tuvarna.sit.wms.dto.ViewReviewDto;
import bg.tuvarna.sit.wms.entities.Agent;
import bg.tuvarna.sit.wms.entities.Review;
import bg.tuvarna.sit.wms.exceptions.ReviewPersistenceException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

/**
 * Data Access Object (DAO) for review operations.
 * Provides an abstraction layer for database operations related to review entities.
 */
public class ReviewDao {

  private final EntityManagerFactory entityManagerFactory;

  public ReviewDao(EntityManagerFactory entityManagerFactory) {
    this.entityManagerFactory = entityManagerFactory;
  }

  /**
   * Deletes all reviews associated with a given agent.
   *
   * @param agent The agent whose reviews are to be deleted.
   * @throws ReviewPersistenceException if there is a problem during the deletion process.
   */
  public void deleteAllReviewsForAgent(Agent agent) throws ReviewPersistenceException {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = entityManager.getTransaction();

    try {
      transaction.begin();
      entityManager.createQuery("DELETE FROM Review r WHERE r.receiver.id = :agentId")
              .setParameter("agentId", agent.getId())
              .executeUpdate();
      transaction.commit();
    } catch (PersistenceException e) {
      if (transaction.isActive()) {
        transaction.rollback();
      }
      throw new ReviewPersistenceException("Could not delete reviews for agent with ID: " + agent.getId(), e);
    } finally {
      entityManager.close();
    }
  }

  /**
   * Persists a new review in the database and associates it with an agent.
   *
   * @param review  The review to persist.
   * @param agentId The ID of the agent to whom the review is associated.
   * @throws ReviewPersistenceException if there is a problem during the persistence process.
   */
  public void persistReview(Review review, Long agentId) throws ReviewPersistenceException {

    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = entityManager.getTransaction();

    try {
      transaction.begin();
      entityManager.persist(review);
      Agent agent = entityManager.find(Agent.class, agentId);
      agent.getReceivedReviews().add(review);
      entityManager.merge(agent);
      transaction.commit();
    } catch (PersistenceException e) {
      if (transaction.isActive()) {
        transaction.rollback();
      }
      throw new ReviewPersistenceException("Could not persist review.", e);
    } finally {
      entityManager.close();
    }
  }

  /**
   * Retrieves a list of reviews DTOs for the current user, assuming the user is an agent.
   *
   * @param currentUserId The ID of the current user.
   * @return A list of {@link ViewReviewDto} objects representing the reviews.
   */
  public List<ViewReviewDto> getReviewsForCurrentUser(Long currentUserId) {

    List<ViewReviewDto> reviewDtos = new ArrayList<>();
    EntityManager entityManager = entityManagerFactory.createEntityManager();

    try {
      Agent agent = entityManager.find(Agent.class, currentUserId);
      if (agent != null) {
        agent.getReceivedReviews()
                .forEach(review -> reviewDtos.add(new ViewReviewDto(review)));
      }
    } finally {
      entityManager.close();
    }

    return reviewDtos;
  }
}
