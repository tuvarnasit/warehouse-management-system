package bg.tuvarna.sit.wms.service;

import bg.tuvarna.sit.wms.dao.CityDAO;
import bg.tuvarna.sit.wms.entities.City;
import bg.tuvarna.sit.wms.entities.Country;
import bg.tuvarna.sit.wms.exceptions.CityCreationException;
import bg.tuvarna.sit.wms.exceptions.CityDAOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

public class CityServiceTest {

  @Mock
  CityDAO cityDAO;
  @InjectMocks
  CityService cityService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getOrCreateCity_cityExists_shouldReturnExistingCity() throws CityDAOException, CityCreationException {

    Country existingCountry = new Country();
    String countryName = "Already existing country";
    existingCountry.setName(countryName);
    String cityName = "Already existing city";
    City existingCity = new City();
    existingCity.setName(cityName);
    existingCity.setCountry(existingCountry);
    when(cityDAO.getByNameAndCountry(cityName, existingCountry)).thenReturn(Optional.of(existingCity));

    City resultCity = cityService.getOrCreateCity(cityName, existingCountry);

    assertNotNull(resultCity);
    assertEquals(cityName, resultCity.getName());
    verify(cityDAO, times(0)).save(any(City.class));
  }

  @Test
  void getOrCreateCity_cityDoesNotExist_shouldCreateNewCity() throws CityDAOException, CityCreationException {

    Country country = new Country();
    String countryName = "Example Country";
    country.setName(countryName);
    String cityName = "New city";
    City city = new City();
    city.setName(cityName);
    city.setCountry(country);
    when(cityDAO.getByNameAndCountry(anyString(), any(Country.class))).thenReturn(Optional.empty());

    City resultCity = cityService.getOrCreateCity(cityName, country);

    assertNotNull(resultCity);
    assertEquals(cityName, resultCity.getName());
    verify(cityDAO, times(1)).save(any(City.class));
  }

  @Test
  void getOrCreateCity_cityDAOThrowsException_shouldThrowCityCreationException() throws CityDAOException {

    Country country = new Country();
    String countryName = "Country";
    country.setName(countryName);
    String cityName = "City";
    City city = new City();
    city.setName(cityName);
    city.setCountry(country);
    doThrow(new CityDAOException("Creating country failed")).when(cityDAO).save(any(City.class));

    assertThrows(CityCreationException.class, () -> cityService.getOrCreateCity(cityName, country));
  }
}
