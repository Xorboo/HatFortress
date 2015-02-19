package com.xorboo.hatfortress.messages.server.connection;

/**
 * (c) 2010 Nicolas Gramlich 
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 11:59:39 - 21.05.2011
 */
public interface ServerMessageFlags {
	// ===========================================================
	// Final Fields
	// ===========================================================

	/* Connection Flags. */
	public static final short FLAG_MESSAGE_SERVER_CONNECTION_CLOSE = Short.MIN_VALUE;
	public static final short FLAG_MESSAGE_SERVER_CONNECTION_ESTABLISHED = FLAG_MESSAGE_SERVER_CONNECTION_CLOSE + 1;
	public static final short FLAG_MESSAGE_SERVER_CONNECTION_REJECTED_PROTOCOL_MISSMATCH = FLAG_MESSAGE_SERVER_CONNECTION_ESTABLISHED + 1;
	public static final short FLAG_MESSAGE_SERVER_CONNECTION_PING = FLAG_MESSAGE_SERVER_CONNECTION_REJECTED_PROTOCOL_MISSMATCH + 1;


	/* Server --> Client */
	public static final short FLAG_MESSAGE_SERVER_SET_PLAYERID = 1;
	public static final short FLAG_MESSAGE_SERVER_PLAYER_MOVE = FLAG_MESSAGE_SERVER_SET_PLAYERID + 1;
	public static final short FLAG_MESSAGE_SERVER_PLAYER_QUIT = FLAG_MESSAGE_SERVER_PLAYER_MOVE + 1;
	public static final short FLAG_MESSAGE_SERVER_BULLET = FLAG_MESSAGE_SERVER_PLAYER_QUIT + 1;
	public static final short FLAG_MESSAGE_SERVER_STATUS_BULLET = FLAG_MESSAGE_SERVER_BULLET + 1;
	public static final short FLAG_MESSAGE_SERVER_PLAYER_DEAD = FLAG_MESSAGE_SERVER_STATUS_BULLET + 1;
	public static final short FLAG_MESSAGE_SERVER_PLAYER_PARAMETERS = FLAG_MESSAGE_SERVER_PLAYER_DEAD + 1;
	public static final short FLAG_MESSAGE_SERVER_DB_INFO = FLAG_MESSAGE_SERVER_PLAYER_PARAMETERS + 1;
	public static final short FLAG_MESSAGE_SERVER_DB_STATISTICS = FLAG_MESSAGE_SERVER_DB_INFO + 1;
	public static final short FLAG_MESSAGE_SERVER_STRING = FLAG_MESSAGE_SERVER_DB_STATISTICS + 1;
	
	// ===========================================================
	// Methods
	// ===========================================================
}
