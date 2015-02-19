package com.xorboo.hatfortress.messages.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;

import com.xorboo.hatfortress.messages.server.connection.ServerMessageFlags;

public class PlayerPositionServerMessage extends ServerMessage implements ServerMessageFlags {
	
	public int playerID;
	public float x;
	public float y;

	public PlayerPositionServerMessage() {

	}

	public PlayerPositionServerMessage(final int pID, final float _x, final float _y) {
		this.playerID = pID;
		this.x = _x;
		this.y = _y;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setPlayerStatus(final int pID, final float _x, final float _y) {
		this.playerID = pID;
		this.x = _x;
		this.y = _y;
	}
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public short getFlag() {
		return FLAG_MESSAGE_SERVER_PLAYER_MOVE;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
		this.playerID = pDataInputStream.readInt();
		this.x = pDataInputStream.readFloat();
		this.y = pDataInputStream.readFloat();
	}

	@Override
	protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws IOException {
		pDataOutputStream.writeInt(this.playerID);
		pDataOutputStream.writeFloat(this.x);
		pDataOutputStream.writeFloat(this.y);
	}
}