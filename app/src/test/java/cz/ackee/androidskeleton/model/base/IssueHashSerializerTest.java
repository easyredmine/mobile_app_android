package cz.ackee.androidskeleton.model.base;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import junit.framework.TestCase;

import org.junit.Before;

import java.util.ArrayList;

import cz.ackee.androidskeleton.model.IssueHash;
import cz.ackee.androidskeleton.model.response.CustomField;

/**
 * Test of custom issue hash gson serializer
 * Created by David Bilik[david.bilik@ackee.cz] on {3. 7. 2015}
 **/
public class IssueHashSerializerTest extends TestCase {
    @Before
    public void setUp() throws Exception {
        super.setUp();

    }

    @SmallTest
    public void testIssueHashSerializer() {
        IssueHash issueHash = new IssueHash();
        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).registerTypeAdapter(IssueHash.class, new IssueHashSerializer()).create();
        String json = gson.toJson(issueHash, IssueHash.class);
        assertEquals(json, "{}");
        issueHash.subject = "Muj tiket";
        json = gson.toJson(issueHash, IssueHash.class);
        assertTrue(json.contains("subject"));
        issueHash.subject = null;
        json = gson.toJson(issueHash, IssueHash.class);
        assertFalse(json.contains("subject"));
        issueHash.customFields = new ArrayList<>();
        json = gson.toJson(issueHash, IssueHash.class);
        assertFalse(json.contains("custom_fields"));
        issueHash.customFields.add(new CustomField());
        json = gson.toJson(issueHash, IssueHash.class);
        assertTrue(json.contains("custom_fields"));
    }
}