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

/**
 * This class provides services related to warehouse operations.
 * It includes methods for saving, updating, retrieving, and deleting warehouses.
 * It also includes methods for checking the uniqueness of a warehouse name and mapping between DTO and entity objects.
 */
public class WarehouseService {

  private final WarehouseDAO warehouseDAO;
  private final CountryService countryService;
  private final CityService cityService;
  private static final Logger LOGGER = LogManager.getLogger(WarehouseService.class);

  public WarehouseService(WarehouseDAO warehouseDAO, CountryService countryService, CityService cityService) {
    this.warehouseDAO = warehouseDAO;
    this.countryService = countryService;
    this.cityService = cityService;
  }

  /**
   * Saves a warehouse to the database.
   *
   * @param warehouseDTO the DTO containing the warehouse data
   * @throws WarehouseServiceException if an error occurs during the the persistence process
   * or if the warehouse name isn't unique for an owner
   */
  public void saveWarehouse(WarehouseDTO warehouseDTO) throws WarehouseServiceException {

    if (!isWarehouseNameUnique(warehouseDTO)) {
      String errorMessage = "Warehouse with the same name already exists";
      LOGGER.error(errorMessage);
      throw new WarehouseServiceException(errorMessage);
    }

    Warehouse warehouse = mapDTOToEntity(warehouseDTO);
    warehouse.setStatus(WarehouseStatus.AVAILABLE);

    try {
      Country country = countryService.getOrCreateCountry(warehouseDTO.getCountryName());
      City city = cityService.getOrCreateCity(warehouseDTO.getCityName(), country);

      warehouse.getAddress().setCity(city);
    } catch (CountryCreationException e) {
      String errorMessage = "Error creating country during warehouse persistence";
      LOGGER.error(errorMessage, e);
      throw new WarehouseServiceException(errorMessage, e);
    } catch (CityCreationException e) {
      String errorMessage = "Error creating city during warehouse persistence";
      LOGGER.error(errorMessage, e);
      throw new WarehouseServiceException(errorMessage, e);
    }

    try {
      warehouseDAO.save(warehouse);
    } catch (WarehouseDAOException e) {
      String errorMessage = "Unexpected error during warehouse persistence";
      LOGGER.error(errorMessage, e);
      throw new WarehouseServiceException(errorMessage, e);
    }
  }

  /**
   * Updates the details of an existing warehouse.
   *
   * @param warehouseDTO the DTO containing the updated details of a warehouse
   * @throws WarehouseServiceException if an error occurs during the the updating process
   * or if the warehouse name matches that of an existing warehouse for an owner
   */
  public void updateWarehouse(WarehouseDTO warehouseDTO) throws WarehouseServiceException {

    if (!isWarehouseNameUnique(warehouseDTO)) {
      String errorMessage = "Warehouse with the same name already exists";
      LOGGER.error(errorMessage);
      throw new WarehouseServiceException(errorMessage);
    }

    if (!warehouseDTO.getStatus().equals(WarehouseStatus.AVAILABLE)) {
      String errorMessage = "Cannot update warehouse. Only warehouses with 'available' status can be updated";
      LOGGER.error(errorMessage);
      throw new WarehouseServiceException(errorMessage);
    }

    Warehouse warehouse = mapDTOToEntity(warehouseDTO);

    try {
      Country country = countryService.getOrCreateCountry(warehouseDTO.getCountryName());
      City city = cityService.getOrCreateCity(warehouseDTO.getCityName(), country);

      warehouse.getAddress().setCity(city);
    } catch (CountryCreationException e) {
      String errorMessage = "Error creating country during warehouse update";
      LOGGER.error(errorMessage, e);
      throw new WarehouseServiceException(errorMessage, e);
    } catch (CityCreationException e) {
      String errorMessage = "Error creating city during warehouse update";
      LOGGER.error(errorMessage, e);
      throw new WarehouseServiceException(errorMessage, e);
    }

    try {
      warehouseDAO.update(warehouse);
    } catch (WarehouseDAOException e) {
      String errorMessage = "Error during the warehouse updating process";
      LOGGER.error(errorMessage, e);
      throw new WarehouseServiceException(errorMessage, e);
    }
  }

  /**
   * Retrieves all of the warehouses owned by a given owner.
   *
   * @param owner the owner of the warehouses to be retrieved
   * @return a list of DTOs containing information about all of the warehouses owned by the owner
   * @throws WarehouseServiceException if there is an error during the retrieval process
   */
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

  /**
   * Deletes a warehouse from the database.
   *
   * @param warehouseDTO a DTO containing warehouse information to be deleted
   * @throws WarehouseServiceException if there is an error during the deletion process
   * or if the warehouse isn't found in the database
   */
  public void deleteWarehouse(WarehouseDTO warehouseDTO) throws WarehouseServiceException {

    Optional<Warehouse> warehouseOptional;
    try {
      warehouseOptional = warehouseDAO.getById(warehouseDTO.getId());
    } catch (WarehouseDAOException e) {
      String errorMessage = "Unexpected error retrieving warehouse during deletion process";
      LOGGER.error(errorMessage, e);
      throw new WarehouseServiceException(errorMessage, e);
    }

    if (warehouseOptional.isEmpty()) {
      String errorMessage = "Error: warehouse scheduled for deletion not found";
      LOGGER.error(errorMessage);
      throw new WarehouseServiceException(errorMessage);
    }

    Warehouse warehouse = warehouseOptional.get();
    if (!warehouse.getStatus().equals(WarehouseStatus.AVAILABLE)) {
      String errorMessage = "Cannot delete warehouse. Only warehouses with 'available' status can be deleted";
      LOGGER.error(errorMessage);
      throw new WarehouseServiceException(errorMessage);
    }

    try {
      warehouseDAO.softDelete(warehouse);
    } catch (WarehouseDAOException e) {
      String errorMessage = "Unexpected error during warehouse deletion";
      LOGGER.error(errorMessage, e);
      throw new WarehouseServiceException(errorMessage, e);
    }
  }

  /**
   * Checks if the name of a warehouse is unique for its owner.
   *
   * @param warehouseDTO the DTO containing the name of a warehouse to be checked for uniqueness
   * @return true if the warehouse name is unique, false if it's not
   * @throws WarehouseServiceException if there is an error during the check
   */
  private boolean isWarehouseNameUnique(WarehouseDTO warehouseDTO) throws WarehouseServiceException {

    if (warehouseDTO.getId() != null) {
      Optional<Warehouse> oldWarehouse;
      try {
        oldWarehouse = warehouseDAO.getById(warehouseDTO.getId());
      } catch (WarehouseDAOException e) {
        String errorMessage = "Error during warehouse name uniqueness check";
        LOGGER.error(errorMessage, e);
        throw new WarehouseServiceException(errorMessage, e);
      }

      if (oldWarehouse.isPresent() && warehouseDTO.getName().equals(oldWarehouse.get().getName())) {
        return true;
      }
    }
    return warehouseDAO.getWarehouseByNameAndOwner(warehouseDTO.getName(), warehouseDTO.getOwner()).isEmpty();
  }

  /**
   * Creates a new Warehouse entity from the warehouse details provided by a given WarehouseDTO object.
   *
   * @param warehouseDTO the DAO containing the warehouse details
   * @return a new warehouse entity
   */
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

  /**
   * Creates a new WarehouseDTO, containing information about a warehouse, from a Warehouse entity.
   *
   * @param warehouse the warehouse entity to be converted to a DTO
   * @return a new WarehouseDTO object
   */
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
