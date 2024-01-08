package bg.tuvarna.sit.wms.service;

import bg.tuvarna.sit.wms.dao.ReviewDao;
import bg.tuvarna.sit.wms.dao.UserDao;
import bg.tuvarna.sit.wms.dto.AddReviewDto;
import bg.tuvarna.sit.wms.dto.ViewReviewDto;
import bg.tuvarna.sit.wms.entities.Agent;
import bg.tuvarna.sit.wms.entities.Review;
import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.exceptions.ReviewPersistenceException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

public class ReviewServiceTest {

  @Mock
  private UserDao userDao;

  @Mock
  private ReviewDao reviewDao;

  @InjectMocks
  private ReviewService reviewService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createAndPersistReview_ShouldPersistReviewWhenAgentExists() throws ReviewPersistenceException {
    // Given
    Long agentId = 1L;
    User sender = new User();
    Agent agent = new Agent();
    Integer assessment = 5;
    String description = "Great service";

    when(userDao.findAgentById(agentId)).thenReturn(agent);

    // When
    reviewService.createAndPersistReview(agentId, sender, new AddReviewDto(assessment, description));

    // Then
    verify(userDao).findAgentById(agentId);
    verify(reviewDao).persistReview(any(Review.class), eq(agentId));
  }

  @Test
  void createAndPersistReview_ShouldHandleEntityNotFoundException() throws ReviewPersistenceException {
    // Given
    Long agentId = 1L;
    User sender = new User();
    Integer assessment = 5;
    String description = "Great service";

    when(userDao.findAgentById(agentId)).thenThrow(new EntityNotFoundException());

    // When
    reviewService.createAndPersistReview(agentId, sender, new AddReviewDto(assessment, description));

    // Then
    verify(userDao).findAgentById(agentId);
    verify(reviewDao, never()).persistReview(any(Review.class), anyLong());
  }

  @Test
  void createAndPersistReview_ShouldHandleReviewPersistenceException() throws ReviewPersistenceException {
    // Given
    Long agentId = 1L;
    User sender = new User();
    Agent agent = new Agent();
    Integer assessment = 5;
    String description = "Great service";

    when(userDao.findAgentById(agentId)).thenReturn(agent);
    doThrow(new ReviewPersistenceException("Error persisting review"))
            .when(reviewDao).persistReview(any(Review.class), eq(agentId));

    // When
    reviewService.createAndPersistReview(agentId, sender, new AddReviewDto(assessment, description));

    // Then
    verify(userDao).findAgentById(agentId);
    verify(reviewDao).persistReview(any(Review.class), eq(agentId));
  }

  @Test
  void initializeReviews_ShouldInitializeReviewsSuccessfully() throws ReviewPersistenceException {
    // Given
    Agent agent = mock(Agent.class);
    User owner = mock(User.class);

    when(userDao.findByEmail("agent1@example.com")).thenReturn(Optional.of(agent));
    when(userDao.findByEmail("owner1@example.com")).thenReturn(Optional.of(owner));
    when(agent.getId()).thenReturn(1L);

    // When
    reviewService.initializeReviews();

    // Then
    verify(userDao).findByEmail("agent1@example.com");
    verify(userDao).findByEmail("owner1@example.com");
    verify(reviewDao).deleteAllReviewsForAgent(agent);
    verify(reviewDao).persistReview(any(Review.class), eq(1L));
  }

  @Test
  void initializeReviews_ShouldHandleAgentNotFoundException() throws ReviewPersistenceException {
    // Given
    when(userDao.findByEmail("agent1@example.com")).thenReturn(Optional.empty());

    // When
    Exception exception = assertThrows(IllegalStateException.class, () -> reviewService.initializeReviews());

    // Then
    assertEquals("Agent not found", exception.getMessage());
    verify(reviewDao, never()).deleteAllReviewsForAgent(any(Agent.class));
    verify(reviewDao, never()).persistReview(any(Review.class), anyLong());
  }

  @Test
  void initializeReviews_ShouldHandleOwnerNotFoundException() throws ReviewPersistenceException {
    // Given
    Agent agent = mock(Agent.class);
    when(userDao.findByEmail("agent1@example.com")).thenReturn(Optional.of(agent));
    when(userDao.findByEmail("owner1@example.com")).thenReturn(Optional.empty());

    // When
    Exception exception = assertThrows(IllegalStateException.class, () -> reviewService.initializeReviews());

    // Then
    assertEquals("Owner not found", exception.getMessage());
    verify(reviewDao, never()).deleteAllReviewsForAgent(any(Agent.class));
    verify(reviewDao, never()).persistReview(any(Review.class), anyLong());
  }

  @Test
  void getReviewsForCurrentUser_ShouldReturnListOfReviews() {
    // Given
    Long currentUserId = 1L;
    Agent agent = mock(Agent.class);
    List<ViewReviewDto> expectedReviews = new ArrayList<>();
    expectedReviews.add(new ViewReviewDto(createReview(agent, new User(), 5, "test")));
    when(reviewDao.getReviewsForCurrentUser(currentUserId)).thenReturn(expectedReviews);

    // When
    List<ViewReviewDto> actualReviews = reviewService.getReviewsForCurrentUser(currentUserId);

    // Then
    assertEquals(expectedReviews, actualReviews);
    verify(reviewDao).getReviewsForCurrentUser(currentUserId);
  }

  @Test
  void getReviewsForCurrentUser_ShouldReturnEmptyListWhenNoReviews() {
    // Given
    Long currentUserId = 1L;
    when(reviewDao.getReviewsForCurrentUser(currentUserId)).thenReturn(Collections.emptyList());

    // When
    List<ViewReviewDto> actualReviews = reviewService.getReviewsForCurrentUser(currentUserId);

    // Then
    assertTrue(actualReviews.isEmpty());
    verify(reviewDao).getReviewsForCurrentUser(currentUserId);
  }

  private Review createReview(Agent agent, User sender, Integer assessment, String description) {

    Review review = new Review();
    review.setAssessment(assessment);
    review.setDescription(description);
    review.setSender(sender);
    review.setReceiver(agent);

    return review;
  }
}