package com.xorboo.hatfortress.messages.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;

import com.xorboo.hatfortress.messages.client.connection.ClientMessageFlags;

public class MovePlayerClientMessage extends ClientMessage implements ClientMessageFlags {
	
	public int playerID;
	public int direction; // 1 - вправо, 0 - стоит, -1 - влево
	public boolean jump;

	public MovePlayerClientMessage() {

	}

	public MovePlayerClientMessage(final int pID, final int dir, final boolean isJumping) {
		this.playerID = pID;
		this.direction = dir;
		this.jump = isJumping;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setPlayer(final int pID, final int dir, final boolean isJumping) {
		this.playerID = pID;
		this.direction = dir;
		this.jump = isJumping;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public short getFlag() {
		return FLAG_MESSAGE_CLIENT_MOVE_PLAYER;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
		this.playerID = pDataInputStream.readInt();
		this.direction = pDataInputStream.readInt();
		this.jump = pDataInputStream.readBoolean();
	}

	@Override
	protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws IOException {
		pDataOutputStream.writeInt(this.playerID);
		pDataOutputStream.writeInt(this.direction);
		pDataOutputStream.writeBoolean(this.jump);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}