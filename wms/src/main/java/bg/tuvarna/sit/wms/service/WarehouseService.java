package bg.tuvarna.sit.wms.service;

import bg.tuvarna.sit.wms.dao.WarehouseDAO;
import bg.tuvarna.sit.wms.dto.WarehouseDTO;
import bg.tuvarna.sit.wms.entities.Address;
import bg.tuvarna.sit.wms.entities.City;
import bg.tuvarna.sit.wms.entities.Country;
import bg.tuvarna.sit.wms.entities.Owner;
import bg.tuvarna.sit.wms.entities.StorageType;
import bg.tuvarna.sit.wms.entities.Warehouse;
import bg.tuvarna.sit.wms.enums.WarehouseStatus;
import bg.tuvarna.sit.wms.exceptions.CityCreationException;
import bg.tuvarna.sit.wms.exceptions.CountryCreationException;
import bg.tuvarna.sit.wms.exceptions.WarehouseDAOException;
import bg.tuvarna.sit.wms.exceptions.WarehouseServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

public class WarehouseService {

  private final WarehouseDAO warehouseDAO ;
  private final CountryService countryService;
  private final CityService cityService;
  private static final Logger LOGGER = LogManager.getLogger(WarehouseService.class);

  public WarehouseService(WarehouseDAO warehouseDAO, CountryService countryService, CityService cityService) {
    this.warehouseDAO = warehouseDAO;
    this.countryService = countryService;
    this.cityService = cityService;
  }

  public void saveWarehouse(WarehouseDTO warehouseDTO) throws WarehouseServiceException {

    Warehouse warehouse = mapDTOToEntity(warehouseDTO);
    warehouse.setStatus(WarehouseStatus.AVAILABLE);

    try {
      Country country = countryService.getOrCreateCountry(warehouseDTO.getCountryName());
      City city = cityService.getOrCreateCity(warehouseDTO.getCityName(), country);

      warehouse.getAddress().setCity(city);
    } catch (CountryCreationException e) {
      String errorMessage = "Error creating country during warehouse persistence";
      LOGGER.error(errorMessage);
      throw new WarehouseServiceException(errorMessage, e);
    } catch (CityCreationException e) {
      String errorMessage = "Error creating city during warehouse persistence";
      LOGGER.error(errorMessage);
      throw new WarehouseServiceException(errorMessage, e);
    }

    try {
      warehouseDAO.save(warehouse);
    } catch (WarehouseDAOException e) {
      String errorMessage = "Unexpected error during warehouse persistence ";
      LOGGER.error(errorMessage);
      throw new WarehouseServiceException(errorMessage, e);
    }
  }

  public void updateWarehouse(WarehouseDTO warehouseDTO) throws WarehouseServiceException {
    Warehouse warehouse = mapDTOToEntity(warehouseDTO);

    try {
      Country country = countryService.getOrCreateCountry(warehouseDTO.getCountryName());
      City city = cityService.getOrCreateCity(warehouseDTO.getCityName(), country);

      warehouse.getAddress().setCity(city);
    } catch (CountryCreationException e) {
      String errorMessage = "Error creating country during warehouse update";
      LOGGER.error(errorMessage);
      throw new WarehouseServiceException(errorMessage, e);
    } catch (CityCreationException e) {
      String errorMessage = "Error creating city during warehouse update";
      LOGGER.error(errorMessage);
      throw new WarehouseServiceException(errorMessage, e);
    }

    try {
      warehouseDAO.update(warehouse);
    } catch (WarehouseDAOException e) {
      String errorMessage = "Error during the warehouse updating process";
      LOGGER.error(errorMessage);
      throw new WarehouseServiceException(errorMessage, e);
    }
  }

  public List<WarehouseDTO> getWarehouseDTOsByOwner(Owner owner) throws WarehouseServiceException {

    List<Warehouse> ownerWarehouses;
    try {
      ownerWarehouses = warehouseDAO.getWarehousesByOwner(owner);
    } catch (WarehouseDAOException e) {
      String errorMessage = "Error during retrieval of owner warehouses";
      LOGGER.error(errorMessage);
      throw new WarehouseServiceException(errorMessage, e);
    }
    return ownerWarehouses.stream().map(this::mapEntityToDTO).toList();
  }

  public void deleteWarehouse(WarehouseDTO warehouseDTO) throws WarehouseServiceException {

    Optional<Warehouse> warehouseOptional;
    try {
      warehouseOptional = warehouseDAO.getById(warehouseDTO.getId());
    } catch (WarehouseDAOException e) {
      String errorMessage = "Unexpected error retrieving warehouse during deletion process";
      LOGGER.error(errorMessage);
      throw new WarehouseServiceException(errorMessage, e);
    }

    if(warehouseOptional.isEmpty()) {
      String errorMessage = "Error: warehouse scheduled for deletion not found";
      LOGGER.error(errorMessage);
      throw new WarehouseServiceException(errorMessage);
    }

    try {
      warehouseDAO.delete(warehouseOptional.get());
    } catch (WarehouseDAOException e) {
      String errorMessage = "Unexpected error during warehouse deletion";
      LOGGER.error(errorMessage);
      throw new WarehouseServiceException(errorMessage, e);
    }
  }

  private Warehouse mapDTOToEntity(WarehouseDTO warehouseDTO) {

    Warehouse warehouse = new Warehouse();
    Country country = new Country();
    City city = new City();
    Address address = new Address();
    StorageType storageType = new StorageType();

    country.setName(warehouseDTO.getName());
    city.setName(warehouseDTO.getName());
    city.setCountry(country);

    address.setStreet(warehouseDTO.getStreet());
    address.setZipCode(warehouseDTO.getZipCode());
    address.setCity(city);

    storageType.setTypeName(warehouseDTO.getStorageType());
    storageType.setDescription(warehouseDTO.getStorageTypeDescription());

    warehouse.setId(warehouseDTO.getId());
    warehouse.setName(warehouseDTO.getName());
    warehouse.setAddress(address);
    warehouse.setStorageType(storageType);
    warehouse.setSize(warehouseDTO.getSize());
    warehouse.setStatus(warehouseDTO.getStatus());
    warehouse.setClimateCondition(warehouseDTO.getClimateCondition());
    warehouse.setOwner(warehouseDTO.getOwner());

    return warehouse;
  }

  private WarehouseDTO mapEntityToDTO(Warehouse warehouse) {

    WarehouseDTO warehouseDTO = new WarehouseDTO();

    warehouseDTO.setId(warehouse.getId());
    warehouseDTO.setName(warehouse.getName());
    warehouseDTO.setCityName(warehouse.getAddress().getCity().getName());
    warehouseDTO.setCountryName(warehouse.getAddress().getCity().getCountry().getName());
    warehouseDTO.setStreet(warehouse.getAddress().getStreet());
    warehouseDTO.setZipCode(warehouse.getAddress().getZipCode());
    warehouseDTO.setClimateCondition(warehouse.getClimateCondition());
    warehouseDTO.setStorageType(warehouse.getStorageType().getTypeName());
    warehouseDTO.setStorageTypeDescription(warehouse.getStorageType().getDescription());
    warehouseDTO.setSize(warehouse.getSize());
    warehouseDTO.setOwner(warehouse.getOwner());
    warehouseDTO.setStatus(warehouse.getStatus());

    return warehouseDTO;
  }
}
