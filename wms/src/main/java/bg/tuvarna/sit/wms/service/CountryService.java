package bg.tuvarna.sit.wms.service;

import bg.tuvarna.sit.wms.dao.CountryDAO;
import bg.tuvarna.sit.wms.entities.Country;
import bg.tuvarna.sit.wms.exceptions.CountryCreationException;
import bg.tuvarna.sit.wms.exceptions.CountryDAOException;

import java.util.Optional;

public class CountryService {

  private final CountryDAO countryDAO;

  public CountryService(CountryDAO countryDAO) {
    this.countryDAO = countryDAO;
  }

  public Country getOrCreateCountry(String countryName) throws CountryCreationException {

    Optional<Country> countryOptional;
    try {
      countryOptional = countryDAO.getByName(countryName);
    } catch (CountryDAOException e) {
      throw new CountryCreationException("Error retrieving country during get or create process", e);
    }

    if(countryOptional.isEmpty()) {
      Country newCountry = new Country();
      newCountry.setName(countryName);

      try {
        countryDAO.save(newCountry);
      } catch (CountryDAOException e) {
        throw new CountryCreationException("Error saving country during get or create process", e);
      }
      return newCountry;
    }

    return countryOptional.get();
  }

}
