package com.xorboo.hatfortress.messages.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;

import com.xorboo.hatfortress.messages.server.connection.ServerMessageFlags;

public class PlayerQuitServerMessage extends ServerMessage implements ServerMessageFlags {
	
	public int playerID;

	public PlayerQuitServerMessage() {

	}

	public PlayerQuitServerMessage(final int pID) {
		this.playerID = pID;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setPlayerStatus(final int pID) {
		this.playerID = pID;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public short getFlag() {
		return FLAG_MESSAGE_SERVER_PLAYER_QUIT;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
		this.playerID = pDataInputStream.readInt();
	}

	@Override
	protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws IOException {
		pDataOutputStream.writeInt(this.playerID);
	}
}