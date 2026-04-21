package com.ufpr.bantads.conta.application.dto.event;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ContaEvent {

    private String eventId;
    private String eventType;
    private LocalDateTime eventDate;
    
}
