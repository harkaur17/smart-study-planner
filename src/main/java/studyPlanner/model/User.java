package studyPlanner.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column
    private String profilePicture; //todo

    @Column
    private String school;

    @Column
    private String program;

    @Column
    private String yearLevel;

    @Column(nullable = false)
    private int streakCount = 0;

    @Column
    private LocalDate lastActiveDate;

    @Column(nullable = false)
    private boolean isPublic = true;

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getSchool() {
        return school;
    }

    public String getProgram() {
        return program;
    }

    public String getYearLevel() {
        return yearLevel;
    }

    public int getStreakCount() {
        return streakCount;
    }

    public LocalDate getLastActiveDate() {
        return lastActiveDate;
    }

    public boolean isPublic() {
        return isPublic;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProfilePicture(String p) {
        this.profilePicture = p;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public void setYearLevel(String yearLevel) {
        this.yearLevel = yearLevel;
    }

    public void setStreakCount(int streakCount) {
        this.streakCount = streakCount;
    }

    public void setLastActiveDate(LocalDate date) {
        this.lastActiveDate = date;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
}