package gutsandgun.kite_result.entity.read;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

/**
 * 발신 e-mail 저장 테이블
 */
@Entity
@Getter
@Setter
@Where(clause = "is_deleted = false")
@SQLDelete(sql= "UPDATE user_email SET is_deleted=true WHERE id = ?")
@Table(name="user_email")
public class UserEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fk_user_id")
    @Comment("user id")
    private String userId;

    @Comment("발신 이름")
    private String name;

    @Comment("발신 email")
    private String email;

    @ColumnDefault("false")
    private Boolean isDeleted = false;

    //audit
    @Comment("생성일자")
    @CreatedDate
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "reg_dt", nullable = false)
    private LocalDateTime regDt;

    @Comment("수정일자")
    @LastModifiedDate
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "mod_dt")
    private LocalDateTime modDt;

    @Comment("생성자")
    @Column(name = "reg_id", nullable = false, length = 20)
    private String regId;

    @Comment("수정자")
    @Column(name = "mod_id", length = 20)
    private String ModId;
}
