package com.xorboo.hatfortress.messages.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;

import com.xorboo.hatfortress.messages.server.connection.ServerMessageFlags;

public class PlayerDeadServerMessage extends ServerMessage implements ServerMessageFlags {
	public int playerID;
	public int killerID;
	public int bulletType;

	public PlayerDeadServerMessage() {

	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void set(int playerID, int killerID, int bulletType) {
		this.killerID = killerID;
		this.playerID = playerID;
		this.bulletType = bulletType;
	}
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public short getFlag() {
		return FLAG_MESSAGE_SERVER_PLAYER_DEAD;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
		this.playerID = pDataInputStream.readInt();
		this.killerID = pDataInputStream.readInt();
		this.bulletType = pDataInputStream.readInt();
	}

	@Override
	protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws IOException {
		pDataOutputStream.writeInt(this.playerID);
		pDataOutputStream.writeInt(this.killerID);
		pDataOutputStream.writeInt(this.bulletType);
	}
}
