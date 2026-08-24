package studyPlanner.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class CourseBlock {

    private Long courseId;
    private String courseCode;
    private int sessionCount;

    public CourseBlock() {
    }

    public CourseBlock(Long courseId, String courseCode, int sessionCount) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.sessionCount = sessionCount;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public int getSessionCount() {
        return sessionCount;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public void setSessionCount(int sessionCount) {
        this.sessionCount = sessionCount;
    }
}
