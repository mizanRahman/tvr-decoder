package com.willcurrie.decoders.apdu;

import com.willcurrie.DecodedData;
import com.willcurrie.EmvTags;
import com.willcurrie.QVsdcTags;
import com.willcurrie.decoders.DecodeSession;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.collection.IsCollectionContaining.hasItem;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GetProcessingOptionsCommandAPDUDecoderTest {

    @Test
    public void testDecodeVisaWithPDOL() throws Exception {
        DecodeSession session = new DecodeSession();
        session.setTagMetaData(QVsdcTags.METADATA);
        session.put(EmvTags.PDOL, "9F66049F02069F03069F1A0295055F2A029A039C019F3704");
        String input = "80A8000023832136000000000000001000000000000000003600000000000036120315000008E4C800";
        DecodedData decoded = new GetProcessingOptionsCommandAPDUDecoder().decode(input, 0, session);
        assertThat(decoded.getRawData(), is("C-APDU: GPO"));
        List<DecodedData> children = decoded.getChildren();
        List<DecodedData> expectedDecodedTTQ = QVsdcTags.METADATA.get(QVsdcTags.TERMINAL_TX_QUALIFIERS).getDecoder().decode("36000000", 7, new DecodeSession());
        assertThat(children, hasItem(new DecodedData(QVsdcTags.TERMINAL_TX_QUALIFIERS.toString(QVsdcTags.METADATA), "36000000", 7, 11, expectedDecodedTTQ)));
        assertThat(children, hasItem(new DecodedData(EmvTags.UNPREDICTABLE_NUMBER.toString(EmvTags.METADATA), "0008E4C8", 36, 40)));
    }

    @Test
    public void testDecodeMastercardWithoutPDOL() throws Exception {
        DecodeSession session = new DecodeSession();
        String input = "80A8000002830000";
        DecodedData decoded = new GetProcessingOptionsCommandAPDUDecoder().decode(input, 0, session);
        assertThat(decoded.getRawData(), is("C-APDU: GPO"));
        assertThat(decoded.getDecodedData(), is("No PDOL included"));
        assertThat(decoded.isComposite(), is(false));
    }
}
