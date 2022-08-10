package com.codecompiler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoConfig<Mongo> extends AbstractMongoClientConfiguration {

	@Value("${spring.data.mongodb.host}")
	private String host;
	@Value("${spring.data.mongodb.database}")
	private String database;
	
	protected String getDatabaseName() {		
		return database;
	}
	
	@Override
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(host);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build();
        
        return MongoClients.create(mongoClientSettings);
    }
}