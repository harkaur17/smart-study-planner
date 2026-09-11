package studyPlanner.dto;

import studyPlanner.model.Badge;
import java.util.List;

public class BuddyProfileDTO {
    public Long id;
    public String username;
    public String name;
    public boolean isPublic;
    public String relationshipStatus;
    public boolean visible;
    public Integer streakCount;
    public Integer xpTotal;
    public List<Badge> badges;
}
