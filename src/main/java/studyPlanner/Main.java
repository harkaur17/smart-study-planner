package studyPlanner;

import studyPlanner.service.StudyPlanner; 

public class Main {

	public static void main(String[] args) {
		System.out.println("Starting Smart Study Planner...");

		StudyPlanner planner = new StudyPlanner();

		// Start the program
		planner.run();

		System.out.println("Program ended.");

	}

}
