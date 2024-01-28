package com.medconnect.controller;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.medconnect.entity.Appointment;
import com.medconnect.entity.Doctor;
import com.medconnect.entity.Services;
import com.medconnect.entity.Users;
import com.medconnect.repository.AppointmentRepository;
import com.medconnect.repository.DoctorRepository;
import com.medconnect.repository.ServicesRepository;
import com.medconnect.util.Constants;

@Controller
public class IndexController {

	@Autowired
	private ServicesRepository servicesRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@GetMapping("/")
	public String index(ModelMap model) {
		model.addAttribute("user", new Users());

		List<Services> servicesList = (List<Services>) servicesRepository.findAll();
		String base64Data = null;
		for (Services service : servicesList) {
			File file = new File(Constants.SERVICE_IMAGES_PATH + File.separator + service.getPath());
			if (file.exists()) {
				byte[] fileContent;
				try {
					fileContent = FileUtils.readFileToByteArray(new File(file.getAbsolutePath()));
					base64Data = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(fileContent);
					service.setFileData(base64Data);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		model.addAttribute("servicesList", servicesList);

		base64Data = null;
		List<Doctor> doctorsList = (List<Doctor>) doctorRepository.findAll();
		for (Doctor doctor : doctorsList) {
			File file = new File(Constants.DOCTOR_IMAGES_PATH + File.separator + doctor.getPath());
			if (file.exists()) {
				byte[] fileContent;
				try {
					fileContent = FileUtils.readFileToByteArray(new File(file.getAbsolutePath()));
					base64Data = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(fileContent);
					doctor.setFileData(base64Data);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		model.addAttribute("doctorsList", doctorsList);

		model.addAttribute("appointmentDetails", new Appointment());

		return "index";
	}

	@GetMapping("/error")
	public String error(ModelMap model) {

		return "error";
	}

	@GetMapping("/signout")
	public String signout(/* HttpServletRequest request */) {
//		try {
//			HttpSession session = request.getSession(false);
//			if (session != null) {
//				session.invalidate();
//			}
//
//			for (Cookie cookie : request.getCookies()) {
//				cookie.setMaxAge(0);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return "redirect:/";
	}

	@ResponseBody
	@GetMapping("/doctorsBySpecilization")
	public List<Doctor> doctorsBySpecilization(@RequestParam("specilization") String specilization) {
		return doctorRepository.findBySpecialization(specilization);
	}

	@RequestMapping(value = "/addAppointment", method = RequestMethod.POST)
	public String addAppointment(@ModelAttribute("appointmentDetails") Appointment appointmentDetails, ModelMap model) {
		appointmentRepository.save(appointmentDetails);
		model.addAttribute("appointmentMessage", "Appointment created successfully.");
		return "redirect:/";

	}
}
