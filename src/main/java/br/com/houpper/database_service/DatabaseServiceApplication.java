package br.com.houpper.database_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Aplicação responsável pelo gerenciamento do banco de dados. Gerencia a criação e atualização de schemas e a execução
 * das migrações do banco de dados utilizando o Flyway.
 */
@EnableAsync
@EnableDiscoveryClient
@SpringBootApplication
public class DatabaseServiceApplication {

	/**
	 * Inicializa a aplicação de gerenciamento do banco de dados.
	 *
	 * @param args Os argumentos da linha de comando.
	 */
    static void main(String[] args) {
        SpringApplication.run(DatabaseServiceApplication.class, args);
    }
}