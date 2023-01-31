package com.example.demo.dao;

import java.sql.Date;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.management.relation.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.example.demo.dtos.Constants;
import com.example.demo.dtos.LastSession;
import com.example.demo.dtos.Roles;
import com.example.demo.dtos.User;

@Repository
public class JdbcUmsRepository implements UmsRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Map<UUID, User> findAllUsers() {
        Map<UUID, User> users = new HashMap<>();

        List<Object> oUsers = jdbcTemplate.query(Constants.GET_ALL_USERS,
                (rs, rowNum) -> new User(DaoHelper.bytesArrayToUuid(rs.getBytes("users.id")),
                        rs.getString("users.name"),
                        rs.getString("users.email"), rs.getString("users.password"), rs.getInt("users.created"),
                        Arrays.asList(new Roles(DaoHelper.bytesArrayToUuid(rs.getBytes("roles.id")),
                                rs.getString("roles.name"), rs.getString("roles.description"))),
                        new LastSession(rs.getInt("last_visit.in"), rs.getInt("last_visit.out"))));

        for (Object oUser : oUsers) {
            if (!users.containsKey(((User) oUser).getId())) {
                User user = new User();
                user.setId(((User) oUser).getId());
                user.setName(((User) oUser).getName());
                user.setEmail(((User) oUser).getEmail());
                user.setPassword(((User) oUser).getPassword());
                user.setCreated(((User) oUser).getCreated());
                user.setLastSession(((User) oUser).getLastSession());
                users.put(((User) oUser).getId(), user);
            }
            users.get(((User) oUser).getId()).addRole(((User) oUser).getRoles().get(0));
        }
        return users;
    }

    @Override
    public User findUserByID(UUID userId) {
        User user = new User();
        List<Object> users = jdbcTemplate.query(Constants.GET_USER_BY_ID_FULL,
                (rs, rowNum) -> new User(DaoHelper.bytesArrayToUuid(rs.getBytes("users.id")),
                        rs.getString("users.name"),
                        rs.getString("users.email"), rs.getString("users.password"), rs.getInt("users.created"),
                        Arrays.asList(new Roles(DaoHelper.bytesArrayToUuid(rs.getBytes("roles.id")),
                                rs.getString("roles.name"), rs.getString("roles.description"))),
                        new LastSession(rs.getInt("last_visit.in"), rs.getInt("last_visit.out"))),
                userId.toString());
        for (Object oUser : users) {
            if (user.getId() == null) {
                user.setId(((User) oUser).getId());
                user.setName(((User) oUser).getName());
                user.setEmail(((User) oUser).getEmail());
                user.setPassword(((User) oUser).getPassword());
                user.setCreated(((User) oUser).getCreated());
                user.setLastSession(((User) oUser).getLastSession());
            }
            user.addRole(((User) oUser).getRoles().get(0));
        }
        return user;
    }

    @Override
    public UUID createUser(User user) {
        long timestamp = Instant.now().getEpochSecond();
        Map<String, Roles> roles = this.findAllRoles();
        UUID userId = UUID.randomUUID();

        try {
            jdbcTemplate.update(Constants.CREATE_USER, userId.toString(), user.getName(), user.getEmail(),
                    user.getPassword(), timestamp, null);
            for (Roles role : user.getRoles()) {
                jdbcTemplate.update(Constants.ASSIGN_ROLE, userId.toString(),
                        roles.get(role.getRole()).getRoleId().toString());
            }
        } catch (Exception e) {
            return null;
        }

        return userId;
    }

    @Override
    public int deleteUser(UUID userId) {
        return jdbcTemplate.update(Constants.DELETE_USER, userId.toString());
    }

    @Override
    public Map<String, Roles> findAllRoles() {
        Map<String, Roles> roles = new HashMap<>();
        jdbcTemplate.query(Constants.GET_ALL_ROLES, rs -> {
            Roles role = new Roles(DaoHelper.bytesArrayToUuid(rs.getBytes("roles.id")), rs.getString("roles.name"),
                    rs.getString("roles.description"));
            roles.put(rs.getString("roles.name"), role);
        });
        return roles;
    }

    @Override
    public Boolean TokenCheck(String token) {
        String GET_AUTH_BY_TOKEN = "SELECT * FROM AUTH WHERE TOKEN = " + token;
        return jdbcTemplate.query(GET_AUTH_BY_TOKEN, rs -> {
            Date date = rs.getDate("Date");
            Date now = new Date(Calendar.getInstance().getTime().getTime());
            Boolean worked = false;
            long between = now.getTime() - date.getTime();
            if (between <= 900000) {
                worked = true;
            }
            if (worked == true) {
                return true;

            } else {
                return false;
            }
        });
    }

    @Override
    public Map<Integer, String> getProducerId(String subscriberID) {
        String query = "SELECT * FROM SUBSCRIPTION WHERE SUBSCRIBERID = " + subscriberID;
        Map<Integer, String> producers = new HashMap<>();

        String ambiguos = jdbcTemplate.query(query, rs -> {
            for (int i = 0; rs.next(); i++) {
                String producerID = rs.getString("PRODUCERID");
                producers.put(i, producerID);
            }
            return "done";
        });
        return producers;
    }

    @Override
    public Date getLastLogout(String token) {
        String query = "SELECT LOGOUTDATE FROM AUTH WHERE TOKEN = " + token;
        Date LOGOUTDATE = jdbcTemplate.query(query, rs -> {
            Date LogOutDate = null;
            try {
                LogOutDate = rs.getDate("LOGOUTDATE");
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
            return LogOutDate;
        });
        return LOGOUTDATE;
    }

    @Override
    public Boolean setLogout(String token) {
        java.util.Date date = new java.util.Date();
        Date formattedDate = new java.sql.Date(date.getTime());
        String query = "UPDATE AUTH SET LOGOUTDATE = '" + formattedDate + "' WHERE TOKEN = " + token;

        int worked = jdbcTemplate.update(query);

        if (worked == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Boolean updateUser(UUID userID, User newUser) {
        String query = "UPDATE USER SET NAME = " + newUser.getName() + ", PASSWORD = "
                + newUser.getPassword() + " WHERE USERID = " + userID;
        int worked = jdbcTemplate.update(query);
        if (worked == 1) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public UUID getIdByToken(String token) {
        String query = "SELECT ID FROM AUTH WHERE TOKEN = " + token;
        UUID userID = jdbcTemplate.query(query, rs -> {
            UUID returnID = UUID.fromString(rs.getString(0));
            return returnID;
        });
        return userID;
    }

    @Override
    public Roles getRoleById(UUID userID) {
        String query = "SELECT * FROM ROLES WHERE ID = " + userID;
        Roles role = jdbcTemplate.query(query, rs -> {
            String roleID = rs.getString(0);
            String roleName = rs.getString(1);
            String description = rs.getString(2);
            Roles returnRole = new Roles(roleID, roleName, description);
            return returnRole;
        });
        return role;
    }

    @Override
    public Boolean setRoleByID(UUID userID, Roles role) {
        String firstQuery = "SELECT ID FROM ROLES ORDER BY ID DESC";
        int roleId = jdbcTemplate.query(firstQuery, rs -> {
            int returnID = rs.getInt(0);
            returnID++;
            return returnID;
        });
        String secondQuery = "UPDATE ROLES VALUES(" + roleId + ", " + userID + ", " + role.getRoleId() + ")";
        int worked = jdbcTemplate.update(secondQuery);
        if (worked == 1) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int setRole(Roles role) {
        String firstQuery = "SELECT ID FROM ROLE WHERE ID >= 1 ORDER BY ID DESC";
        int id = jdbcTemplate.query(firstQuery, rs -> {
            int returnId = rs.getInt(0);
            returnId++;
            return returnId;
        });
        String secondQuery = "INSERT INTO ROLE VALUES(" + id + ", '" + role.getRoleName() + "', '"
                + role.getDescription() + "'')";
        int worked = jdbcTemplate.update(secondQuery);
        return worked;
    }

    @Override
    public int updateRole(String roleId, Roles role) {
        String query = "UPDATE ROLE SET ROLENAME = '" + role.getRoleName() + "', '" + role.getDescription()
                + "' WHERE ID = " + roleId;
        int worked = jdbcTemplate.update(query);
        return worked;
    }

    @Override
    public int deleteRole(String roleId) {
        String query = "DELETE FROM ROLE WHERE ID = " + roleId;
        int worked = jdbcTemplate.update(query);
        return worked;
    }

    @Override
    public int getAuthId() {
        String query = "SELECT ID FROM AUTH WHERE ID >= 1 ORDER BY ID DESC";
        return jdbcTemplate.query(query, rs -> {
            int returnID = rs.getInt(0);
            returnID++;
            return returnID;
        });
    }

    @Override
    public int setAuth(int authId, String username, String accessToken, Date formattedDate) {
        String query = "INSERT INTO AUTH VALUES(" + authId + ", '" + username + "', '" + accessToken + "', '"
                + formattedDate + "')";
        int worked = jdbcTemplate.update(query);
        return worked;
    }

    @Override
    public String getUserName(UUID userId) {
        String query = "SELECT * FROM USER WHERE USERID = " + userId;
        return jdbcTemplate.query(query, rs -> {
            String username = rs.getString("USERNAME");
            return username;
        });
    }

	@Override
	public List<Object> getProducers(int Subscriber) {
		String query = "SELECT PRODUCERID FROM SUBSCRIPTIONS WHERE SUBSCRIBERID = " + Subscriber;
		List<Object> Producers = jdbcTemplate.query(query, (rs, rowNum) -> rs.getInt("PRODUCERID"));
		return Producers;
	}
}
