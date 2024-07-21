package com.datasynops.job.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Set;

@AllArgsConstructor
@Entity
@Data
public class MessagingProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String providerName;
    @Enumerated(EnumType.STRING)
    private ProviderStatus status;
    private String token;
    private String instance;
    private Long msgInterval;

    public MessagingProvider() {
    }
}
