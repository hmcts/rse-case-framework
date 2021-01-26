package uk.gov.hmcts.ccf.types;

import org.junit.Test;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewField;
import uk.gov.hmcts.ccd.domain.model.definition.FieldTypeDefinition;
import uk.gov.hmcts.ccd.domain.model.definition.WizardPage;
import uk.gov.hmcts.ccd.domain.model.definition.WizardPageField;
import uk.gov.hmcts.ccf.EventBuilder;
import uk.gov.hmcts.ccf.types.dto.GeneralReferral;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EventBuilderTest {

    @Test
    public void buildReasonForReferral() {
        EventBuilder<GeneralReferral> e = new EventBuilder<>(GeneralReferral.class);
        e.field(GeneralReferral::getReason);
        FieldTypeDefinition fieldtype = e.build().getViewEvent().getCaseFields().get(0).getFieldTypeDefinition();

        assertEquals("FixedRadioList", fieldtype.getType());
        assertEquals(2, fieldtype.getFixedListItemDefinitions().size());
        assertEquals("Caseworker referral", fieldtype.getFixedListItemDefinitions().get(0).getLabel());
    }

    @Test
    public void buildsReferralDate() {
        EventBuilder<GeneralReferral> e = new EventBuilder<>(GeneralReferral.class);
        e.field(GeneralReferral::getDate);
        CaseViewField date = e.build().getViewEvent().getCaseFields().get(0);

        assertEquals("date", date.getId());
        assertTrue(date.getShowSummaryChangeOption());
        assertEquals("Application or referral date", date.getLabel());

        FieldTypeDefinition fieldtype = date.getFieldTypeDefinition();
        assertEquals("Date", fieldtype.getType());
        assertEquals("Date", fieldtype.getId());
    }

    @Test
    public void buildsReferralDetails() {
        EventBuilder<GeneralReferral> e = new EventBuilder<>(GeneralReferral.class);
        e.field(GeneralReferral::getReferralDetails);
        CaseViewField date = e.build().getViewEvent().getCaseFields().get(0);
        FieldTypeDefinition fieldtype = date.getFieldTypeDefinition();

        assertEquals("referralDetails", date.getId());

        assertEquals("Text", fieldtype.getType());
        assertEquals("Text", fieldtype.getId());
    }

    @Test
    public void buildsBoolean() {
        EventBuilder<GeneralReferral> e = new EventBuilder<>(GeneralReferral.class);
        e.field(GeneralReferral::isFeeRequired);
        CaseViewField date = e.build().getViewEvent().getCaseFields().get(0);
        FieldTypeDefinition fieldtype = date.getFieldTypeDefinition();

        assertEquals("feeRequired", date.getId());

        assertEquals("YesOrNo", fieldtype.getType());
        assertEquals("YesOrNo", fieldtype.getId());
    }

    @Test
    public void buildsWizardPage() {
        EventBuilder<GeneralReferral> e = new EventBuilder<>(GeneralReferral.class);
        e.field(GeneralReferral::isFeeRequired);
        e.field(GeneralReferral::getDate);
        List<WizardPage> pages = e.build().getViewEvent().getWizardPages();
        assertEquals(1, pages.size());
        WizardPage page = pages.get(0);
        assertEquals("1", page.getId());
        assertEquals(1, page.getOrder());
        assertEquals(2, page.getWizardPageFields().size());

        List<WizardPageField> fields = page.getWizardPageFields();
        assertEquals("feeRequired", fields.get(0).getCaseFieldId());
        assertEquals("date", fields.get(1).getCaseFieldId());

        assertEquals(1, fields.get(0).getOrder());
        assertEquals(2, fields.get(1).getOrder());
    }

    @Test
    public void buildsWizardPages() {
        EventBuilder<GeneralReferral> e = new EventBuilder<>(GeneralReferral.class);
        e.field(GeneralReferral::isFeeRequired);
        e.nextPage();
        e.field(GeneralReferral::getDate);
        List<WizardPage> pages = e.build().getViewEvent().getWizardPages();
        assertEquals(2, pages.size());
        WizardPage page = pages.get(0);
        assertEquals(1, page.getOrder());
        assertEquals(1, page.getWizardPageFields().size());

        List<WizardPageField> fields = page.getWizardPageFields();
        assertEquals("feeRequired", fields.get(0).getCaseFieldId());

        page = pages.get(1);
        fields = page.getWizardPageFields();
        assertEquals("date", fields.get(0).getCaseFieldId());
        assertEquals(1, fields.get(0).getOrder());
    }
}
