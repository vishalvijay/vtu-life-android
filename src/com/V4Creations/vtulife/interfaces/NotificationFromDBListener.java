package com.V4Creations.vtulife.interfaces;

import java.util.ArrayList;

import com.V4Creations.vtulife.model.VTULifeNotification;

public interface NotificationFromDBListener {
	public void notificationCreated(ArrayList<VTULifeNotification> notifications);
}
