package com.hmsService.repository;

import com.hmsService.model.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class TransactionRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Transaction> mapper = (rs, rowNum) -> Transaction.builder()
            .id(rs.getLong("transactionid"))
            .payDate(rs.getDate("paydate") != null ? rs.getDate("paydate").toLocalDate() : null)
            .status(Transaction.Status.valueOf(rs.getString("status").toUpperCase()))
            .mode(Transaction.Mode.valueOf(rs.getString("mode").toUpperCase()))
            .amount(rs.getBigDecimal("amount"))
            .build();

    public TransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Transaction save(Transaction tx) {
        if (tx.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement("""
                        insert into transactions (paydate, status, mode, amount)
                        values (?, ?, ?, ?)
                        """, Statement.RETURN_GENERATED_KEYS);
                ps.setDate(1, tx.getPayDate() != null ? Date.valueOf(tx.getPayDate()) : null);
                ps.setString(2, tx.getStatus().name());
                ps.setString(3, tx.getMode().name());
                ps.setBigDecimal(4, tx.getAmount());
                return ps;
            }, keyHolder);
            if (keyHolder.getKey() != null) {
                tx.setId(keyHolder.getKey().longValue());
            }
        } else {
            jdbcTemplate.update("""
                    update transactions
                    set paydate = ?, status = ?, mode = ?, amount = ?
                    where transactionid = ?
                    """,
                    tx.getPayDate() != null ? Date.valueOf(tx.getPayDate()) : null,
                    tx.getStatus().name(),
                    tx.getMode().name(),
                    tx.getAmount(),
                    tx.getId());
        }
        return tx;
    }

    public Optional<Transaction> findById(Long id) {
        List<Transaction> list = jdbcTemplate.query("select * from transactions where transactionid = ?", mapper, id);
        return list.stream().findFirst();
    }
}
