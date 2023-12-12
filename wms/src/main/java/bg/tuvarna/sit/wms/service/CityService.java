package bg.tuvarna.sit.wms.service;

import bg.tuvarna.sit.wms.dao.CityDAO;
import bg.tuvarna.sit.wms.entities.City;
import bg.tuvarna.sit.wms.entities.Country;
import bg.tuvarna.sit.wms.exceptions.CityCreationException;
import bg.tuvarna.sit.wms.exceptions.CityDAOException;

import java.util.Optional;

public class CityService {

  private final CityDAO cityDAO;

  public CityService(CityDAO cityDAO) {
    this.cityDAO = cityDAO;
  }

  public City getOrCreateCity(String cityName, Country country) throws CityCreationException {

    Optional<City> cityOptional;
    try {
      cityOptional = cityDAO.getByNameAndCountry(cityName, country);
    } catch (CityDAOException e) {
      throw new CityCreationException("Error retrieving city during get or create process", e);
    }

    if(cityOptional.isEmpty()) {
      City newCity = new City();
      newCity.setName(cityName);
      newCity.setCountry(country);

      try {
        cityDAO.save(newCity);
      } catch (CityDAOException e) {
        throw new CityCreationException("Error saving city during get or create process", e);
      }
      return newCity;
    }

    return cityOptional.get();
  }

}
