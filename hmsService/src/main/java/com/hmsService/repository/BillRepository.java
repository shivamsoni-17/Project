package com.hmsService.repository;

import com.hmsService.model.Bill;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class BillRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Bill> mapper = (rs, rowNum) -> Bill.builder()
            .id(rs.getLong("billid"))
            .reservationId(rs.getLong("reservationid"))
            .amount(rs.getBigDecimal("amount"))
            .addCharges(rs.getBigDecimal("addcharges"))
            .payStatus(Bill.PayStatus.valueOf(rs.getString("paystatus").toUpperCase()))
            .transactionId(rs.getObject("transactionid") != null ? rs.getLong("transactionid") : null)
            .createdAt(rs.getTimestamp("createdat") != null ? rs.getTimestamp("createdat").toLocalDateTime() : null)
            .build();

    public BillRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Bill> findById(Long id) {
        List<Bill> list = jdbcTemplate.query("select * from bills where billid = ?", mapper, id);
        return list.stream().findFirst();
    }

    public Optional<Bill> findByReservationId(Long reservationId) {
        List<Bill> list = jdbcTemplate.query("select * from bills where reservationid = ?", mapper, reservationId);
        return list.stream().findFirst();
    }

    public Bill save(Bill bill) {
        if (bill.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement("""
                        insert into bills (reservationid, amount, addcharges, paystatus, transactionid, createdat)
                        values (?, ?, ?, ?, ?, ?)
                        """, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, bill.getReservationId());
                ps.setBigDecimal(2, bill.getAmount());
                ps.setBigDecimal(3, bill.getAddCharges());
                ps.setString(4, bill.getPayStatus().name());
                if (bill.getTransactionId() != null) {
                    ps.setLong(5, bill.getTransactionId());
                } else {
                    ps.setNull(5, java.sql.Types.INTEGER);
                }
                ps.setTimestamp(6, bill.getCreatedAt() != null ? Timestamp.valueOf(bill.getCreatedAt()) : null);
                return ps;
            }, keyHolder);
            if (keyHolder.getKey() != null) {
                bill.setId(keyHolder.getKey().longValue());
            }
        } else {
            jdbcTemplate.update("""
                    update bills
                    set amount = ?, addcharges = ?, paystatus = ?, transactionid = ?
                    where billid = ?
                    """,
                    bill.getAmount(),
                    bill.getAddCharges(),
                    bill.getPayStatus().name(),
                    bill.getTransactionId(),
                    bill.getId());
        }
        return bill;
    }

    public List<Bill> findByUserId(Long userId) {
        return jdbcTemplate.query("""
                select b.* from bills b
                join reservations r on r.reservationid = b.reservationid
                where r.userid = ?
                order by b.createdat desc
                """, mapper, userId);
    }

    public List<Bill> findAll() {
        return jdbcTemplate.query("select * from bills order by billid desc", mapper);
    }
}
