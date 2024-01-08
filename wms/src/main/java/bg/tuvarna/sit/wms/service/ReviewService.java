package bg.tuvarna.sit.wms.service;

import bg.tuvarna.sit.wms.dao.ReviewDao;
import bg.tuvarna.sit.wms.dao.UserDao;
import bg.tuvarna.sit.wms.dto.AddReviewDto;
import bg.tuvarna.sit.wms.dto.ViewReviewDto;
import bg.tuvarna.sit.wms.entities.Agent;
import bg.tuvarna.sit.wms.entities.Review;
import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.entities.Warehouse;
import bg.tuvarna.sit.wms.exceptions.CityCreationException;
import bg.tuvarna.sit.wms.exceptions.CountryCreationException;
import bg.tuvarna.sit.wms.exceptions.ReviewPersistenceException;
import bg.tuvarna.sit.wms.exceptions.WarehouseDAOException;
import bg.tuvarna.sit.wms.exceptions.WarehousePersistenceException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ReviewService {

  private static final Logger LOGGER = LogManager.getLogger(ReviewService.class);

  private final UserDao userDao;
  private final ReviewDao reviewDao;

  @Setter
  @Getter
  private Long agentId;

  public ReviewService(UserDao userDao, ReviewDao reviewDao) {
    this.userDao = userDao;
    this.reviewDao = reviewDao;
  }

  /**
   * Creates a new review and persists it to the database along with updating the associated agent.
   *
   * @param agentId      The ID of the agent who will receive the review.
   * @param sender       The user who sends the review.
   * @param addReviewDto The DTO used to create new review.
   */
  public void createAndPersistReview(Long agentId, User sender, AddReviewDto addReviewDto) {

    try {
      Agent agent = userDao.findAgentById(agentId);
      Review review = createReview(agent, sender, addReviewDto.getAssessment(),
              addReviewDto.getDescription());
      reviewDao.persistReview(review, agentId);
    } catch (EntityNotFoundException e) {
      LOGGER.error("Agent not found with ID: {}", agentId, e);
    } catch (ReviewPersistenceException e) {
      LOGGER.error("Error persisting review: {}", e.getMessage(), e);
    }
  }

  public void loadReviewsFromCSV(String csvFilePath) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(Paths.get(csvFilePath).toFile()))) {
      String line;
      boolean header = true;
      while ((line = br.readLine()) != null) {
        if (header) {
          header = false;
          continue; // Skip header line
        }
        String[] values = line.split(",");
        Agent agent = (Agent) userDao.findByEmail(values[0]).orElseThrow(
                () -> new IllegalStateException("Agent not found"));
        User owner = userDao.findByEmail(values[1]).orElseThrow(
                () -> new IllegalStateException("Owner not found"));
        deleteAllReviewsForAgent(agent);
        createAndPersistReview(agent.getId(), owner, new AddReviewDto(Integer.parseInt(values[2]),
                values[3]));
        LOGGER.info("Successfully initialized reviews for agent {}", agent.getEmail());
      }
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

    deleteAllReviewsForAgent(agent);
    createAndPersistReview(agent.getId(), owner, new AddReviewDto(5, "Excellent service."));
    LOGGER.info("Successfully initialized reviews for agent {}", agent.getEmail());
  }

  public List<ViewReviewDto> getReviewsForCurrentUser(Long currentUserId) {

    return reviewDao.getReviewsForCurrentUser(currentUserId);
  }

  private Review createReview(Agent agent, User sender, Integer assessment, String description) {

    Review review = new Review();
    review.setAssessment(assessment);
    review.setDescription(description);
    review.setSender(sender);
    review.setReceiver(agent);

    return review;
  }

  /**
   * Deletes all reviews associated with a given agent.
   *
   * @param agent The agent whose reviews are to be deleted.
   */
  private void deleteAllReviewsForAgent(Agent agent) {

    try {
      reviewDao.deleteAllReviewsForAgent(agent);
    } catch (ReviewPersistenceException e) {
      LOGGER.error("Exception occurred while deleting reviews for agent: {}", e.getMessage(), e);
    }
  }
}
