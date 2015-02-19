package com.xorboo.hatfortress.messages.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;

import com.xorboo.hatfortress.messages.client.connection.ClientMessageFlags;

public class WeaponPlayerClientMessage extends ClientMessage implements ClientMessageFlags{
	
	public int playerID;
	public int weaponID;

	public WeaponPlayerClientMessage() {

	}

	public WeaponPlayerClientMessage(final int pID, final int wID) {
		this.playerID = pID;
		this.weaponID = wID;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setPlayerShot(final int pID, final int wID) {
		this.playerID = pID;
		this.weaponID = wID;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public short getFlag() {
		return FLAG_MESSAGE_CLIENT_CHOOSE_WEAPON;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
		this.playerID = pDataInputStream.readInt();
		this.weaponID = pDataInputStream.readInt();
	}

	@Override
	protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws IOException {
		pDataOutputStream.writeInt(this.playerID);
		pDataOutputStream.writeInt(this.weaponID);
	}
}
