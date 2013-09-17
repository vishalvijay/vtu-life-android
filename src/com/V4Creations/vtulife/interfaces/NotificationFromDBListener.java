package com.V4Creations.vtulife.interfaces;

import java.util.ArrayList;

import com.V4Creations.vtulife.model.Notification;

public interface NotificationFromDBListener {
	public void notificationCreated(ArrayList<Notification> notifications);
}
