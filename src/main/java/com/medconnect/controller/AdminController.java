package com.medconnect.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.medconnect.entity.Appointment;
import com.medconnect.entity.Doctor;
import com.medconnect.entity.Services;
import com.medconnect.entity.Users;
import com.medconnect.repository.AppointmentRepository;
import com.medconnect.repository.DoctorRepository;
import com.medconnect.repository.ServicesRepository;
import com.medconnect.repository.UsersRepository;
import com.medconnect.util.Constants;

@Controller
public class AdminController {

	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private ServicesRepository servicesRepository;
	
	@Autowired
	private DoctorRepository doctorRepository;
	
	@Autowired
	private AppointmentRepository appointmentRepository;

	@RequestMapping(value = "/adminLogin", method = RequestMethod.POST)
	public String adminLogin(@ModelAttribute("user") Users authenticateUser, ModelMap model) {
		boolean isAuthenticated = false;
		try {
			if (authenticateUser != null) {
				Users user = usersRepository.findUserByEmailPassword(authenticateUser.getEmail(),
						authenticateUser.getPassword());
				if (user != null) {
					isAuthenticated = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (isAuthenticated) {
			return "redirect:/adminDashboard";
		} else {
			model.addAttribute("loginErrorMessage", "Invalid Credentials, please try again !");
			
			model.addAttribute("appointmentDetails", new Appointment());
			
			return "index";
		}
	}

	@GetMapping("/adminDashboard")
	public String adminDashboard(ModelMap model) {
		
		List<Doctor> doctorsList = (List<Doctor>) doctorRepository.findAll();
		List<Services> servicesList = (List<Services>) servicesRepository.findAll();
		List<Appointment> appointmentsList = (List<Appointment>) appointmentRepository.findAll();
		
		model.addAttribute("doctorsList", doctorsList.size());
		model.addAttribute("servicesList", servicesList.size());
		model.addAttribute("appointmentsList", appointmentsList.size());
		
		return "adminDashboard";
	}

	@GetMapping("/services")
	public String services(ModelMap model) {
		List<Services> servicesList = (List<Services>) servicesRepository.findAll();
		String base64Data = null;
		for(Services service : servicesList) {
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

		model.addAttribute("serviceDetails", new Services());
		model.addAttribute("servicesList", servicesList);
		return "services";
	}

	@RequestMapping(value = "/addService", method = RequestMethod.POST)
	public String addService(@RequestParam("photoFile") MultipartFile photoFile,
			@ModelAttribute("serviceDetails") Services serviceDetails) {

		try {

			File dir = new File(Constants.SERVICE_IMAGES_PATH);
			if (!dir.exists()) {
				dir.mkdir();
			}

			String pfid = UUID.randomUUID().toString() + "_" + photoFile.getOriginalFilename();
			File outputFile = new File(dir.getAbsolutePath() + File.separator + pfid);
			InputStream fis = photoFile.getInputStream();
			FileOutputStream fos = new FileOutputStream(outputFile);
			int c = 0;
			while ((c = fis.read()) != -1) {
				fos.write(c);
			}
			fis.close();
			fos.close();

			serviceDetails.setPath(pfid);

			servicesRepository.save(serviceDetails);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "redirect:/services";

	}

	@GetMapping("/doctors")
	public String doctors(ModelMap model) {
		model.addAttribute("doctorDetails", new Doctor());
		model.addAttribute("servicesList", servicesRepository.findAll());
		
		List<Doctor> doctorsList = (List<Doctor>) doctorRepository.findAll();
		String base64Data = null;
		for(Doctor doctor : doctorsList) {
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
		
		return "doctors";
	}
	
	@RequestMapping(value = "/addDoctor", method = RequestMethod.POST)
	public String addDoctor(@RequestParam("photoFile") MultipartFile photoFile,
			@ModelAttribute("doctorDetails") Doctor doctorDetails) {

		try {

			File dir = new File(Constants.DOCTOR_IMAGES_PATH);
			if (!dir.exists()) {
				dir.mkdir();
			}

			String pfid = UUID.randomUUID().toString() + "_" + photoFile.getOriginalFilename();
			File outputFile = new File(dir.getAbsolutePath() + File.separator + pfid);
			InputStream fis = photoFile.getInputStream();
			FileOutputStream fos = new FileOutputStream(outputFile);
			int c = 0;
			while ((c = fis.read()) != -1) {
				fos.write(c);
			}
			fis.close();
			fos.close();

			doctorDetails.setPath(pfid);
			doctorDetails.setPassword("doctor@2023");

			doctorRepository.save(doctorDetails);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "redirect:/doctors";

	}

	@GetMapping("/appointments")
	public String appointments(ModelMap model) {
		model.addAttribute("appointmentsList", appointmentRepository.findAll());
		return "appointments";
	}
}
