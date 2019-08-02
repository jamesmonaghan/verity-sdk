package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.utils.Context;

import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class WriteSchemaTest {

    private String schemaName = "test schema";
    private String schemaVersion = "0.0.1";
    private String attr1 = "name";
    private String attr2 = "degree";

    @Test
    public void testGetMessageType() {
        String msgName = "msg name";
        assertEquals(Util.getMessageType("write-schema", "0.1", msgName), WriteSchema.getMessageType(msgName));
    }

    @Test
    public void testConstructor() {
        WriteSchema writeSchema = new WriteSchema(schemaName, schemaVersion, attr1, attr2);
        assertEquals(schemaName, writeSchema.name);
        assertEquals(schemaVersion, writeSchema.version);
        assertEquals(attr1, writeSchema.attrs[0]);
        assertEquals(attr2, writeSchema.attrs[1]);
        testMessages(writeSchema);
    }

    private void testMessages(WriteSchema writeSchema) {
        JSONObject msg = writeSchema.messages.getJSONObject(WriteSchema.WRITE_SCHEMA);
        assertEquals(WriteSchema.getMessageType("write"), msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertEquals(schemaName, msg.getString("name"));
        assertEquals(schemaVersion, msg.getString("version"));
        assertEquals(attr1, msg.getJSONArray("attrNames").getString(0));
        assertEquals(attr2, msg.getJSONArray("attrNames").getString(1));

    }

    @Test
    public void testWrite() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            WriteSchema writeSchema = new WriteSchema(schemaName, schemaVersion, attr1, attr2);
            writeSchema.disableHTTPSend();
            byte[] message = writeSchema.write(context);
            JSONObject unpackedMessage = Util.unpackForwardMessage(context, message);
            assertEquals(WriteSchema.getMessageType(WriteSchema.WRITE_SCHEMA), unpackedMessage.getString("@type"));
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }
}