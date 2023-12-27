package bg.tuvarna.sit.wms.dao;

import bg.tuvarna.sit.wms.entities.User;
import bg.tuvarna.sit.wms.exceptions.UserPersistenceException;
import javax.persistence.EntityManagerFactory;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

class UserDaoTest {

  @Mock
  private EntityManagerFactory entityManagerFactory;

  @Mock
  private EntityManager entityManager;

  @Mock
  private EntityTransaction transaction;

  private UserDao userDao;

  @BeforeEach
  void setUp() {

    MockitoAnnotations.openMocks(this);
    when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
    when(entityManager.getTransaction()).thenReturn(transaction);
    when(entityManager.isOpen()).thenReturn(true);
    when(transaction.isActive()).thenReturn(true);
    userDao = new UserDao(entityManagerFactory);
  }

  @Test
  void saveUser_ShouldPersistUser() throws UserPersistenceException {

    User user = new User();

    userDao.saveUser(user);

    verify(entityManager).getTransaction();
    verify(transaction).begin();
    verify(entityManager).persist(user);
    verify(transaction).commit();
    verify(entityManager).close();
  }

  @Test
  void saveUser_WhenPersistenceException_ShouldRollback() {
    User user = new User();

    doThrow(PersistenceException.class).when(entityManager).persist(user);

    assertThrows(UserPersistenceException.class, () -> userDao.saveUser(user));

    verify(entityManager).getTransaction();
    verify(transaction).begin();
    verify(entityManager).persist(user);
    verify(transaction).rollback();
    verify(entityManager).close();
  }

  @Test
  void saveUser_WhenGenericException_ShouldRollbackAndThrow() {
    User user = new User();
    doThrow(RuntimeException.class).when(entityManager).persist(user);

    assertThrows(UserPersistenceException.class, () -> userDao.saveUser(user));

    verify(transaction).begin();
    verify(entityManager).persist(user);
    verify(transaction).rollback();
    verify(entityManager).close();
  }

}
