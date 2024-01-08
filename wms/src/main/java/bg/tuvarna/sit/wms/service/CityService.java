package bg.tuvarna.sit.wms.service;

import bg.tuvarna.sit.wms.dao.CityDAO;
import bg.tuvarna.sit.wms.entities.City;
import bg.tuvarna.sit.wms.entities.Country;
import bg.tuvarna.sit.wms.exceptions.CityCreationException;
import bg.tuvarna.sit.wms.exceptions.CityDAOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

/**
 * A service class for the City entity.
 * It provides a method for persisting a city entity or retrieving it if it already exists in the database.
 */
public class CityService {

  private final CityDAO cityDAO;
  private static final Logger LOGGER = LogManager.getLogger(CityService.class);

  public CityService(CityDAO cityDAO) {
    this.cityDAO = cityDAO;
  }

  /**
   * Retrieves a city entity by its name and country from the database or creates a new City entity,
   * if it doesn't exist and persists it
   *
   * @param cityName the name of the city entity
   * @param country the country of the city entity
   * @return a newly created city entity if the city doesn't already exist or the existing city entity
   * @throws CityCreationException if an error occurs during the get or create process
   */
  public City getOrCreateCity(String cityName, Country country) throws CityCreationException {

    Optional<City> cityOptional;
    try {
      cityOptional = cityDAO.getByNameAndCountry(cityName, country);
    } catch (CityDAOException e) {
      String errorMessage = "Error retrieving city during get or create process";
      LOGGER.error(errorMessage, e);
      throw new CityCreationException(errorMessage, e);
    }

    if(cityOptional.isEmpty()) {
      City newCity = new City();
      newCity.setName(cityName);
      newCity.setCountry(country);

      try {
        cityDAO.save(newCity);
      } catch (CityDAOException e) {
        String errorMessage = "Error saving city during get or create process";
        LOGGER.error(errorMessage, e);
        throw new CityCreationException(errorMessage, e);
      }
      return newCity;
    }

    return cityOptional.get();
  }
}
