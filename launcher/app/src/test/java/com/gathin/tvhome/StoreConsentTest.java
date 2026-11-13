package com.gathin.tvhome;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/** The store questions: which screen is next, and which reads stay off until Agree. */
public class StoreConsentTest {

    @Test
    public void dataQuestionComesFirst() {
        assertEquals(StoreConsent.KIND_DATA,
                StoreConsent.nextKind(StoreConsent.UNSET, StoreConsent.UNSET));
    }

    @Test
    public void accessibilityQuestionFollowsEitherDataAnswer() {
        assertEquals(StoreConsent.KIND_ACCESSIBILITY,
                StoreConsent.nextKind(StoreConsent.YES, StoreConsent.UNSET));
        assertEquals(StoreConsent.KIND_ACCESSIBILITY,
                StoreConsent.nextKind(StoreConsent.NO, StoreConsent.UNSET));
    }

    @Test
    public void playBuildSkipsTheRemoteButtonQuestion() {
        assertEquals(StoreConsent.KIND_DATA,
                StoreConsent.nextKind(StoreConsent.UNSET, StoreConsent.UNSET, false));
        assertNull(StoreConsent.nextKind(StoreConsent.YES, StoreConsent.UNSET, false));
        assertNull(StoreConsent.nextKind(StoreConsent.NO, StoreConsent.UNSET, false));
    }

    @Test
    public void bothAnswersLeaveNoFurtherQuestion() {
        assertNull(StoreConsent.nextKind(StoreConsent.YES, StoreConsent.NO));
        assertNull(StoreConsent.nextKind(StoreConsent.NO, StoreConsent.YES));
    }

    @Test
    public void onlyAgreeTurnsTheReadAndTheHelpersOn() {
        assertFalse(StoreConsent.mayReadWifiName(StoreConsent.UNSET));
        assertFalse(StoreConsent.mayReadWifiName(StoreConsent.NO));
        assertTrue(StoreConsent.mayReadWifiName(StoreConsent.YES));
        assertFalse(StoreConsent.mayFilterKeys(StoreConsent.UNSET));
        assertFalse(StoreConsent.mayFilterKeys(StoreConsent.NO));
        assertTrue(StoreConsent.mayFilterKeys(StoreConsent.YES));
    }
}
