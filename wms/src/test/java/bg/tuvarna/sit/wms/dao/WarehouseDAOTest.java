package bg.tuvarna.sit.wms.dao;

import bg.tuvarna.sit.wms.dto.WarehouseRentalAgreementDto;
import bg.tuvarna.sit.wms.enums.ClimateCondition;
import bg.tuvarna.sit.wms.enums.WarehouseStatus;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

class WarehouseDAOTest {

  @Mock
  private EntityManagerFactory entityManagerFactory;
  @Mock
  private EntityManager entityManager;
  @Mock
  private TypedQuery<WarehouseRentalAgreementDto> typedQuery;

  private WarehouseDAO warehouseDAO;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
    when(entityManager.createQuery(anyString(), eq(WarehouseRentalAgreementDto.class))).thenReturn(typedQuery);
    warehouseDAO = new WarehouseDAO(entityManagerFactory);
  }

  @Test
  void testGetWarehousesWithRentalAgreementsForOwner() {
    Long ownerId = 1L;
    WarehouseRentalAgreementDto mockDto = new WarehouseRentalAgreementDto(
            "", "", 1.0, WarehouseStatus.RENTED, "",
            ClimateCondition.AMBIENT, new Date(), new Date(), new BigDecimal(0),
            "", "", 1L);

    List<WarehouseRentalAgreementDto> mockDtos = List.of(mockDto);

    when(typedQuery.setParameter("ownerId", ownerId)).thenReturn(typedQuery);
    when(typedQuery.getResultList()).thenReturn(mockDtos);

    List<WarehouseRentalAgreementDto> result = warehouseDAO.getWarehousesWithRentalAgreementsForOwner(ownerId);

    // Assertions
    verify(entityManagerFactory).createEntityManager();
    verify(entityManager).createQuery(anyString(), eq(WarehouseRentalAgreementDto.class));
    verify(typedQuery).setParameter("ownerId", ownerId);
    verify(typedQuery).getResultList();
    verify(entityManager).close();

    // Assert
    assertEquals(1, result.get(0).getAgentId());
  }
}
