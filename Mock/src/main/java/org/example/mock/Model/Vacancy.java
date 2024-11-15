package org.example.mock.Model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "vacancy")
public class Vacancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "JSON", nullable = false)
    private String details;

    @Column(nullable = false, length = 50)
    private String salary;

    @Column(nullable = false)
    private Integer count;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType type; // Enum for job type

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VacancyStatus status = VacancyStatus.ACTIVE; // Enum for vacancy status

    //ApproveStatus
    @Enumerated(EnumType.STRING)
    @Column(name = "approve_status", nullable = false)
    private ApproveStatus approveStatus = ApproveStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "reopen_at")
    private LocalDateTime reopenAt;

    @ManyToOne
    @JoinColumn(name = "created_user_id", nullable = false)
    private User createdUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_user_id")
    private User updatedUser;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false)
    private PositionAll position;

    public Vacancy(Long id, String details, String salary, Integer count, JobType type, LocalDate dueDate, VacancyStatus status,ApproveStatus approveStatus, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime reopenAt, User createdUser, User updatedUser, Department department, PositionAll position) {
        this.id = id;
        this.details = details;
        this.salary = salary;
        this.count = count;
        this.type = type;
        this.dueDate = dueDate;
        this.status = status;
        this.approveStatus = approveStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reopenAt = reopenAt;
        this.createdUser = createdUser;
        this.updatedUser = updatedUser;
        this.department = department;
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public JobType getType() {
        return type;
    }

    public void setType(JobType type) {
        this.type = type;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public VacancyStatus getStatus() {
        return status;
    }

    public void setStatus(VacancyStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getReopenAt() {
        return reopenAt;
    }

    public void setReopenAt(LocalDateTime reopenAt) {
        this.reopenAt = reopenAt;
    }

    public User getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(User createdUser) {
        this.createdUser = createdUser;
    }

    public User getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(User updatedUser) {
        this.updatedUser = updatedUser;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public PositionAll getPosition() {
        return position;
    }

    public void setPosition(PositionAll position) {
        this.position = position;
    }

    public ApproveStatus getApproveStatus() { return approveStatus;}

    public void setApproveStatus(ApproveStatus approveStatus) { this.approveStatus = approveStatus; }

    @Transient
    public String getRequiredSkills() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode detailsNode = mapper.readTree(this.details);
            JsonNode skillsNode = detailsNode.get("required_skills");

            if (skillsNode != null && skillsNode.isArray()) {
                // Chuyển đổi danh sách JSON thành chuỗi nối bởi dấu phẩy
                List<String> skills = new ArrayList<>();
                for (JsonNode skill : skillsNode) {
                    skills.add(skill.asText());
                }
                return String.join(", ", skills);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}



