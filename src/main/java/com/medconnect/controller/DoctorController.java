package com.medconnect.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.medconnect.entity.Appointment;
import com.medconnect.entity.Doctor;
import com.medconnect.entity.Users;
import com.medconnect.repository.AppointmentRepository;
import com.medconnect.repository.DoctorRepository;
import com.medconnect.util.Constants;

@Controller
public class DoctorController {
	
	@Autowired
	private DoctorRepository doctorRepository;
	
	@Autowired
	private AppointmentRepository appointmentRepository;

	@RequestMapping(value = "/doctorLogin", method = RequestMethod.POST)
	public String doctorLogin(@ModelAttribute("user") Users authenticateUser, ModelMap model) {
		boolean isAuthenticated = false;
		try {
			if (authenticateUser != null) {
				Doctor doctor = doctorRepository.findByEmailPassword(authenticateUser.getEmail(),
						authenticateUser.getPassword());
				if (doctor != null) {
					Constants.DOCTOR = doctor.getName();
					isAuthenticated = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (isAuthenticated) {
			
			return "redirect:/doctorDashboard";
		} else {
			model.addAttribute("loginErrorMessage", "Invalid Credentials, please try again !");
			
			model.addAttribute("appointmentDetails", new Appointment());
			
			return "index";
		}
	}
	
	@GetMapping("/doctorDashboard")
	public String doctorDashboard(ModelMap model) {
		
		List<Appointment> appointmentsList = (List<Appointment>) appointmentRepository.findByDoctorId(Constants.DOCTOR);
		
		model.addAttribute("appointmentsList", appointmentsList);
		
		return "doctorDashboard";
	}

}
