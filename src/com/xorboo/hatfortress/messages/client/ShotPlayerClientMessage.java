package com.xorboo.hatfortress.messages.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;

import com.xorboo.hatfortress.messages.client.connection.ClientMessageFlags;

public class ShotPlayerClientMessage extends ClientMessage implements ClientMessageFlags{
	
	public int playerID;
	public float dirX, dirY;

	public ShotPlayerClientMessage() {

	}

	public ShotPlayerClientMessage(final int pID, final float x, final float y) {
		this.playerID = pID;
		this.dirX = x;
		this.dirY = y;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setPlayerShot(final int pID, final float x, final float y) {
		this.playerID = pID;
		this.dirX = x;
		this.dirY = y;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public short getFlag() {
		return FLAG_MESSAGE_CLIENT_SHOT_PLAYER;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
		this.playerID = pDataInputStream.readInt();
		this.dirX = pDataInputStream.readFloat();
		this.dirY = pDataInputStream.readFloat();
	}

	@Override
	protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws IOException {
		pDataOutputStream.writeInt(this.playerID);
		pDataOutputStream.writeFloat(this.dirX);
		pDataOutputStream.writeFloat(this.dirY);
	}
}
