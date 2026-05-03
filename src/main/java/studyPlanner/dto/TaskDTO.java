package studyPlanner.dto;

import java.time.LocalDate;
import java.util.List;

public class TaskDTO {
    public Long id;
    public String taskName;
    public String taskType;
    public LocalDate dueDate;
    public String taskStatus;
    public String priority;
    public List<CourseInfo> courses;

    public static class CourseInfo {
        public Long id;
        public String code;
        public String name;

        public CourseInfo(Long id, String code, String name) {
            this.id = id;
            this.code = code;
            this.name = name;
        }
    }
}