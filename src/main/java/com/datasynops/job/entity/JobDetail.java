package com.datasynops.job.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
@AllArgsConstructor
public class JobDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="job_id", nullable=false)
    private Job job;
    private String name;
    @Enumerated(EnumType.STRING)
    private JobDetailEnum status;
    private byte[] optionalGraphicContent;
    private byte[] optionalText;
    private byte[] csvText;
    private String responseMsg;
    private String msgProviderRef;
    private String channelAuthKey;
    private String responseStatusCode;
    public JobDetail() {
    }
}
