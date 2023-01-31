package com.example.demo.dao;

import java.util.List;
import java.util.UUID;

import com.example.demo.dtos.Message;

public interface MessageRepository {
    public Message getMessagebyId(UUID messageID);

    public List<Message> getMessagesForProducerById(UUID producerId);

    public List<Message> getMessagesForSubscriberById(UUID subscriberId);

    public UUID createMessage(Message message);

    public int deleteMessageById(UUID messageId);

	public boolean TokenCheck(String token);

	public UUID getIDByToken(String token);

	public UUID getHighestID();

	public int updateMessage(UUID id, String newData);

	public int setToken(UUID newID, String username, String accesstoken);
}
