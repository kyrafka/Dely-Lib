package com.mobiles.mobil.config;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mobiles.mobil.repository.SesionRepository;

@Component
public class ExpiredSessionCleanup {
    private static final Logger logger = LoggerFactory.getLogger(ExpiredSessionCleanup.class);

    private final SesionRepository sesionRepository;

    public ExpiredSessionCleanup(SesionRepository sesionRepository) {
        this.sesionRepository = sesionRepository;
    }

    @Scheduled(cron = "${app.sessions.cleanup-cron:0 15 * * * *}")
    public void deleteExpiredSessions() {
        int removed = sesionRepository.deleteExpiredOrInactive(LocalDateTime.now());
        if (removed > 0) {
            logger.info("Removed {} expired or inactive sessions", removed);
        }
    }
}
