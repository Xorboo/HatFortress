package com.xorboo.hatfortress.messages.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;

import com.xorboo.hatfortress.messages.server.connection.ServerMessageFlags;

public class BulletStatusServerMessage extends ServerMessage implements ServerMessageFlags {
	public int key;
	public float posX, posY;
	
	public BulletStatusServerMessage() {
	}
	
	public BulletStatusServerMessage(final int _key, final float _posX, final float _posY) {
		this.key = _key;
		this.posX = _posX;
		this.posY = _posY;
	}

	// ===========================================================
	// Getter & Setter\\
	// ===========================================================

	public void set(final int _key, final float _posX, final float _posY) {
		this.key = _key;
		this.posX = _posX;
		this.posY = _posY;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public short getFlag() {
		return FLAG_MESSAGE_SERVER_STATUS_BULLET;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
		this.key = pDataInputStream.readInt();
		this.posX = pDataInputStream.readFloat();
		this.posY = pDataInputStream.readFloat();
	}

	@Override
	protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws IOException {
		pDataOutputStream.writeInt(this.key);
		pDataOutputStream.writeFloat(this.posX);
		pDataOutputStream.writeFloat(this.posY);
	}
}
