package com.umerqureshicodes.tidier.limits;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="limits")
public class Limit {
    @Id
    private Long id = 1L;

    private long totalVideoMillis;

    protected Limit() {}

    public Limit(long totalVideoMillis) {
        this.id = 1L;
        this.totalVideoMillis = totalVideoMillis;
    }

    public long getTotalVideoMillis() { return totalVideoMillis; }
    public void setTotalVideoMillis(long totalVideoMillis) { this.totalVideoMillis = totalVideoMillis; }
}
