import java.time.LocalDateTime;
import java.util.Set;

public class User implements IUser {
    private String nickName;
    private LocalDateTime registrationDateTime;
    private int userRating;
    private UserPermissions userPermissions;
    private Set<User> friends;

    @Override
    public boolean setNickName(String nickName) {
        if (nickName.isEmpty())
            return false;
        this.nickName = nickName;
        return true;
    }

    @Override
    public String getNickName() {
        return nickName;
    }

    @Override
    public boolean setPermissions(UserPermissions userPermissions) {
        if (userPermissions == null)
            return false;
        this.userPermissions = userPermissions;
        return true;
    }

    @Override
    public UserPermissions getPermissions() {
        return userPermissions;
    }

    @Override
    public boolean setRating(int rating) {
        if (rating < 0 || rating > 10)
            return false;
        userRating = rating;
        return true;
    }

    @Override
    public int getRating() {
        return userRating;
    }

    @Override
    public boolean setFriends(Set<User> friends) {
        if (friends == null)
            return false;
        this.friends = friends;
        return true;
    }

    @Override
    public Set<User> getFriends() {
        return friends;
    }

    @Override
    public boolean setRegistrationDateTime(LocalDateTime dt) {
        if (dt == null)
            return false;
        registrationDateTime = dt;
        return true;
    }

    @Override
    public LocalDateTime getRegistrationDateTime() {
        return registrationDateTime;
    }
}
