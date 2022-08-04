package com.codecompiler.binary.api.config;

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
 
//    @Override
//    public Collection getMappingBasePackages() {
//        return Collections.singleton("com.baeldung");
//    }
	
//	@Override
//	public MongoClient mongo() throws Exception {
//		return new MongoClient(host);
//	}

//
//	@Bean
//	public GridFsTemplate gridFsTemplate() throws Exception {
//		return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
//	}
//	

}