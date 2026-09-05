package dev.gad.main.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration(proxyBeanMethods = false)
public class DataSourceConfig {

	@Bean
	@Primary
	@ConfigurationProperties("spring.datasource.mysql")
	public DataSourceProperties mysqlDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@Primary
	public DataSource mysqlDataSource(
			@Qualifier("mysqlDataSourceProperties") DataSourceProperties properties) {
		return properties.initializeDataSourceBuilder().build();
	}

	@Bean
	@ConfigurationProperties("spring.datasource.postgres")
	public DataSourceProperties postgresDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	public DataSource postgresDataSource(
			@Qualifier("postgresDataSourceProperties") DataSourceProperties properties) {
		return properties.initializeDataSourceBuilder().build();
	}
}
