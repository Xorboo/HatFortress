package com.xorboo.hatfortress.messages.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;

import com.xorboo.hatfortress.messages.client.connection.ClientMessageFlags;
import com.xorboo.hatfortress.utils.database.PlayerDB;

public class StringClientMessage extends ClientMessage implements ClientMessageFlags {

	public String str = "";

	public StringClientMessage() {

	}

	public StringClientMessage(final String command) {
		this.str = command;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void set(final String command) {
		this.str = command;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public short getFlag() {
		return FLAG_MESSAGE_CLIENT_STRING;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
		this.str = pDataInputStream.readUTF();
	}

	@Override
	protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws IOException {
		pDataOutputStream.writeUTF(this.str);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}