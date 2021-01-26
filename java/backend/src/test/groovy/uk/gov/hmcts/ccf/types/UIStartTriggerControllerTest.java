package uk.gov.hmcts.ccf.types;

import org.junit.Test;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseUpdateViewEvent;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewField;
import uk.gov.hmcts.ccd.domain.model.definition.FieldTypeDefinition;
import uk.gov.hmcts.ccd.v2.internal.controller.UIStartTriggerController;
import uk.gov.hmcts.ccf.EventBuilder;
import uk.gov.hmcts.ccf.types.dto.GeneralReferral;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UIStartTriggerControllerTest {

    private UIStartTriggerController controller = new UIStartTriggerController();

    @Test
    public void describesEvent() {
        CaseUpdateViewEvent e = controller.getCaseUpdateViewEvent("1", "generalReferral",
            false).getBody().getCaseUpdateViewEvent();

        assertTrue(e.getCaseFields().size() > 0);
        assertTrue(e.getWizardPages().size() > 0);
    }

    @Test
    public void buildReasonForReferral() {
        EventBuilder<GeneralReferral> e = new EventBuilder<>(GeneralReferral.class);
        e.field(GeneralReferral::getReason);
        FieldTypeDefinition fieldtype = e.build().getCaseFields().get(0).getFieldTypeDefinition();

        assertEquals("FixedRadioList", fieldtype.getType());
        assertEquals(2, fieldtype.getFixedListItemDefinitions().size());
        assertEquals("Caseworker referral", fieldtype.getFixedListItemDefinitions().get(0).getLabel());
    }

    @Test
    public void buildsReferralDate() {
        EventBuilder<GeneralReferral> e = new EventBuilder<>(GeneralReferral.class);
        e.field(GeneralReferral::getDate);
        CaseViewField date = e.build().getCaseFields().get(0);
        FieldTypeDefinition fieldtype = date.getFieldTypeDefinition();

        assertEquals("date", date.getId());
        assertEquals("Application or referral date", date.getLabel());

        assertEquals("Date", fieldtype.getType());
        assertEquals("Date", fieldtype.getId());
    }

    @Test
    public void buildsReferralDetails() {
        EventBuilder<GeneralReferral> e = new EventBuilder<>(GeneralReferral.class);
        e.field(GeneralReferral::getReferralDetails);
        CaseViewField date = e.build().getCaseFields().get(0);
        FieldTypeDefinition fieldtype = date.getFieldTypeDefinition();

        assertEquals("referralDetails", date.getId());

        assertEquals("TextArea", fieldtype.getType());
        assertEquals("TextArea", fieldtype.getId());
    }

    @Test
    public void buildsBoolean() {
        EventBuilder<GeneralReferral> e = new EventBuilder<>(GeneralReferral.class);
        e.field(GeneralReferral::isFeeRequired);
        CaseViewField date = e.build().getCaseFields().get(0);
        FieldTypeDefinition fieldtype = date.getFieldTypeDefinition();

        assertEquals("feeRequired", date.getId());

        assertEquals("YesOrNo", fieldtype.getType());
        assertEquals("YesOrNo", fieldtype.getId());
    }
    
}
