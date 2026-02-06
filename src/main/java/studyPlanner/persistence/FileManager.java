package studyPlanner.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import studyPlanner.model.*;
import studyPlanner.model.Task.Status;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class FileManager {

	//Declare Gson as static field
	private static final Gson gson;
	// Static initializer block - runs once when class is loaded
	static {
		GsonBuilder builder = new GsonBuilder();
		gson = builder.create();
	}
	
	private static CourseDTO convertCourseToDTO(Course course) {
		CourseDTO c = new CourseDTO();
		c.setName(course.getName());
		c.setCode(course.getCode());

		List<String> tNames = new ArrayList<String>();
		//		for(int i = 0; i<course.getTasks().size(); i++) {
		//			tNames.add(course.getTasks().get(i).getTaskName());
		//		}
		for(Task task : course.getTasks()) {
			tNames.add(task.getTaskName());
		}
		c.setTaskNames(tNames);
		return c;
	}

	private static TaskDTO convertTaskToDTO(Task task) {
		TaskDTO t = new TaskDTO();
		t.setTaskName(task.getTaskName());
		t.setTaskType(task.getTaskType());

		List<String> cNames = new ArrayList<String>();
		for(Course course : task.getCourses()) {
			cNames.add(course.getCode());
		}
		t.setCourseCodes(cNames);

		Status status = task.getTaskStatus();
		t.setStatus(status.name());

		LocalDate date = task.getDueDate();
		if(date != null) {
			t.setDueDate(date.toString());
		} else {
			t.setDueDate(null);
		}
		return t;
	}

	private static StudyPlannerDTO convertToDTO(StudyPlanner planner) {
		StudyPlannerDTO data = new StudyPlannerDTO();

		for(Course course: planner.getAllCourses()) {
			CourseDTO  courseDTO = convertCourseToDTO(course);
			data.addCourse(courseDTO);
		}

		for (Task task : planner.getAllTasks()) {
			TaskDTO taskDTO = convertTaskToDTO(task);
			data.addTask(taskDTO);
		}
		return data;
	}

	private static StudyPlanner convertFromDTO(StudyPlannerDTO data) {
		StudyPlanner planner = new StudyPlanner();
		Map<String, Course> courseMap = new HashMap<>();
		for(CourseDTO courseDTO: data.getCourses()) {
			try {
				Course course = new Course(courseDTO.getName(), courseDTO.getCode());
				planner.addCourse(course);
				courseMap.put(courseDTO.getCode(), course);
			} catch (IllegalArgumentException e) {
				System.err.println("Warning: Could not load course " + courseDTO.getCode());
			}
		}

		Map<String, Task> taskMap = new HashMap<>();
		for(TaskDTO taskDTO : data.getTasks()) {
			try {
				LocalDate dueDate = null;
				if(taskDTO.getDueDate() != null && ! taskDTO.getDueDate().isEmpty()) {
					dueDate = LocalDate.parse(taskDTO.getDueDate());
				}

				Status status = Status.valueOf(taskDTO.getStatus());

				Task task;
				if (dueDate != null) {
					task = new Task(taskDTO.getTaskName(), taskDTO.getTaskType(), dueDate, status);
				} else {
					task = new Task(taskDTO.getTaskName(), taskDTO.getTaskType(), status);
				}
				planner.addTask(task);
				taskMap.put(taskDTO.getTaskName(), task);
			} catch (IllegalArgumentException e) {
				System.err.println("Warning: Could not load task " + taskDTO.getTaskName());
			}
		}
		
		//Link Tasks and Courses
		for(TaskDTO taskDTO : data.getTasks()) {
			Task task = taskMap.get(taskDTO.getTaskName());
			if(task != null) {
				for(String courseCode : taskDTO.getCourseCodes()) {
					Course course = courseMap.get(courseCode);
					if(course != null) {
						task.addCourse(course);
					} else {
						System.err.println("Warning: Course " + courseCode + " not found for task " + taskDTO.getTaskName());
					}
				}
			}
		}
		return planner;
	}
	
	public static void saveData(StudyPlanner planner, String filePath) {
		try {
			StudyPlannerDTO data = convertToDTO(planner);
			
			//convert StudyPlannerDTO to JSON String
			String json = gson.toJson(data);
			//write JSON to file
			try (FileWriter writer = new FileWriter(filePath)){
				writer.write(json);
			}
			//System.out.println("Data saved successfully to: " + filePath);
		} catch (Exception e) {
			System.err.println("Error saving data: " +  e.getMessage());
			e.printStackTrace();  //helps with debugging
		}
	}
	
	public static StudyPlanner loadData(String filePath) {
		File file = new File(filePath);
		if(!file.exists()) {
			System.out.println("No saved data found. Starting with empty planner.");
			return new StudyPlanner();
		}
		
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))){
			StringBuilder jsonBuilder = new StringBuilder();
			String line;
			while((line = reader.readLine()) != null) {
				jsonBuilder.append(line);
			}
			String json = jsonBuilder.toString();
			
			//Convert JSON String to StudyPlannerDTO using gson
			StudyPlannerDTO data = gson.fromJson(json, StudyPlannerDTO.class);
			
			//Convert DTO to read StudyPLanner object
			StudyPlanner planner = convertFromDTO(data);
			
			System.out.println("Data loaded successfully from: " + filePath);
			return planner;
			
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + filePath);
			return new StudyPlanner();
		} catch (IOException e) {
			System.err.println("Error reading file: " + e.getMessage());
			return new StudyPlanner();
		} catch (Exception e) {
			System.err.println("Error loading data: " + e.getMessage());
			e.printStackTrace();
			return new StudyPlanner();
		}
	}
}
