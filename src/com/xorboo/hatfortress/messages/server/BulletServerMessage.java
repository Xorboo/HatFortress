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
public class BulletServerMessage extends ServerMessage implements ServerMessageFlags {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	public int key;
	public float dirX, dirY;
	public float posX, posY;
	public int type;
	public boolean added;

	// ===========================================================
	// Constructors
	// ===========================================================

	public BulletServerMessage() {

	}

	public BulletServerMessage(final int _key, final float _dirX, final float _dirY, final float _posX, final float _posY, 
			final int _type, final boolean _added) {
		this.key = _key;
		this.dirX = _dirX;
		this.dirY = _dirY;
		this.posX = _posX;
		this.posY = _posY;
		this.type = _type;
		this.added = _added;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void set(final int _key, final float _dirX, final float _dirY, final float _posX, final float _posY, 
			final int _type, final boolean _added) {
		this.key = _key;
		this.dirX = _dirX;
		this.dirY = _dirY;
		this.posX = _posX;
		this.posY = _posY;
		this.type = _type;
		this.added = _added;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public short getFlag() {
		return FLAG_MESSAGE_SERVER_BULLET;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
		this.key = pDataInputStream.readInt();
		this.type = pDataInputStream.readInt();
		this.added = pDataInputStream.readBoolean();
		this.dirX = pDataInputStream.readFloat();
		this.dirY = pDataInputStream.readFloat();
		this.posX = pDataInputStream.readFloat();
		this.posY = pDataInputStream.readFloat();
	}

	@Override
	protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws IOException {
		pDataOutputStream.writeInt(this.key);
		pDataOutputStream.writeInt(this.type);
		pDataOutputStream.writeBoolean(this.added);
		pDataOutputStream.writeFloat(this.dirX);
		pDataOutputStream.writeFloat(this.dirY);
		pDataOutputStream.writeFloat(this.posX);
		pDataOutputStream.writeFloat(this.posY);
	}
	
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}