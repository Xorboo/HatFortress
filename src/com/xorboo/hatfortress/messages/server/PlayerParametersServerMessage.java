package com.xorboo.hatfortress.messages.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;

import com.xorboo.hatfortress.messages.server.connection.ServerMessageFlags;

public class PlayerParametersServerMessage extends ServerMessage implements ServerMessageFlags {
	public int playerID;
	public int hp;
	public int weapon;
	public int armor;
	public int lives;

	public PlayerParametersServerMessage() {

	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void set(int playerID, int hp, int weapon, int armor, int lives) {
		this.playerID = playerID;
		this.hp = hp;
		this.weapon = weapon;
		this.armor = armor;
		this.lives = lives;
	}
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public short getFlag() {
		return FLAG_MESSAGE_SERVER_PLAYER_PARAMETERS;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
		this.playerID = pDataInputStream.readInt();
		this.hp = pDataInputStream.readInt();
		this.weapon = pDataInputStream.readInt();
		this.armor = pDataInputStream.readInt();
		this.lives = pDataInputStream.readInt();
	}

	@Override
	protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws IOException {
		pDataOutputStream.writeInt(this.playerID);
		pDataOutputStream.writeInt(this.hp);
		pDataOutputStream.writeInt(this.weapon);
		pDataOutputStream.writeInt(this.armor);
		pDataOutputStream.writeInt(this.lives);
	}
}
