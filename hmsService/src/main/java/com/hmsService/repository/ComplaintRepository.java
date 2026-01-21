package com.hmsService.repository;

import com.hmsService.model.Complaint;
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
public class ComplaintRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Complaint> mapper = (rs, rowNum) -> {
        Complaint complaint = new Complaint();
        complaint.setId(rs.getLong("complaintid"));
        complaint.setUserId(rs.getLong("userid"));
        complaint.setContact(rs.getString("contact"));
        if (rs.getObject("roomno") != null) {
            complaint.setRoomNo(rs.getInt("roomno"));
        }
        complaint.setCategory(rs.getString("category"));
        complaint.setDescription(rs.getString("description"));
        complaint.setStatus(Complaint.Status.valueOf(rs.getString("status").toUpperCase().replace(" ", "_")));
        if (rs.getDate("createdat") != null) {
            complaint.setCreatedAt(rs.getDate("createdat").toLocalDate());
        }
        if (rs.getDate("resolvedat") != null) {
            complaint.setResolvedAt(rs.getDate("resolvedat").toLocalDate());
        }
        return complaint;
    };

    public ComplaintRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Complaint save(Complaint complaint) {
        if (complaint.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement("""
                        insert into complaints (userid, contact, roomno, category, description, status, createdat, resolvedat)
                        values (?, ?, ?, ?, ?, ?, ?, ?)
                        """, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, complaint.getUserId());
                ps.setString(2, complaint.getContact());
                if (complaint.getRoomNo() != null) {
                    ps.setInt(3, complaint.getRoomNo());
                } else {
                    ps.setNull(3, java.sql.Types.INTEGER);
                }
                ps.setString(4, complaint.getCategory());
                ps.setString(5, complaint.getDescription());
                ps.setString(6, complaint.getStatus().name());
                ps.setDate(7, complaint.getCreatedAt() != null ? Date.valueOf(complaint.getCreatedAt()) : null);
                ps.setDate(8, complaint.getResolvedAt() != null ? Date.valueOf(complaint.getResolvedAt()) : null);
                return ps;
            }, keyHolder);
            if (keyHolder.getKey() != null) {
                complaint.setId(keyHolder.getKey().longValue());
            }
        } else {
            jdbcTemplate.update("""
                    update complaints
                    set userid = ?, contact = ?, roomno = ?, category = ?, description = ?, status = ?, createdat = ?, resolvedat = ?
                    where complaintid = ?
                    """,
                    complaint.getUserId(),
                    complaint.getContact(),
                    complaint.getRoomNo(),
                    complaint.getCategory(),
                    complaint.getDescription(),
                    complaint.getStatus().name(),
                    complaint.getCreatedAt() != null ? Date.valueOf(complaint.getCreatedAt()) : null,
                    complaint.getResolvedAt() != null ? Date.valueOf(complaint.getResolvedAt()) : null,
                    complaint.getId());
        }
        return complaint;
    }

    public List<Complaint> findByUser(Long userId) {
        return jdbcTemplate.query("select * from complaints where userid = ?", mapper, userId);
    }

    public Optional<Complaint> findById(Long id) {
        List<Complaint> complaints = jdbcTemplate.query(
                "select * from complaints where complaintid = ?",
                mapper,
                id);
        return complaints.stream().findFirst();
    }

    public List<Complaint> findAll() {
        return jdbcTemplate.query("select * from complaints order by complaintid desc", mapper);
    }
}
