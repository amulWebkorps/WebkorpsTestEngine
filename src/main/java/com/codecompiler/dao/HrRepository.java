package com.codecompiler.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.codecompiler.entity.HrDetails;

public interface HrRepository extends  MongoRepository<HrDetails,Integer>{
}
