package bg.tuvarna.sit.wms.dao;

import bg.tuvarna.sit.wms.entities.Country;
import bg.tuvarna.sit.wms.exceptions.CountryDAOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CountryDAOTest {

  @Mock
  private EntityManagerFactory entityManagerFactory;

  @Mock
  private EntityManager entityManager;

  @Mock
  private EntityTransaction transaction;

  @Mock
  private TypedQuery<Country> query;

  @InjectMocks
  private CountryDAO countryDAO;

  @BeforeEach
  void setUp() {

    MockitoAnnotations.openMocks(this);
    when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
    when(entityManager.getTransaction()).thenReturn(transaction);
    when(entityManager.isOpen()).thenReturn(true);
    when(transaction.isActive()).thenReturn(true);
  }

  @Test
  void save_shouldSaveCountry() throws CountryDAOException {

    Country country = new Country();
    country.setName("Country1");

    countryDAO.save(country);

    verify(transaction).begin();
    verify(entityManager).persist(country);
    verify(transaction).commit();
    verify(entityManager).close();
  }

  @Test
  void save_shouldThrowCountryDAOException() {

    Country country = new Country();
    country.setName("Country1");
    doThrow(new RuntimeException("Exception during persistence")).when(entityManager).persist(country);

    assertThrows(CountryDAOException.class, () -> countryDAO.save(country));

    verify(transaction).begin();
    verify(transaction).rollback();
    verify(entityManager).close();
  }

  @Test
  void getByName_shouldReturnCountryOptional() throws CountryDAOException {

    String countryName = "Country1";
    Country expectedCountry = new Country();
    expectedCountry.setName(countryName);

    when(entityManager.createQuery(anyString(), eq(Country.class))).thenReturn(query);
    when(query.setParameter("name", countryName)).thenReturn(query);
    when(query.getSingleResult()).thenReturn(expectedCountry);

    Optional<Country> result = countryDAO.getByName(countryName);

    assertTrue(result.isPresent());
    assertEquals(expectedCountry, result.get());

    verify(transaction).begin();
    verify(query).setParameter("name", countryName);
    verify(query).getSingleResult();
    verify(transaction).commit();
    verify(entityManager).close();
  }
  @Test
  void getByName_noResultException_shouldReturnEmptyOptional() throws CountryDAOException {

    String nonExistentCountryName = "Non Existent Country";

    when(entityManager.createQuery(anyString(), eq(Country.class))).thenReturn(query);
    when(query.setParameter("name", nonExistentCountryName)).thenReturn(query);
    when(query.getSingleResult()).thenThrow(new NoResultException());

    Optional<Country> result = countryDAO.getByName(nonExistentCountryName);

    assertFalse(result.isPresent());

    verify(transaction).begin();
    verify(query).setParameter("name", nonExistentCountryName);
    verify(query).getSingleResult();
    verify(transaction, never()).commit();
    verify(entityManager).close();
  }

  @Test
  void getByName_shouldRollbackAndThrowCountryDAOException() {

    String countryName = "Country1";

    when(entityManager.createQuery(anyString(), eq(Country.class))).thenReturn(query);
    when(query.setParameter("name", countryName)).thenReturn(query);
    when(query.getSingleResult()).thenThrow(new RuntimeException("Exception during retrieval"));

    assertThrows(CountryDAOException.class, () -> countryDAO.getByName(countryName));

    verify(transaction).begin();
    verify(transaction).rollback();
    verify(entityManager).close();

  }
}