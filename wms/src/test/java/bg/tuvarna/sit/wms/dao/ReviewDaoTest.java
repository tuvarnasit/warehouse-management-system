package bg.tuvarna.sit.wms.dao;

import bg.tuvarna.sit.wms.dao.ReviewDao;

import bg.tuvarna.sit.wms.dto.ViewReviewDto;
import bg.tuvarna.sit.wms.entities.Agent;
import bg.tuvarna.sit.wms.entities.Review;
import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.exceptions.ReviewPersistenceException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

public class ReviewDaoTest {

  @Mock
  private EntityManagerFactory entityManagerFactory;

  @Mock
  private EntityManager entityManager;

  @Mock
  private EntityTransaction transaction;

  @Mock
  private TypedQuery<Review> typedQuery;

  @InjectMocks
  private ReviewDao reviewDao;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
    when(entityManager.getTransaction()).thenReturn(transaction);
    when(entityManager.createQuery(anyString(), eq(Review.class))).thenReturn(typedQuery);
  }

  @Test
  public void deleteAllReviewsForAgent_ShouldDeleteReviews() throws ReviewPersistenceException {
    // Given
    Agent agent = mock(Agent.class);
    when(agent.getId()).thenReturn(1L);

    Query queryMock = mock(Query.class);
    when(entityManager.createQuery(anyString())).thenReturn(queryMock);
    when(queryMock.setParameter(anyString(), any())).thenReturn(queryMock);

    // When
    reviewDao.deleteAllReviewsForAgent(agent);

    // Then
    verify(entityManager).createQuery("DELETE FROM Review r WHERE r.receiver.id = :agentId");
    verify(queryMock).setParameter("agentId", agent.getId());
    verify(transaction).begin();
    verify(queryMock).executeUpdate();
    verify(transaction).commit();
    verify(entityManager).close();
  }

  @Test
  public void deleteAllReviewsForAgent_ShouldThrowExceptionIfPersistenceFails() {
    // Given
    Agent agent = mock(Agent.class);
    when(agent.getId()).thenReturn(1L);

    PersistenceException simulatedException = new PersistenceException("Error");
    when(entityManager.createQuery(anyString())).thenThrow(simulatedException);
    when(transaction.isActive()).thenReturn(true);

    // When
    Assertions.assertThrows(ReviewPersistenceException.class, () -> {
      reviewDao.deleteAllReviewsForAgent(agent);
    });

    // Then
    verify(transaction, never()).commit();
    verify(transaction).rollback();
    verify(entityManager).close();
  }

  @Test
  public void persistReview_ShouldPersistReview() throws ReviewPersistenceException {
    // Given
    Review review = new Review();
    Agent agent = new Agent();
    Long agentId = 1L;
    agent.setId(agentId);
    agent.setReceivedReviews(new HashSet<>());
    when(entityManager.find(Agent.class, agentId)).thenReturn(agent);

    // When
    reviewDao.persistReview(review, agentId);

    // Then
    verify(transaction).begin();
    verify(entityManager).persist(review);
    verify(entityManager).merge(agent);
    verify(transaction).commit();
    verify(entityManager).close();
  }

  @Test
  public void persistReview_ShouldThrowExceptionIfPersistenceFails() {
    // Given
    Review review = new Review();
    Long agentId = 1L;
    PersistenceException simulatedException = new PersistenceException("Error");
    when(entityManager.find(Agent.class, agentId)).thenThrow(simulatedException);
    when(transaction.isActive()).thenReturn(true);

    // When
    Assertions.assertThrows(ReviewPersistenceException.class, () -> {
      reviewDao.persistReview(review, agentId);
    });

    // Then
    verify(transaction).rollback();
    verify(entityManager).close();
  }

  @Test
  public void getReviewsForCurrentUser_ShouldReturnReviews() {
    // Given
    Long currentUserId = 1L;
    Agent agent = mock(Agent.class);
    List<Review> reviews = Arrays.asList(
            createReview(agent, new User(), 5, "test"),
            createReview(agent, new User(), 1, "test"));
    when(entityManager.createQuery(anyString(), eq(Review.class))).thenReturn(typedQuery);
    when(typedQuery.getResultList()).thenReturn(reviews);

    // When
    List<ViewReviewDto> result = reviewDao.getReviewsForCurrentUser(currentUserId);

    // Then
    verify(entityManager).close();
    Assertions.assertEquals(reviews.size(), result.size());
  }

  @Test
  public void getReviewsForCurrentUser_ShouldReturnEmptyListWhenAgentNotFound() {
    // Given
    Long currentUserId = 1L;
    when(entityManager.createQuery(anyString(), eq(Review.class))).thenReturn(typedQuery);
    when(typedQuery.getResultList()).thenReturn(new ArrayList<>());

    // When
    List<ViewReviewDto> result = reviewDao.getReviewsForCurrentUser(currentUserId);

    // Then
    verify(entityManager).close();
    Assertions.assertTrue(result.isEmpty());
  }

  // Cleanup after tests
  @AfterEach
  public void tearDown() {
    // This is actually called automatically by the Mockito JUnit extension
    entityManager.close();
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
