package br.com.houpper.database_service.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuração responsável pela execução assíncrona das migrações dos schemas.
 */
@Configuration
@EnableAsync
public class MigrationAsyncConfig {

    /**
     * Executor utilizado exclusivamente pelas migrações Flyway.
     *
     * @return Executor configurado.
     */
    @Bean("migrationTaskExecutor")
    public Executor migrationTaskExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2);

        executor.setMaxPoolSize(5);

        executor.setQueueCapacity(20);

        executor.setThreadNamePrefix("tenant-migration-");

        executor.setWaitForTasksToCompleteOnShutdown(true);

        executor.setAwaitTerminationSeconds(60);

        executor.initialize();

        return executor;
    }
}