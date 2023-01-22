package gutsandgun.kite_result.entity.read;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@Where(clause = "is_deleted = false")
@SQLDelete(sql= "UPDATE address_email SET is_deleted=true WHERE id = ?")
public class AddressEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 주소록 ID
     */
    @Column(name = "fk_user_address_id")
    @Comment("주소록 ID")
    private Long userAddressId;

    /**
     * 이메일
     */
    @Comment("이메일")
    private String email;

    private Boolean isDeleted = false;
}
