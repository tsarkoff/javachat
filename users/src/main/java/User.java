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
        return false;
    }

    @Override
    public String getNickName() {
        return null;
    }

    @Override
    public boolean setPermissions(UserPermissions userPermission) {
        return false;
    }

    @Override
    public UserPermissions getPermissions() {
        return null;
    }

    @Override
    public boolean setRating(int rating) {
        return false;
    }

    @Override
    public int getRating() {
        return 0;
    }

    @Override
    public boolean setFriends(Set<User> friends) {
        return false;
    }

    @Override
    public Set<User> getFriends() {
        return null;
    }

    @Override
    public boolean setRegistrationDateTime(LocalDateTime dt) {
        return false;
    }

    @Override
    public LocalDateTime getRegistrationDateTime() {
        return null;
    }
}
