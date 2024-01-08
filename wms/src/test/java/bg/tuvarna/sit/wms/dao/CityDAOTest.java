package bg.tuvarna.sit.wms.dao;

import bg.tuvarna.sit.wms.entities.City;
import bg.tuvarna.sit.wms.entities.Country;
import bg.tuvarna.sit.wms.exceptions.CityDAOException;
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

class CityDAOTest {

  @Mock
  private EntityManagerFactory entityManagerFactory;

  @Mock
  private EntityManager entityManager;

  @Mock
  private EntityTransaction transaction;

  @Mock
  private TypedQuery<City> query;

  @InjectMocks
  private CityDAO cityDAO;

  @BeforeEach
  void setUp() {

    MockitoAnnotations.openMocks(this);
    when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
    when(entityManager.getTransaction()).thenReturn(transaction);
    when(entityManager.isOpen()).thenReturn(true);
    when(transaction.isActive()).thenReturn(true);
  }

  @Test
  void save_shouldPersistCity() throws CityDAOException {

    City city = new City();

    cityDAO.save(city);

    verify(entityManager).getTransaction();
    verify(transaction).begin();
    verify(entityManager).persist(city);
    verify(transaction).commit();
    verify(entityManager).close();
  }

  @Test
  void save_whenException_shouldRollbackAndThrowCityDAOException() {

    City city = new City();
    doThrow(new RuntimeException("Exception during persistence")).when(entityManager).persist(city);

    assertThrows(CityDAOException.class, () -> cityDAO.save(city));

    verify(entityManager).getTransaction();
    verify(transaction).begin();
    verify(entityManager).persist(city);
    verify(transaction).rollback();
    verify(entityManager).close();
  }

  @Test
  void getByNameAndCountry_shouldReturnCityOptional() throws CityDAOException {

    City expectedCity = new City();
    Country country = new Country();
    expectedCity.setName("City1");
    country.setName("Country1");
    expectedCity.setCountry(country);
    when(entityManager.createQuery(anyString(), eq(City.class))).thenReturn(query);
    when(query.setParameter("name", expectedCity.getName())).thenReturn(query);
    when(query.setParameter("country", expectedCity.getCountry())).thenReturn(query);
    when(query.getSingleResult()).thenReturn(expectedCity);

    Optional<City> result = cityDAO.getByNameAndCountry(expectedCity.getName(), expectedCity.getCountry());

    assertTrue(result.isPresent());
    assertEquals(expectedCity.getName(), result.get().getName());
    assertEquals(expectedCity.getCountry().getName(), result.get().getCountry().getName());

    verify(transaction).begin();
    verify(transaction).commit();
    verify(entityManager).close();
  }

  @Test
  void getByNameAndCountry_nonExistentCity_shouldReturnEmptyOptional() throws CityDAOException {

    String nonExistentCityName = "Non Existent City";
    Country country = new Country();
    country.setName("Country1");
    when(entityManager.createQuery(anyString(), eq(City.class))).thenReturn(query);
    when(query.setParameter("name", nonExistentCityName)).thenReturn(query);
    when(query.setParameter("country", country)).thenReturn(query);
    when(query.getSingleResult()).thenThrow(new NoResultException());

    Optional<City> result = cityDAO.getByNameAndCountry(nonExistentCityName, country);

    assertFalse(result.isPresent());

    verify(transaction).begin();
    verify(transaction, never()).commit();
    verify(entityManager).close();
  }

  @Test
  void getByNameAndCountry_shouldRollbackAndThrowCityDAOException() {

    String cityName = "City1";
    Country country = new Country();
    country.setName("Country1");
    when(entityManager.createQuery(anyString(), eq(City.class))).thenReturn(query);
    when(query.setParameter("name", cityName)).thenReturn(query);
    when(query.setParameter("country", country)).thenReturn(query);
    when(query.getSingleResult()).thenThrow(new RuntimeException("Exception during retrieval"));

    assertThrows(CityDAOException.class, () -> cityDAO.getByNameAndCountry(cityName, country));

    verify(transaction).begin();
    verify(transaction).rollback();
    verify(entityManager).close();
  }

}