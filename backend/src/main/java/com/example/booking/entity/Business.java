package com.example.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "businesses")
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    private String name;

    private String description;
    private String address;
    private String phone;

    @Column(name = "created_at",nullable = false, insertable = false, updatable = false)
    private Instant createdAt;
}
