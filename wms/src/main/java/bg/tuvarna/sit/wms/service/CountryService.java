package bg.tuvarna.sit.wms.service;

import bg.tuvarna.sit.wms.dao.CountryDAO;
import bg.tuvarna.sit.wms.entities.Country;
import bg.tuvarna.sit.wms.exceptions.CountryCreationException;
import bg.tuvarna.sit.wms.exceptions.CountryDAOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

/**
 * A service class for the Country entity.
 * It provides a method for persisting a country entity or retrieving it if it already exists in the database.
 */
public class CountryService {

  private final CountryDAO countryDAO;
  private static final Logger LOGGER = LogManager.getLogger(CountryService.class);

  public CountryService(CountryDAO countryDAO) {
    this.countryDAO = countryDAO;
  }

  /**
   * Retrieves a country entity by its name from the database or creates a new Country entity,
   * if it doesn't exist and persists it
   *
   * @param countryName the name of the country entity
   * @return a newly created country entity if the country doesn't already exist or the existing country entity
   * @throws CountryCreationException if an error occurs during the get or create process
   */
  public Country getOrCreateCountry(String countryName) throws CountryCreationException {

    Optional<Country> countryOptional;
    try {
      countryOptional = countryDAO.getByName(countryName);
    } catch (CountryDAOException e) {
      String errorMessage = "Error retrieving country during get or create process";
      LOGGER.error(errorMessage, e);
      throw new CountryCreationException(errorMessage, e);
    }

    if(countryOptional.isEmpty()) {
      Country newCountry = new Country();
      newCountry.setName(countryName);

      try {
        countryDAO.save(newCountry);
      } catch (CountryDAOException e) {
        String errorMessage = "Error saving country during get or create process";
        LOGGER.error(errorMessage, e);
        throw new CountryCreationException(errorMessage, e);
      }
      return newCountry;
    }

    return countryOptional.get();
  }
}
