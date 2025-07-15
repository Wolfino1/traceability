package com.plazoleta.trazabilidad.infrastructure.entity;

import com.plazoleta.trazabilidad.domain.models.OrderStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Document(collection = "trazabilidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraceabilityEntity {
    @Id
    private String id;
    private Long orderId;
    private Long clientId;
    private String clientEmail;
    private LocalDateTime date;
    private OrderStatus previousState;
    private OrderStatus newState;
    private Long employeeId;
    private String employeeEmail;
}
