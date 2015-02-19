package com.xorboo.hatfortress.messages.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;

import com.xorboo.hatfortress.messages.server.connection.ServerMessageFlags;

/**
 * (c) 2010 Nicolas Gramlich 
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 19:48:32 - 28.02.2011
 */
public class SetPlayerIDServerMessage extends ServerMessage implements ServerMessageFlags {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	public int playerID;
	public int mapID;

	// ===========================================================
	// Constructors
	// ===========================================================

	public SetPlayerIDServerMessage() {

	}

	public SetPlayerIDServerMessage(final int pPlayerID, final int pMapID) {
		this.playerID = pPlayerID;
		this.mapID = pMapID;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void set(final int pPlayerID, final int pMapID) {
		this.playerID = pPlayerID;
		this.mapID = pMapID;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public short getFlag() {
		return FLAG_MESSAGE_SERVER_SET_PLAYERID;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
		this.playerID = pDataInputStream.readInt();
		this.mapID = pDataInputStream.readInt();
	}

	@Override
	protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws IOException {
		pDataOutputStream.writeInt(this.playerID);
		pDataOutputStream.writeInt(this.mapID);
	}
	
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}