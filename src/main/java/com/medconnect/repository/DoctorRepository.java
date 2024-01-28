package com.medconnect.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.medconnect.entity.Doctor;

public interface DoctorRepository extends CrudRepository<Doctor, Long> {

	@Query("SELECT d FROM Doctor d WHERE d.Specialization = ?1")
    List<Doctor> findBySpecialization(String specialization);
	
	@Query("SELECT d FROM Doctor d WHERE d.Email = ?1 and d.Password = ?2")
	Doctor findByEmailPassword(String email, String password);
	
}