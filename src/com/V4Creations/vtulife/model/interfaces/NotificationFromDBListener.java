package com.V4Creations.vtulife.model.interfaces;

import java.util.ArrayList;

import com.V4Creations.vtulife.model.VTULifeNotification;

public interface NotificationFromDBListener {
	public void notificationListCreated(ArrayList<VTULifeNotification> notifications);
}
