package bg.tuvarna.sit.wms.service;

import bg.tuvarna.sit.wms.dao.WarehouseDAO;
import bg.tuvarna.sit.wms.dto.WarehouseDTO;
import bg.tuvarna.sit.wms.dto.WarehouseRentalAgreementDto;
import bg.tuvarna.sit.wms.entities.Address;
import bg.tuvarna.sit.wms.entities.City;
import bg.tuvarna.sit.wms.entities.Country;
import bg.tuvarna.sit.wms.entities.Owner;
import bg.tuvarna.sit.wms.entities.StorageType;
import bg.tuvarna.sit.wms.entities.Warehouse;
import bg.tuvarna.sit.wms.enums.ClimateCondition;
import bg.tuvarna.sit.wms.enums.WarehouseStatus;
import bg.tuvarna.sit.wms.exceptions.CityCreationException;
import bg.tuvarna.sit.wms.exceptions.CountryCreationException;
import bg.tuvarna.sit.wms.exceptions.WarehouseDAOException;
import bg.tuvarna.sit.wms.exceptions.WarehousePersistenceException;
import bg.tuvarna.sit.wms.exceptions.WarehouseServiceException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class WarehouseServiceTest {

  @Mock
  CountryService countryService;
  @Mock
  CityService cityService;
  @Mock
  WarehouseDAO warehouseDAO;
  @Mock
  private UserService userService;
  @InjectMocks
  WarehouseService warehouseService;

  private BufferedReader bufferedReader;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void saveWarehouse_shouldSaveWarehouse() throws WarehouseServiceException, WarehouseDAOException, CountryCreationException, CityCreationException {

    WarehouseDTO warehouseDTO = createWarehouseDTO();
    when(countryService.getOrCreateCountry(anyString())).thenReturn(new Country());
    when(cityService.getOrCreateCity(anyString(), any(Country.class))).thenReturn(new City());

    warehouseService.saveWarehouse(warehouseDTO);

    verify(countryService, times(1)).getOrCreateCountry(anyString());
    verify(cityService, times(1)).getOrCreateCity(anyString(), any(Country.class));
    verify(warehouseDAO, times(1)).save(any(Warehouse.class));
  }

  @Test
  void saveWarehouse_duplicateWarehouseName_shouldThrowWarehouseServiceException() {

    WarehouseDTO warehouseDTO = createWarehouseDTO();
    warehouseDTO.setName("duplicate name");
    when(warehouseDAO.getWarehouseByNameAndOwner(anyString(), any(Owner.class))).thenReturn(Optional.of(createWarehouse()));

    assertThrows(WarehouseServiceException.class, () -> warehouseService.saveWarehouse(warehouseDTO));

    verify(warehouseDAO, times(1)).getWarehouseByNameAndOwner(anyString(), any(Owner.class));
  }

  @Test
  void saveWarehouse_countryServiceThrowsException_shouldThrowWarehouseServiceException() throws CountryCreationException {

    WarehouseDTO warehouseDTO = createWarehouseDTO();
    when(countryService.getOrCreateCountry(anyString())).thenThrow(new CountryCreationException("Country creation failed"));

    assertThrows(WarehouseServiceException.class, () -> warehouseService.saveWarehouse(warehouseDTO));
  }

  @Test
  void saveWarehouse_cityDAOThrowsException_shouldThrowWarehouseServiceException() throws CityCreationException, CountryCreationException {

    WarehouseDTO warehouseDTO = createWarehouseDTO();
    when(countryService.getOrCreateCountry(anyString())).thenReturn(new Country());
    when(cityService.getOrCreateCity(anyString(), any(Country.class))).thenThrow(new CityCreationException("City creation failed"));

    assertThrows(WarehouseServiceException.class, () -> warehouseService.saveWarehouse(warehouseDTO));
  }

  @Test
  void updateWarehouse_shouldUpdateWarehouse() throws CountryCreationException, CityCreationException, WarehouseServiceException, WarehouseDAOException {

    WarehouseDTO warehouseDTO = createWarehouseDTO();
    when(countryService.getOrCreateCountry(anyString())).thenReturn(new Country());
    when(cityService.getOrCreateCity(anyString(), any(Country.class))).thenReturn(new City());

    warehouseService.updateWarehouse(warehouseDTO);

    verify(countryService, times(1)).getOrCreateCountry(anyString());
    verify(cityService, times(1)).getOrCreateCity(anyString(), any(Country.class));
    verify(warehouseDAO, times(1)).update(any(Warehouse.class));
  }

  @Test
  void updateWarehouse_duplicateWarehouseName_shouldThrowWarehouseServiceException() {

    WarehouseDTO warehouseDTO = createWarehouseDTO();
    warehouseDTO.setName("duplicate name");
    when(warehouseDAO.getWarehouseByNameAndOwner(anyString(), any(Owner.class))).thenReturn(Optional.of(createWarehouse()));

    assertThrows(WarehouseServiceException.class, () -> warehouseService.updateWarehouse(warehouseDTO));

    verify(warehouseDAO, times(1)).getWarehouseByNameAndOwner(anyString(), any(Owner.class));
  }

  @Test
  void updateWarehouse_rentedStatusWarehouseDTO_shouldThrowWarehouseServiceException() {

    WarehouseDTO warehouseDTO = createWarehouseDTO();
    warehouseDTO.setStatus(WarehouseStatus.RENTED);

    assertThrows(WarehouseServiceException.class, () -> warehouseService.updateWarehouse(warehouseDTO));
  }

  @Test
  void updateWarehouse_pendingStatusWarehouseDTO_shouldThrowWarehouseServiceException() {

    WarehouseDTO warehouseDTO = createWarehouseDTO();
    warehouseDTO.setStatus(WarehouseStatus.PENDING_RENTAL);

    assertThrows(WarehouseServiceException.class, () -> warehouseService.updateWarehouse(warehouseDTO));
  }

  @Test
  void updateWarehouse_countryServiceThrowsException_shouldThrowWarehouseServiceException() throws CountryCreationException {

    WarehouseDTO warehouseDTO = createWarehouseDTO();
    when(countryService.getOrCreateCountry(anyString())).thenThrow(new CountryCreationException("Country creation failed"));

    assertThrows(WarehouseServiceException.class, () -> warehouseService.updateWarehouse(warehouseDTO));
  }

  @Test
  void updateWarehouse_cityServiceThrowsException_shouldThrowWarehouseServiceException() throws CityCreationException, CountryCreationException {

    WarehouseDTO warehouseDTO = createWarehouseDTO();
    when(countryService.getOrCreateCountry(anyString())).thenReturn(new Country());
    when(cityService.getOrCreateCity(anyString(), any(Country.class))).thenThrow(new CityCreationException("City creation failed"));

    assertThrows(WarehouseServiceException.class, () -> warehouseService.updateWarehouse(warehouseDTO));
  }

  @Test
  void getWarehouseDTOsByOwner_shouldReturnList() throws WarehouseDAOException, WarehouseServiceException {

    Owner owner = new Owner();
    when(warehouseDAO.getAllWarehousesByOwner(owner)).thenReturn(Collections.singletonList(createWarehouse()));

    List<WarehouseDTO> warehouseDTOsResult = warehouseService.getAllWarehouseDTOsByOwner(owner);

    assertNotNull(warehouseDTOsResult);
    assertFalse(warehouseDTOsResult.isEmpty());
  }

  @Test
  void getWarehouseDTOsByOwner_warehouseThrowsException_shouldThrowWarehouseServiceException() throws WarehouseDAOException {

    Owner owner = new Owner();
    when(warehouseDAO.getAllWarehousesByOwner(owner)).thenThrow(new WarehouseDAOException("Retrieving warehouses failed"));

    assertThrows(WarehouseServiceException.class, () -> warehouseService.getAllWarehouseDTOsByOwner(owner));
  }

  @Test
  void deleteWarehouse_correctWarehouseDTO_shouldDeleteWarehouse() throws WarehouseDAOException, WarehouseServiceException {

    WarehouseDTO warehouseDTO = createWarehouseDTO();
    when(warehouseDAO.getById(warehouseDTO.getId())).thenReturn(Optional.of(createWarehouse()));

    warehouseService.deleteWarehouse(warehouseDTO);

    verify(warehouseDAO, times(1)).softDelete(any(Warehouse.class));
  }

  @Test
  void deleteWarehouse_nonExistentWarehouseDTOId_shouldThrowWarehouseServiceException() throws WarehouseDAOException {

   WarehouseDTO warehouseDTO = createWarehouseDTO();
   warehouseDTO.setId(99999999L);
   when(warehouseDAO.getById(warehouseDTO.getId())).thenReturn(Optional.empty());

   assertThrows(WarehouseServiceException.class, () -> warehouseService.deleteWarehouse(warehouseDTO));
  }

  @Test
  void deleteWarehouse_rentedStatusWarehouseDTO_shouldThrowWarehouseServiceException() throws WarehouseDAOException {

    WarehouseDTO warehouseDTO = createWarehouseDTO();
    warehouseDTO.setStatus(WarehouseStatus.RENTED);
    when(warehouseDAO.getById(warehouseDTO.getId())).thenReturn(Optional.of(createWarehouseWithRentedStatus()));

    assertThrows(WarehouseServiceException.class, () -> warehouseService.deleteWarehouse(warehouseDTO));
  }

  @Test
  void deleteWarehouse_pendingRentalStatusWarehouseDTO_shouldThrowWarehouseServiceException() throws WarehouseDAOException {

    WarehouseDTO warehouseDTO = createWarehouseDTO();
    warehouseDTO.setStatus(WarehouseStatus.PENDING_RENTAL);
    when(warehouseDAO.getById(warehouseDTO.getId())).thenReturn(Optional.of(createWarehouseWithPendingStatus()));

    assertThrows(WarehouseServiceException.class, () -> warehouseService.deleteWarehouse(warehouseDTO));
  }

  @Test
  void deleteWarehouse_nullWarehouseDTOId_shouldThrowWarehouseServiceException() throws WarehouseDAOException {

    WarehouseDTO warehouseDTO = createWarehouseDTO();
    warehouseDTO.setId(null);
    when(warehouseDAO.getById(warehouseDTO.getId())).thenThrow(new WarehouseDAOException("Retrieving warehouse by id failed"));

    assertThrows(WarehouseServiceException.class, () -> warehouseService.deleteWarehouse(warehouseDTO));
  }

  @Test
  void deleteWarehouse_warehouseDAOThrowsException_shouldThrowWarehouseServiceException() throws WarehouseDAOException {

    WarehouseDTO warehouseDTO = createWarehouseDTO();
    when(warehouseDAO.getById(warehouseDTO.getId())).thenReturn(Optional.of(createWarehouse()));
    doThrow(new WarehouseDAOException("Deleting warehouse failed")).when(warehouseDAO).softDelete(any(Warehouse.class));

    assertThrows(WarehouseServiceException.class, () -> warehouseService.deleteWarehouse(warehouseDTO));
  }

  @Test
  public void testGetWarehousesWithRentalAgreementsForOwner_Success() throws WarehouseServiceException {
    Long ownerId = 1L;
    WarehouseRentalAgreementDto dto = new WarehouseRentalAgreementDto("Warehouse1", "Street 123", 100.0,
            WarehouseStatus.AVAILABLE, "Storage Type",
            ClimateCondition.AMBIENT, new Date(), new Date(),
            new BigDecimal("1000"), "John", "john@example.com", 2L);
    List<WarehouseRentalAgreementDto> expectedList = Collections.singletonList(dto);

    when(warehouseDAO.getWarehousesWithRentalAgreementsForOwner(ownerId)).thenReturn(expectedList);

    List<WarehouseRentalAgreementDto> result = warehouseService.getWarehousesWithRentalAgreementsForOwner(ownerId);

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(expectedList, result);
  }

  @Test
  public void testGetWarehousesWithRentalAgreementsForOwner_Exception() {

    Long ownerId = 1L;
    when(warehouseDAO.getWarehousesWithRentalAgreementsForOwner(ownerId)).thenThrow(new RuntimeException("Database error"));

    assertThrows(WarehouseServiceException.class,
            () -> warehouseService.getWarehousesWithRentalAgreementsForOwner(ownerId));
  }

  @Test
  void loadWarehousesFromCSV_ShouldLoadWarehouses() throws IOException, WarehousePersistenceException,
          WarehouseDAOException, CityCreationException,
          CountryCreationException {

    String csvData = "name,address,size,status,storageType,climateCondition\n" +
            "Trans Logistics,120.39,3,Center Street,8600,City,Bulgaria,AVAILABLE,HUMIDITY_CONTROLLED,E-Commerce,Used for e-commerce\n";

    bufferedReader = new BufferedReader(new StringReader(csvData));

    when(warehouseDAO.findByName(anyString())).thenReturn(Optional.empty());
    warehouseService.loadWarehousesFromCSV(bufferedReader);

    verify(warehouseDAO, times(1)).save(any(Warehouse.class));
  }

  private WarehouseDTO createWarehouseDTO() {

    WarehouseDTO warehouseDTO = new WarehouseDTO();
    warehouseDTO.setName("Test warehouse");
    warehouseDTO.setCountryName("Test Country");
    warehouseDTO.setCityName("Test City");
    warehouseDTO.setStreet("Street 1");
    warehouseDTO.setZipCode("1234");
    warehouseDTO.setClimateCondition(ClimateCondition.AMBIENT);
    warehouseDTO.setSize(123.0);
    warehouseDTO.setStorageType("Pallet Racking");
    warehouseDTO.setStorageTypeDescription("Heavy-duty shelving for palletized goods");
    warehouseDTO.setStatus(WarehouseStatus.AVAILABLE);
    warehouseDTO.setOwner(new Owner());

    return warehouseDTO;
  }

  private Warehouse createWarehouse() {

    Warehouse warehouse = new Warehouse();

    warehouse.setOwner(new Owner());
    warehouse.setName("Test warehouse");
    warehouse.setClimateCondition(ClimateCondition.AMBIENT);
    warehouse.setSize(123.0);
    warehouse.setStatus(WarehouseStatus.AVAILABLE);
    warehouse.setStorageType(new StorageType() {
      {
        setTypeName("Pallet Racking");
        setDescription("Heavy-duty shelving for palletized goods");
      }
    });
    warehouse.setAddress(new Address() {
      {
        setStreet("Street 1");
        setZipCode("1234");
        setCity(new City() {
          {
            setName("Test City");
            setCountry(new Country() {
              {setName("Test Country");}
            });
          }

        });
      }
    });

    return warehouse;
  }

  private Warehouse createWarehouseWithRentedStatus() {
    Warehouse warehouse = createWarehouse();
    warehouse.setStatus(WarehouseStatus.RENTED);
    return warehouse;
  }

  private Warehouse createWarehouseWithPendingStatus() {
    Warehouse warehouse = createWarehouse();
    warehouse.setStatus(WarehouseStatus.PENDING_RENTAL);
    return warehouse;
  }
}