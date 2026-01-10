package com.umerqureshicodes.tidier.limits;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LimitService {

    private final LimitRepo limitRepo;
    private final long maxMillis = 600L * 60L * 1000L; // 600 minutes

    // I AM TESTING RUG

    public LimitService(LimitRepo limitRepo) {
        this.limitRepo = limitRepo;
    }

    @Transactional
    public boolean exceededLimit(long uploadedVideosDuration) {
        Limit limit = limitRepo.getSingletonForUpdate()
                .orElseGet(() -> limitRepo.save(new Limit(0)));

        long newTotal = limit.getTotalVideoMillis()+ uploadedVideosDuration;

        if (newTotal >= maxMillis) { // use >= if you want to block exactly-at-limit too
            return true;
        }

        limit.setTotalVideoMillis(newTotal);
        return false;
    }

}
