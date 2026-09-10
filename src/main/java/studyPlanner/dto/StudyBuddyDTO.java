package studyPlanner.dto;

public class StudyBuddyDTO {
    public Long id;
    public String username;
    public String name;
    public String relationshipStatus;
    public Long connectionId;

    public StudyBuddyDTO(Long id, String username, String name, String relationshipStatus) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.relationshipStatus = relationshipStatus;
    }
}
