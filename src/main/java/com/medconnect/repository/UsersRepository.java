package com.medconnect.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.medconnect.entity.Users;

public interface UsersRepository extends CrudRepository<Users, Long> {

	@Query("SELECT u FROM Users u WHERE u.Email = ?1 and u.Password = ?2")
    Users findUserByEmailPassword(String email, String password);
	
}