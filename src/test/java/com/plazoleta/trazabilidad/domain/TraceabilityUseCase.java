package com.plazoleta.trazabilidad.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.plazoleta.trazabilidad.domain.exceptions.IncompleteTraceabilityException;
import com.plazoleta.trazabilidad.domain.exceptions.InvalidOrderException;
import com.plazoleta.trazabilidad.domain.exceptions.UnauthorizedException;
import com.plazoleta.trazabilidad.domain.exceptions.WrongArgumentException;
import com.plazoleta.trazabilidad.domain.models.EmployeeEfficiencyModel;
import com.plazoleta.trazabilidad.domain.models.OrderEfficiencyModel;
import com.plazoleta.trazabilidad.domain.models.OrderStatus;
import com.plazoleta.trazabilidad.domain.models.TraceabilityModel;
import com.plazoleta.trazabilidad.domain.ports.out.TraceabilityPersistencePort;
import com.plazoleta.trazabilidad.domain.usecase.TraceabilityUseCase;
import com.plazoleta.trazabilidad.domain.util.page.PagedResult;
import com.plazoleta.trazabilidad.infrastructure.security.JwtUtil;
import com.plazoleta.trazabilidad.infrastructure.web.dto.RestaurantClientDto;
import com.plazoleta.trazabilidad.infrastructure.web.dto.UserClientDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TraceabilityUseCaseTest {

    @Mock
    private TraceabilityPersistencePort persistencePort;
    @Mock
    private com.plazoleta.trazabilidad.domain.ports.out.RestaurantClientPort restaurantClient;
    @Mock
    private com.plazoleta.trazabilidad.domain.ports.out.UserClientPort userClientPort;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private TraceabilityUseCase useCase;

    private final String AUTH = "Bearer token";
    private final Long CLIENT_ID = 1L;
    private final Long EMPLOYEE_ID = 2L;
    private final Long RESTAURANT_ID = 10L;
    private TraceabilityModel baseModel;

    @BeforeEach
    void setUp() {
        baseModel = new TraceabilityModel(
                RESTAURANT_ID,
                100L,
                CLIENT_ID,
                null,
                null,
                null,
                null,
                EMPLOYEE_ID,
                null
        );
    }

    @Test
    void save_whenClientIdNull_throwsWrongArgument() {
        TraceabilityModel m = new TraceabilityModel(null, 100L, null, null, null, null, null, null, null);
        assertThrows(WrongArgumentException.class, () -> useCase.save(m, AUTH));
    }

    @Test
    void save_setsEmailsAndDelegatesToPersistence() {
        TraceabilityModel m = new TraceabilityModel(RESTAURANT_ID, 100L, CLIENT_ID, null, null, null, null, EMPLOYEE_ID, null);
        UserClientDto clientDto = mock(UserClientDto.class);
        when(clientDto.email()).thenReturn("client@example.com");
        UserClientDto empDto = mock(UserClientDto.class);
        when(empDto.email()).thenReturn("emp@example.com");
        when(userClientPort.getUserById(CLIENT_ID, AUTH)).thenReturn(clientDto);
        when(userClientPort.getUserById(EMPLOYEE_ID, AUTH)).thenReturn(empDto);
        when(persistencePort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        TraceabilityModel result = useCase.save(m, AUTH);

        assertEquals("client@example.com", result.getClientEmail());
        assertEquals("emp@example.com", result.getEmployeeEmail());
        assertNotNull(result.getDate());
        verify(persistencePort).save(result);
    }

    @Test
    void getLogsByOrderId_delegatesToPersistence() {
        PagedResult<TraceabilityModel> page = mock(PagedResult.class);
        when(persistencePort.getLogsByOrderId(100L, 0, 5, CLIENT_ID, null, null, null))
                .thenReturn(page);
        PagedResult<TraceabilityModel> actual =
                useCase.getLogsByOrderId(100L, 0, 5, CLIENT_ID, null, null, null);
        assertSame(page, actual);
    }

    @Test
    void getEfficiencyByOrder_nullOrderId_throwsInvalidOrder() {
        assertThrows(InvalidOrderException.class, () -> useCase.getEfficiencyByOrder(null));
    }

    @Test
    void getEfficiencyByOrder_noLogs_throwsInvalidOrder() {
        when(persistencePort.findByOrderId(100L)).thenReturn(List.of());
        assertThrows(InvalidOrderException.class, () -> useCase.getEfficiencyByOrder(100L));
    }

    @Test
    void getEfficiencyByOrder_missingStates_throwsIncomplete() {
        TraceabilityModel onlyPrep = new TraceabilityModel(
                RESTAURANT_ID, 100L, CLIENT_ID, null,
                LocalDateTime.now(), null, OrderStatus.EN_PREPARACION, EMPLOYEE_ID, null
        );
        when(persistencePort.findByOrderId(100L)).thenReturn(List.of(onlyPrep));
        assertThrows(IncompleteTraceabilityException.class, () -> useCase.getEfficiencyByOrder(100L));
    }

    @Test
    void getEfficiencyByOrder_success_returnsModel() {
        LocalDateTime t0 = LocalDateTime.now();
        LocalDateTime t1 = t0.plusMinutes(30);
        TraceabilityModel prep = new TraceabilityModel(
                RESTAURANT_ID, 100L, CLIENT_ID, null, t0, null, OrderStatus.EN_PREPARACION, EMPLOYEE_ID, null
        );
        TraceabilityModel delivered = new TraceabilityModel(
                RESTAURANT_ID, 100L, CLIENT_ID, null, t1, OrderStatus.EN_PREPARACION, OrderStatus.ENTREGADO, EMPLOYEE_ID, null
        );
        when(persistencePort.findByOrderId(100L)).thenReturn(List.of(delivered, prep));
        OrderEfficiencyModel eff = useCase.getEfficiencyByOrder(100L);
        assertEquals(100L, eff.getOrderId());
        assertEquals(EMPLOYEE_ID, eff.getEmployeeId());
        assertEquals(Duration.between(t0, t1), eff.getTimeTaken());
    }

    @Test
    void getEmployeesEfficiency_nullRestaurantId_throwsWrongArgument() {
        assertThrows(WrongArgumentException.class, () -> useCase.getEmployeesEfficiency(AUTH, null));
    }

    @Test
    void getEmployeesEfficiency_unauthorized_throwsUnauthorized() {
        when(jwtUtil.getOwnerIdFromSecurityContext()).thenReturn(999L);
        RestaurantClientDto rest = new RestaurantClientDto(RESTAURANT_ID, 123L);
        when(restaurantClient.getById(RESTAURANT_ID, AUTH)).thenReturn(rest);
        assertThrows(UnauthorizedException.class,
                () -> useCase.getEmployeesEfficiency(AUTH, RESTAURANT_ID));
    }

    @Test
    void getEmployeesEfficiency_noLogs_throwsWrongArgument() {
        when(jwtUtil.getOwnerIdFromSecurityContext()).thenReturn(123L);
        RestaurantClientDto rest = new RestaurantClientDto(RESTAURANT_ID, 123L);
        when(restaurantClient.getById(RESTAURANT_ID, AUTH)).thenReturn(rest);
        when(persistencePort.findByRestaurantId(AUTH, RESTAURANT_ID)).thenReturn(List.of());
        assertThrows(WrongArgumentException.class,
                () -> useCase.getEmployeesEfficiency(AUTH, RESTAURANT_ID));
    }
}
