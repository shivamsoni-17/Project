package com.hmsService.repository;

import com.hmsService.model.Reservation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class ReservationRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Reservation> mapper = (rs, rowNum) -> {
        Reservation reservation = new Reservation();
        reservation.setId(rs.getLong("reservationid"));
        reservation.setUserId(rs.getLong("userid"));
        reservation.setRoomNo(rs.getInt("roomno"));
        reservation.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
        reservation.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
        reservation.setStatus(Reservation.Status.valueOf(rs.getString("status").toUpperCase()));
        if (rs.getDate("upcoming") != null) {
            reservation.setUpcoming(rs.getDate("upcoming").toLocalDate());
        }
        return reservation;
    };

    public ReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Reservation save(Reservation reservation) {
        if (reservation.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement("""
                        insert into reservations (userid, roomno, check_in_date, check_out_date, status, upcoming)
                        values (?, ?, ?, ?, ?, ?)
                        """, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, reservation.getUserId());
                ps.setInt(2, reservation.getRoomNo());
                ps.setDate(3, Date.valueOf(reservation.getCheckInDate()));
                ps.setDate(4, Date.valueOf(reservation.getCheckOutDate()));
                ps.setString(5, reservation.getStatus().name());
                if (reservation.getUpcoming() != null) {
                    ps.setDate(6, Date.valueOf(reservation.getUpcoming()));
                } else {
                    ps.setNull(6, java.sql.Types.DATE);
                }
                return ps;
            }, keyHolder);
            if (keyHolder.getKey() != null) {
                reservation.setId(keyHolder.getKey().longValue());
            }
        } else {
            jdbcTemplate.update("""
                    update reservations
                    set userid = ?, roomno = ?, check_in_date = ?, check_out_date = ?, status = ?, upcoming = ?
                    where reservationid = ?
                    """,
                    reservation.getUserId(),
                    reservation.getRoomNo(),
                    Date.valueOf(reservation.getCheckInDate()),
                    Date.valueOf(reservation.getCheckOutDate()),
                    reservation.getStatus().name(),
                    reservation.getUpcoming() != null ? Date.valueOf(reservation.getUpcoming()) : null,
                    reservation.getId());
        }
        return reservation;
    }

    public Optional<Reservation> findById(Long id) {
        List<Reservation> reservations = jdbcTemplate.query(
                "select * from reservations where reservationid = ?",
                mapper,
                id);
        return reservations.stream().findFirst();
    }

    public List<Reservation> findByUser(Long userId) {
        return jdbcTemplate.query(
                "select * from reservations where userid = ?",
                mapper,
                userId);
    }

    public List<Reservation> findAll() {
        return jdbcTemplate.query("select * from reservations order by reservationid desc", mapper);
    }

    public boolean existsForRoomAfter(Integer roomNo, Reservation.Status status, LocalDate checkInDate) {
        Integer count = jdbcTemplate.queryForObject("""
                select count(1) from reservations
                where roomno = ?
                  and status = ?
                  and check_out_date > ?
                """,
                Integer.class,
                roomNo,
                status.name(),
                Date.valueOf(checkInDate));
        return count != null && count > 0;
    }
}
