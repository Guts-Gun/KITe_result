package gutsandgun.kite_result.entity.read;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@Where(clause = "is_deleted = false")
@SQLDelete(sql= "UPDATE user SET is_deleted=true WHERE id = ?")
public class User {

    /**
     * String user id generate from keycloak
     */
    @Id
    private String id;

    /**
     * user 이름
     */
    @Comment("")
    private String name;

    /**
     * user email
     */
    @Comment("e-mail")
    private String email;

    private Boolean isDeleted = false;
}
