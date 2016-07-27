package com.howell.talk;

public interface ITalkProtocol {

	final int HWVOICE_PROTOCOL_TAG							= 0x12345678;
	final int HWVOICE_PROTOCOL_VERSION						= 0x00000001;
	
	
	final int HWVOICE_PROTOCOL_ACK 							= 0x80000000;
	final int HWVOICE_PROTOCOL_REGISTER						= 0x00000001;
	final int HWVOICE_PROTOCOL_ALIVE						= 0x00000002;
	final int HWVOICE_PROTOCOL_SENDING						= 0x00000003;
	final int HWVOICE_PROTOCOL_DIALOG_LIST					= 0x00000004;
	final int HWVOICE_PROTOCOL_RECEINING					= 0x00000005;
	final int HWVOICE_PROTOCOL_BOARDCAST_SENDING			= 0x00000006;
	final int HWVOICE_PROTOCOL_SILENT						= 0x00000007;
	final int HWVOICE_PROTOCOL_CREATE_GROUP					= 0x00000008;
	final int HWVOICE_PROTOCOL_DELETE_GROUP					= 0x00000009;
	final int HWVOICE_PROTOCOL_SET_GROUP					= 0x0000000a;
	final int HWVOICE_PROTOCOL_GET_GROUPS					= 0x0000000b;
	final int HWVOICE_PROTOCOL_GET_GROUP					= 0x0000000c;
	final int HWVOICE_PROTOCOL_GROUP_SENDING				= 0x0000000d;
	final int HWVOICE_PROTOCOL_GROUP_RECEIVING				= 0x0000000e;
	final int HWVOICE_PROTOCOL_SILENT_GROUP					= 0x0000000f;
	final int HWVOICE_PROTOCOL_ONOFFLINE_NOTICE				= 0x00000010;
	final int HWVOICE_PROTOCOL_GET_USERS					= 0x00000011;
	final int HWVOICE_PROTOCOL_GET_USER						= 0x00000012;
	
	

}