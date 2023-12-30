package bg.tuvarna.sit.wms.service;

import bg.tuvarna.sit.wms.dao.UserDao;
import bg.tuvarna.sit.wms.dto.ViewReviewDto;
import bg.tuvarna.sit.wms.entities.Agent;
import bg.tuvarna.sit.wms.entities.Review;
import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.exceptions.ReviewPersistenceException;
import bg.tuvarna.sit.wms.util.JpaUtil;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReviewService {

  private static final Logger LOGGER = LogManager.getLogger(ReviewService.class);

  private final UserDao userDao;

  public ReviewService(UserDao userDao) {
    this.userDao = userDao;
  }

  /**
   * Creates a new review and persists it to the database along with updating the associated agent.
   *
   * @param agentId     The ID of the agent who will receive the review.
   * @param sender      The user who sends the review.
   * @param assessment  The assessment score of the review.
   * @param description The descriptive text of the review.
   * @throws ReviewPersistenceException If there is an issue during the persistence operation.
   */
  public void createAndPersistReview(Long agentId, User sender, Integer assessment, String description) throws ReviewPersistenceException {

    EntityManager entityManager = JpaUtil.getEntityManagerFactory().createEntityManager();

    try {
      Agent agent = findAgent(entityManager, agentId);
      Review review = createReview(agent, sender, assessment, description);
      persistReview(entityManager, review, agent);
    } finally {
      entityManager.close();
    }
  }

  /**
   * Initializes the reviews by clearing any existing reviews for a specific agent
   * and creating a new review. The initialization process finds the agent and owner
   * by their emails, deletes any reviews associated with the agent, and then creates
   * a new review from the owner to the agent.
   */
  public void initializeReviews() {

    Agent agent = (Agent) userDao.findByEmail("agent1@example.com").orElseThrow(
            () -> new IllegalStateException("Agent not found"));
    User owner = userDao.findByEmail("owner1@example.com").orElseThrow(
            () -> new IllegalStateException("Owner not found"));

    try {
      deleteAllReviewsForAgent(agent);
      createAndPersistReview(agent.getId(), owner, 5, "Excellent service.");
      //createAndPersistReview(agent.getId(), owner, 3, "Poor service.");
      LOGGER.info("Successfully initialized reviews for agent {}", agent.getEmail());
    } catch (ReviewPersistenceException e) {
      LOGGER.error("Exception occurred while initializing reviews: ", e);
    }
  }

  public List<ViewReviewDto> getReviewsForCurrentUser(Long currentUserId) {

    List<ViewReviewDto> reviewDtos = new ArrayList<>();
    EntityManager entityManager = JpaUtil.getEntityManagerFactory().createEntityManager();

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

  private Agent findAgent(EntityManager entityManager, Long agentId) throws EntityNotFoundException {

    Agent agent = entityManager.find(Agent.class, agentId);
    if (agent == null) {
      LOGGER.error("Agent not found with ID: {}", agentId);
      throw new EntityNotFoundException("Agent with ID " + agentId + " not found.");
    }
    return agent;
  }

  private Review createReview(Agent agent, User sender, Integer assessment, String description) {

    Review review = new Review();
    review.setAssessment(assessment);
    review.setDescription(description);
    review.setSender(sender);
    review.setReceiver(agent);

    return review;
  }

  private void persistReview(EntityManager entityManager, Review review, Agent agent) throws ReviewPersistenceException {

    EntityTransaction transaction = entityManager.getTransaction();
    try {
      transaction.begin();
      entityManager.persist(review);
      agent.getReceivedReviews().add(review); // Assuming this set is already initialized
      entityManager.merge(agent);
      transaction.commit();
    } catch (PersistenceException e) {
      LOGGER.error("Persistence exception occurred while persisting review: {}", e.getMessage());
      handleTransaction(transaction);
      throw new ReviewPersistenceException("Could not persist review.", e);
    } catch (Exception e) {
      LOGGER.error("Unexpected exception occurred while persisting review: {}", e.getMessage());
      handleTransaction(transaction);
      throw new ReviewPersistenceException("An unexpected error occurred while creating a review.", e);
    }
  }

  private void handleTransaction(EntityTransaction transaction) {

    if (transaction != null && transaction.isActive()) {
      transaction.rollback();
    }
  }

  /**
   * Deletes all reviews associated with a given agent.
   *
   * @param agent The agent whose reviews are to be deleted.
   */
  private void deleteAllReviewsForAgent(Agent agent) {

    EntityManager entityManager = JpaUtil.getEntityManagerFactory().createEntityManager();
    EntityTransaction transaction = entityManager.getTransaction();

    try {
      transaction.begin();

      entityManager.createQuery("DELETE FROM Review").executeUpdate();
      LOGGER.info("Deleted reviews for agent with ID: {}", agent.getId());

      transaction.commit();
    } catch (PersistenceException e) {
      if (transaction.isActive()) {
        transaction.rollback();
      }
      LOGGER.error("Persistence exception occurred during review deletion: ", e);
    } finally {
      entityManager.close();
    }
  }
}
