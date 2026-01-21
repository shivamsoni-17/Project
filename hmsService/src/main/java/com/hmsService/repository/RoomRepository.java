package com.hmsService.repository;

import com.hmsService.model.Room;
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
public class RoomRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Room> mapper = (rs, rowNum) -> {
        Room room = new Room();
        room.setRoomNo(rs.getInt("roomno"));
        room.setStatus(Room.Status.valueOf(rs.getString("status").toUpperCase()));
        if (rs.getDate("availabilitydate") != null) {
            room.setAvailabilityDate(rs.getDate("availabilitydate").toLocalDate());
        }
        room.setType(Room.Type.valueOf(rs.getString("type").toUpperCase()));
        room.setPrice(rs.getBigDecimal("price"));
        return room;
    };

    public RoomRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long count() {
        Long count = jdbcTemplate.queryForObject("select count(1) from rooms", Long.class);
        return count != null ? count : 0;
    }

    public List<Room> findByStatus(Room.Status status) {
        return jdbcTemplate.query(
                "select * from rooms where status = ?",
                mapper,
                status.name());
    }

    public List<Room> searchAvailable(LocalDate checkIn, Room.Type type, Room.Status status) {
        StringBuilder sql = new StringBuilder("""
                select * from rooms
                where status = ?
                  and (availabilitydate is null or availabilitydate <= ?)
                """);
        Object[] params;
        if (type != null) {
            sql.append(" and type = ?");
            params = new Object[]{status.name(), Date.valueOf(checkIn), type.name()};
        } else {
            params = new Object[]{status.name(), Date.valueOf(checkIn)};
        }
        return jdbcTemplate.query(sql.toString(), mapper, params);
    }

    public Optional<Room> findById(Integer roomNo) {
        List<Room> rooms = jdbcTemplate.query(
                "select * from rooms where roomno = ?",
                mapper,
                roomNo);
        return rooms.stream().findFirst();
    }

    public List<Room> findAll() {
        return jdbcTemplate.query("select * from rooms order by roomno", mapper);
    }

    public Room save(Room room) {
        // If room exists, update; else insert
        boolean exists = findById(room.getRoomNo()).isPresent();
        if (exists) {
            jdbcTemplate.update("""
                    update rooms
                    set status = ?, availabilitydate = ?, type = ?, price = ?
                    where roomno = ?
                    """,
                    room.getStatus().name(),
                    room.getAvailabilityDate() != null ? Date.valueOf(room.getAvailabilityDate()) : null,
                    room.getType().name(),
                    room.getPrice(),
                    room.getRoomNo());
        } else {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement("""
                        insert into rooms (roomno, status, availabilitydate, type, price)
                        values (?, ?, ?, ?, ?)
                        """, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, room.getRoomNo());
                ps.setString(2, room.getStatus().name());
                if (room.getAvailabilityDate() != null) {
                    ps.setDate(3, Date.valueOf(room.getAvailabilityDate()));
                } else {
                    ps.setNull(3, java.sql.Types.DATE);
                }
                ps.setString(4, room.getType().name());
                ps.setBigDecimal(5, room.getPrice());
                return ps;
            }, keyHolder);
        }
        return room;
    }
}
