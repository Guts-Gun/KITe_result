package gutsandgun.kite_result.querydsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import gutsandgun.kite_result.dto.QResultSendingDto;
import gutsandgun.kite_result.dto.QResultTxSuccessDto;
import gutsandgun.kite_result.dto.ResultSendingDto;
import gutsandgun.kite_result.entity.read.*;
import gutsandgun.kite_result.type.SendingStatus;
import gutsandgun.kite_result.type.SendingType;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class ResultRepositoryCustom {


    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    QResultSending qResultSending = QResultSending.resultSending;
    QResultTx qResultTx = QResultTx.resultTx;
    QResultTxTransfer qResultTxTransfer = QResultTxTransfer.resultTxTransfer;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Page<ResultSendingDto> findByRegIdAndSendingTypeAndSuccessAndRegDt(
            String userId, SendingType sendingType, String startDt, String endDt, SendingStatus sendingStatus, Pageable pageable) throws ParseException {


        List<ResultSendingDto> list = queryFactory
                .select(new QResultSendingDto(
                        qResultSending.id,
                        qResultSending.userId,
                        qResultSending.sendingId,
                        qResultSending.sendingType,
                        qResultSending.sendingRuleType,
                        qResultSending.success,
                        qResultSending.totalMessage,
                        qResultSending.failedMessage,
                        qResultSending.avgLatency.longValue(),
                        qResultSending.inputTime,
                        qResultSending.scheduleTime,
                        qResultSending.startTime,
                        qResultSending.completeTime,
                        qResultSending.logTime,
                        qResultSending.sendingStatus,
                        new QResultTxSuccessDto( qResultSending.sendingId,
                            new CaseBuilder().when(qResultTx.success.eq(true)).then(new Long(1)).otherwise(new Long(0)).sum(),
                            new CaseBuilder().when(qResultTx.success.eq(false)).then(new Long(1)).otherwise(new Long(0)).sum()
                        )
                    )
                )
                .from(qResultSending)
                .leftJoin(qResultTx).on(qResultSending.sendingId.eq(qResultTx.resultSendingId))
                .leftJoin(qResultTxTransfer).on(qResultTx.txId.eq(qResultTxTransfer.txId).and(qResultTx.success.eq(qResultTxTransfer.success)))
                .where(
                        eqUserId(userId),
                        eqSendingType(sendingType)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .groupBy(qResultSending.sendingId)
                .orderBy(qResultSending.regDt.desc())
                .fetch();

        Long count = queryFactory
                .select(qResultSending.count())
                .from(qResultSending)
                .leftJoin(qResultTx).on(qResultSending.sendingId.eq(qResultTx.resultSendingId))
                .leftJoin(qResultTxTransfer).on(qResultTx.txId.eq(qResultTxTransfer.txId).and(qResultTx.success.eq(qResultTxTransfer.success)))
                .where(
                        eqUserId(userId),
                        eqSendingType(sendingType),
                        afterInputTime(startDt),
                        beforeInputTime(endDt),
                        eqsSendingStatus(sendingStatus)
                )
                .fetchOne();


        return  new PageImpl<>(list, pageable, count);
    }






    private BooleanExpression eqUserId(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        return qResultSending.userId.eq(userId);
    }

    private BooleanExpression eqSendingType(SendingType sendingType) {
        if (sendingType == null || StringUtils.isEmpty(sendingType)) {
            return null;
        }
        return qResultSending.sendingType.eq(sendingType);
    }

    private BooleanExpression afterInputTime(String startDt) throws ParseException {
        if (StringUtils.isEmpty(String.valueOf(startDt)) || startDt == null) {
            return null;
        }

        Date date = formatter.parse(startDt);
        return qResultSending.inputTime.goe(date.getTime());
    }

    private BooleanExpression beforeInputTime(String endDt) throws ParseException {
        if (StringUtils.isEmpty(String.valueOf(endDt))  || endDt == null) {
            return null;
        }
        Date date = formatter.parse(endDt);
        return qResultSending.inputTime.loe(date.getTime());
    }

    private BooleanExpression eqsSendingStatus(SendingStatus sendingStatus) {
        if (sendingStatus == null || StringUtils.isEmpty(sendingStatus)) {
            return null;
        }
        return qResultSending.sendingStatus.eq(sendingStatus);
    }

}
