package com.startandselect.agora;

import com.startandselect.agora.net.RestRequest;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void net_asynk() {
        RestRequest k = new RestRequest();
        //k.process();
    }
}