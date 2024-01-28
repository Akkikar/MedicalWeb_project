package com.medconnect.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.medconnect.entity.Appointment;

public interface AppointmentRepository extends CrudRepository<Appointment, Long> {

	@Query("SELECT a FROM Appointment a WHERE a.Doctor = ?1")
    List<Appointment> findByDoctorId(String doctor);
}