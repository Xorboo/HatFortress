package com.xorboo.hatfortress.messages.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;

import android.util.SparseArray;

import com.xorboo.hatfortress.game.PlayerObject;
import com.xorboo.hatfortress.messages.server.connection.ServerMessageFlags;
import com.xorboo.hatfortress.utils.database.GameDB;
import com.xorboo.hatfortress.utils.database.GamePlayerDB;
import com.xorboo.hatfortress.utils.database.PlayerDB;

public class DBStatisticsServerMessage extends ServerMessage implements ServerMessageFlags {

	public ArrayList<PlayerDB> playerDBs = new ArrayList<PlayerDB>();
	public ArrayList<GamePlayerDB> gpDBs = new ArrayList<GamePlayerDB>();
	public GameDB game;

	public DBStatisticsServerMessage() {

	}

	public DBStatisticsServerMessage(final GameDB _game, SparseArray<PlayerObject> _players) {		
		game = _game;
		for (int i = 0; i < _players.size(); i++) {
			PlayerObject p = _players.valueAt(i);
			playerDBs.add(p.dbInfo);
			gpDBs.add(p.stats);
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void set(final GameDB _game, SparseArray<PlayerObject> _players) {		
		game = _game;
		for (int i = 0; i < _players.size(); i++) {
			PlayerObject p = _players.valueAt(i);
			playerDBs.add(p.dbInfo);
			gpDBs.add(p.stats);
		}
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public short getFlag() {
		return FLAG_MESSAGE_SERVER_DB_STATISTICS;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream) throws IOException {
		// Получаем инфу об игре
		game = readGame(pDataInputStream);
		// Получаем инфу об игроке и его игре
		int size = pDataInputStream.readInt();
		for (int i = 0; i < size; i++) {
			PlayerDB pl = readPlayerDB(pDataInputStream);	
			playerDBs.add(pl);
			GamePlayerDB gp = readGP(pDataInputStream);
			gpDBs.add(gp);
		}
	}

	private GameDB readGame(DataInputStream s) throws IOException {
		GameDB p = new GameDB();
		p._id = s.readInt();
		p.map = s.readInt();
		p.time = s.readUTF();
		return p;
	}

	private PlayerDB readPlayerDB(DataInputStream s) throws IOException {
		PlayerDB p = new PlayerDB();
		p._id = s.readInt();
		p.name = s.readUTF();
		p.password = s.readUTF();
		p.exp = s.readInt();
		p.money = s.readInt();
		p.type = s.readInt();
		p.avID = s.readInt();
		return p;
	}

	private GamePlayerDB readGP(DataInputStream s) throws IOException {
		GamePlayerDB p = new GamePlayerDB();
		p._id = s.readInt();
		p.pID = s.readInt();
		p.gID = s.readInt();
		p.kills = s.readInt();
		p.deaths = s.readInt();
		p.jumps = s.readInt();
		p.shoots = s.readInt();
		p.winner = s.readInt();
		return p;
	}
	
	@Override
	protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws IOException {
		writeGame(pDataOutputStream, game);
		pDataOutputStream.writeInt(playerDBs.size());
		for (int i = 0; i < playerDBs.size(); i++) {
			writePlayerDB(pDataOutputStream, playerDBs.get(i));
			writeStatsDB(pDataOutputStream, gpDBs.get(i));
		}
	}

	private void writeGame(DataOutputStream s, GameDB g) throws IOException {
		s.writeInt(g._id);
		s.writeInt(g.map);
		s.writeUTF(g.time);
	}

	private void writePlayerDB(DataOutputStream s, PlayerDB g) throws IOException {
		s.writeInt(g._id);
		s.writeUTF(g.name);
		s.writeUTF(g.password);
		s.writeInt(g.exp);
		s.writeInt(g.money);
		s.writeInt(g.type);
		s.writeInt(g.avID);
	}

	private void writeStatsDB(DataOutputStream s, GamePlayerDB g) throws IOException {
		s.writeInt(g._id);
		s.writeInt(g.pID);
		s.writeInt(g.gID);
		s.writeInt(g.kills);
		s.writeInt(g.deaths);
		s.writeInt(g.jumps);
		s.writeInt(g.shoots);
		s.writeInt(g.winner);
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}