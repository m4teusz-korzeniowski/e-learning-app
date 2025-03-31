package korzeniowski.mateusz.app.model.user.dto;


import java.util.List;

public class UserGroupEnrollmentDto {
    private Long groupId;
    private String groupName;
    private List<String> userEmails;


    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getUserEmails() {
        return userEmails;
    }

    public void setUserEmails(List<String> userEmails) {
        this.userEmails = userEmails;
    }
}
