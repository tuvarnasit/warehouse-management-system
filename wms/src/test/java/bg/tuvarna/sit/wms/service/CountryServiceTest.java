package bg.tuvarna.sit.wms.service;

import bg.tuvarna.sit.wms.dao.CountryDAO;
import bg.tuvarna.sit.wms.entities.Country;
import bg.tuvarna.sit.wms.exceptions.CountryCreationException;
import bg.tuvarna.sit.wms.exceptions.CountryDAOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CountryServiceTest {

  @Mock
  private CountryDAO countryDAO;
  @InjectMocks
  private CountryService countryService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getOrCreateCountry_countryExist_shouldReturnCountry() throws CountryDAOException, CountryCreationException {

    Country existingCountry = new Country();
    String countryName = "Already existing country";
    existingCountry.setName(countryName);
    when(countryDAO.getByName(countryName)).thenReturn(Optional.of(existingCountry));

    Country countryResult = countryService.getOrCreateCountry(countryName);

    assertNotNull(countryResult);
    assertEquals(countryName, countryResult.getName());
    verify(countryDAO, times(0)).save(any(Country.class));
  }

  @Test
  void getOrCreateCountry_countryDoesNotExist_shouldCreateNewCountry() throws CountryDAOException, CountryCreationException {

    Country country = new Country();
    String countryName = "Already existing country";
    country.setName(countryName);
    when(countryDAO.getByName(anyString())).thenReturn(Optional.empty());

    Country countryResult = countryService.getOrCreateCountry(countryName);

    assertNotNull(countryResult);
    assertEquals(countryName, countryResult.getName());
    verify(countryDAO, times(1)).save(any(Country.class));
  }

  @Test
  void getOrCreateCountry_countryDAOThrowsException_shouldThrowCountryCreationException() throws CountryDAOException {

    String countryName = "Country";
    doThrow(new CountryDAOException("Creating country failed")).when(countryDAO).save(any(Country.class));

    assertThrows(CountryCreationException.class, () -> countryService.getOrCreateCountry(countryName));
  }
}