package bg.tuvarna.sit.wms.service;

import bg.tuvarna.sit.wms.dao.CityDAO;
import bg.tuvarna.sit.wms.dao.CountryDAO;
import bg.tuvarna.sit.wms.dao.WarehouseDAO;
import bg.tuvarna.sit.wms.entities.City;
import bg.tuvarna.sit.wms.entities.Country;
import bg.tuvarna.sit.wms.entities.Warehouse;

import java.util.Optional;

public class WarehouseService {
  private final WarehouseDAO warehouseDAO = new WarehouseDAO();
  private final CountryDAO countryDAO = new CountryDAO();
  private final CityDAO cityDAO = new CityDAO();

  public void saveWarehouse(Warehouse warehouse) {
    Optional<Country> countryOptional = countryDAO.getByName(warehouse.getAddress().getCity().getCountry().getName());
    Country country;
    if(countryOptional.isEmpty()) {
      country = warehouse.getAddress().getCity().getCountry();
      countryDAO.save(country);
    } else {
      country = countryOptional.get();
    }

    warehouse.getAddress().getCity().setCountry(country);

    Optional<City> cityOptional = cityDAO.getByName(warehouse.getAddress().getCity().getName());
    City city;
    if(cityOptional.isEmpty()) {
      city = warehouse.getAddress().getCity();
      cityDAO.save(city);
    } else {
      city = cityOptional.get();
    }

    warehouse.getAddress().setCity(city);
    warehouseDAO.save(warehouse);
  }
}
