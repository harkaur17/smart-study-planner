package studyPlanner.model;

import jakarta.persistence.*;

@Entity
@Table(name = "grade_items")
public class GradeItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double weight;

    @Column
    private Double grade; 

    public GradeItem() {
    }  

    public GradeItem(Course course, User user, String name, double weight, Double grade){
        this.course = course;
        this.user = user;
        this.weight = weight;
        this.grade = grade;
    }

    public Long getId() { return id; }
    public Course getCourse() { return course; }
    public User getUser() { return user; }
    public String getName() { return name; }
    public double getWeight() { return weight; }
    public Double getGrade() { return grade; }

    public void setId(Long id) { this.id = id; }
    public void setCourse(Course course) { this.course = course; }
    public void setUser(User user) { this.user = user; }
    public void setName(String name) { this.name = name; }
    public void setWeight(double weight) { this.weight = weight; }
    public void setGrade(Double grade) { this.grade = grade; }


}
