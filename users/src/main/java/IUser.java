import java.time.LocalDateTime;
import java.util.Set;

public interface IUser {
    boolean setNickName(String nickName);

    String getNickName();

    boolean setPermissions(UserPermissions userPermission);

    UserPermissions getPermissions();

    boolean setRating(int rating);

    int getRating();

    boolean setFriends(Set<User> friends);

    Set<User> getFriends();

    boolean setRegistrationDateTime(LocalDateTime dt);

    LocalDateTime getRegistrationDateTime();
}
