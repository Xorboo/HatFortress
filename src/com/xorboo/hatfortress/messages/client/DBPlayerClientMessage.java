package com.xorboo.hatfortress.messages.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;

import com.xorboo.hatfortress.messages.client.connection.ClientMessageFlags;
import com.xorboo.hatfortress.utils.database.PlayerDB;

public class DBPlayerClientMessage extends ClientMessage implements ClientMessageFlags {

	public PlayerDB dbInfo = null;
	public int pID = 0;
	public int _id = 0;
	public String name = "";
	public String password = "";
	public int exp = 0;
	public int money = 0;
	public int games = 0;
	public int type = 0;
	public int avID = 0;

	public DBPlayerClientMessage() {

	}

	public DBPlayerClientMessage(final PlayerDB info, int pID) {
		this.dbInfo = info;
		this.pID = pID;
		this._id = info._id;
		this.name = info.name;
		this.password = info.password;
		this.exp = info.exp;
		this.money = info.money;
		this.type = info.type;
		this.avID = info.avID;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void setPlayer(final PlayerDB info, int pID) {
		this.dbInfo = info;
		this.pID = pID;
		this._id = info._id;
		this.name = info.name;
		this.password = info.password;
		this.exp = info.exp;
		this.money = info.money;
		this.type = info.type;
		this.avID = info.avID;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public short getFlag() {
		return FLAG_MESSAGE_CLIENT_DB_INFO;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
		this.pID = pDataInputStream.readInt();
		this._id = pDataInputStream.readInt();
		this.name = pDataInputStream.readUTF();
		this.password = pDataInputStream.readUTF();
		this.exp = pDataInputStream.readInt();
		this.money = pDataInputStream.readInt();
		this.type = pDataInputStream.readInt();
		this.avID = pDataInputStream.readInt();
		
		this.dbInfo = new PlayerDB();
		this.dbInfo._id = this._id;
		this.dbInfo.name = name;
		this.dbInfo.password = password;
		this.dbInfo.exp = exp;
		this.dbInfo.money = money;
		this.dbInfo.type = type;
		this.dbInfo.avID = avID;
	}

	@Override
	protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws IOException {
		pDataOutputStream.writeInt(this.pID);
		pDataOutputStream.writeInt(this._id);
		pDataOutputStream.writeUTF(this.name);
		pDataOutputStream.writeUTF(this.password);
		pDataOutputStream.writeInt(this.exp);
		pDataOutputStream.writeInt(this.money);
		pDataOutputStream.writeInt(this.type);
		pDataOutputStream.writeInt(this.avID);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}