package com.tripwhiz.tripwhizuserback.luggage.entity;

import com.tripwhiz.tripwhizuserback.store.domain.Spot;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LuggageMove {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lmno; // 이동 번호

    @ManyToOne
    @JoinColumn(name = "source_spno", nullable = false) // 출발 지점
    private Spot sourceSpot;

    @ManyToOne
    @JoinColumn(name = "destination_spno", nullable = false) // 도착 지점
    private Spot destinationSpot;


    private String email;

    @CreatedDate
    @Builder.Default
    private LocalDateTime moveDate = LocalDateTime.now(); // 이동 요청 날짜

    @CreatedDate
    @Builder.Default
    private LocalDateTime startDate = LocalDateTime.now(); // 출발 날짜

    @LastModifiedDate
    @Builder.Default
    private LocalDateTime endDate = LocalDateTime.now(); // 도착 날짜

    @Builder.Default
    private LuggageMoveStatus status = LuggageMoveStatus.PENDING;
}
