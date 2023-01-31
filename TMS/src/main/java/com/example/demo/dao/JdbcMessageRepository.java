package com.example.demo.dao;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.demo.dtos.Constants;
import com.example.demo.dtos.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcMessageRepository implements MessageRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Message getMessagebyId(UUID messageId) {
        List<Message> messages = jdbcTemplate.query(Constants.GET_MESSAGE_BY_ID,
                (rs, rowNum) -> new Message(DaoHelper.bytesArrayToUuid(rs.getBytes("messages.id")),
                        DaoHelper.bytesArrayToUuid(rs.getBytes("messages.producer_id")),
                        rs.getString("messages.content"), rs.getDate("messages.created")),
                messageId.toString());

        // better to return empty message instead of null (for automatic processing)
        return Optional.ofNullable(messages.get(0)).orElse(new Message());
    }

    @Override
    public List<Message> getMessagesForProducerById(UUID prodicerId) {
        List<Message> messages = jdbcTemplate.query(Constants.GET_MESSAGES_FOR_PRODUCER,
                (rs, rowNum) -> new Message(DaoHelper.bytesArrayToUuid(rs.getBytes("messages.id")),
                        DaoHelper.bytesArrayToUuid(rs.getBytes("messages.producer_id")),
                        rs.getString("messages.content"), rs.getDate("messages.created")),
                prodicerId.toString());

        return Optional.ofNullable(messages).orElse(new ArrayList<>());
    }

    @Override
    public List<Message> getMessagesForSubscriberById(UUID subscriberId) {
        List<Message> messages = jdbcTemplate.query(Constants.GET_MESSAGES_FOR_SUBSCRIBER,
                (rs, rowNum) -> new Message(DaoHelper.bytesArrayToUuid(rs.getBytes("messages.id")),
                        DaoHelper.bytesArrayToUuid(rs.getBytes("messages.producer_id")),
                        rs.getString("messages.content"), rs.getDate("messages.created")),
                subscriberId.toString());
        return Optional.ofNullable(messages).orElse(new ArrayList<>());
    }

    @Override
    public UUID createMessage(Message message) {
        message.setId(UUID.randomUUID());
        message.setTimestamp(new Date());
        // check for empty message
        if ((message.getAuthor() == null || message.getContent() == null)
                & this.createProducer(message.getAuthor()) == null)
            return null;

        try {
            jdbcTemplate.update(Constants.CREATE_MESSAGE, message.getId().toString(), message.getAuthor().toString(),
                    message.getContent(), message.getTimestamp());
        } catch (Exception e) {
            return null;
        }
        return message.getId();
    }

    @Override
    public int deleteMessageById(UUID messageId) {
        return jdbcTemplate.update(Constants.DELETE_MESSAGE, messageId.toString());
    }

    UUID createProducer(UUID producerID) {
        try {
            jdbcTemplate.update(Constants.CREATE_PRODUCER, producerID.toString());
        } catch (Exception e) {
            return null;
        }
        return producerID;
    }

	@Override
	public boolean TokenCheck(String token) {
		List<Object> alpha = jdbcTemplate.query("SELECT USERID FROM AUTH WHERE TOKEN = " + token, (rs, rowNum) -> new String(rs.getString("USERID")));
		return Optional.ofNullable(alpha) != null;

	}

	@Override
	public UUID getIDByToken(String token) {
		List<Object> id = jdbcTemplate.query("SELECT USERID FROM AUTH WHERE TOKEN = " + token, (rs, rowNum) -> DaoHelper.bytesArrayToUuid(rs.getBytes("USERID")));
		return (UUID) id.get(0);
	}

	@Override
	public UUID getHighestID() {
		List<Object> alpha = jdbcTemplate.query("SELECT ID FROM MESSAGES WHERE ID >= 1 ORDER BY ID DESC", (rs, rowNum) -> DaoHelper.bytesArrayToUuid(rs.getBytes("ID")));
		UUID id = (UUID) alpha.get(0);
		return id;
	}

	@Override
	public int updateMessage(UUID id, String newData) {
	 return jdbcTemplate.update("UPDATE MESSAGES SET CONTENT = " + newData + " WHERE ID = " + id);	
	}

	@Override
	public int setToken(UUID newID, String username, String accessToken) {
		return jdbcTemplate.update("INSERT INTO AUTH VALUES(" + newID + ", " + username + ", " + accessToken + ")");
	}
}