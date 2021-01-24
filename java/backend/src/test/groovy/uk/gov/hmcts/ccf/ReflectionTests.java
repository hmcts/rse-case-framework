package uk.gov.hmcts.ccf;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewField;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewTab;
import uk.gov.hmcts.ccd.domain.model.definition.CaseFieldDefinition;
import uk.gov.hmcts.ccf.definition.ICaseView;
import uk.gov.hmcts.ccf.types.fields.HasDate;
import uk.gov.hmcts.ccf.types.fields.TestAddress;
import uk.gov.hmcts.ccf.types.fields.TwoFields;
import uk.gov.hmcts.ccf.types.model.FakeCCDImplementation;
import uk.gov.hmcts.ccf.types.model.FakeCase;
import uk.gov.hmcts.ccf.types.model.FakeState;
import uk.gov.hmcts.ccf.types.model.FakeView;
import uk.gov.hmcts.ccf.types.model.Party;
import uk.gov.hmcts.ccf.types.nested.WithSubClass;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class ReflectionTests {


    @Test
    public void generatesCaseFieldDefinition() {
        List<CaseFieldDefinition> result = ReflectionUtils.getCaseListFields(TwoFields.class);
        assertThat(result.size()).isEqualTo(1);
        CaseFieldDefinition first = result.get(0);
        assertThat(first.getFieldTypeDefinition().getType()).isEqualTo("Text");
        assertThat(first.getId()).isEqualTo("fooBar");
        assertThat(first.getLabel()).isEqualTo("Foo Bar");
    }

    @Test
    public void extractsSubClassFields() {
        List<CaseFieldDefinition> result = ReflectionUtils.getCaseListFields(WithSubClass.class);
        assertThat(result.size()).isEqualTo(2);
        CaseFieldDefinition first = result.get(0);
        assertThat(first.getFieldTypeDefinition().getType()).isEqualTo("Text");
        assertThat(first.getId()).isEqualTo("fooBar");
        assertThat(first.getLabel()).isEqualTo("Foo Bar");

        CaseFieldDefinition second = result.get(1);
        assertThat(second.getFieldTypeDefinition().getType()).isEqualTo("Text");
        assertThat(second.getId()).isEqualTo("line1");
        assertThat(second.getLabel()).isEqualTo("Nested field");
    }

    @Test
    public void getsView() {
        WithSubClass w = new WithSubClass();
        Map<String, Object> result = ReflectionUtils.getCaseListViewModel(w);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).isEqualTo(ImmutableMap.of(
            "fooBar", "foo",
            "line1", "test line 1"
        ));
    }

    @Test
    public void extractsCaseStates() {
        ImmutableSet<? extends Enum> states = ReflectionUtils.extractStates(FakeCase.class);
        assertThat(states).isEqualTo(ImmutableSet.copyOf(
            FakeState.values()
        ));
    }

    @Test
    public void mapsComplextype() {
        CaseFieldDefinition field = ReflectionUtils.mapComplexType(TestAddress.class, new TestAddress());
        assertThat(field.getFieldTypeDefinition().getType()).isEqualTo("Complex");
        assertThat(field.getFieldTypeDefinition().getComplexFields().size()).isGreaterThanOrEqualTo(3);
        assertThat(field.getFieldTypeDefinition().getComplexFields().get(0).getLabel()).isEqualTo("test");
    }
    @Test
    public void mapsNestedComplexType() {
        CaseFieldDefinition field = ReflectionUtils.mapComplexType(Party.class, new Party());
        assertThat(field.getFieldTypeDefinition().getType()).isEqualTo("Complex");
        assertThat(field.getFieldTypeDefinition().getComplexFields().size()).isGreaterThanOrEqualTo(2);
        assertThat(field.getFieldTypeDefinition().getComplexFields().get(1).getFieldTypeDefinition().getType()).isEqualTo("Complex");
    }

    @Test
    public void extractsCaseType() {
        assertThat(ReflectionUtils.getCaseType(FakeCCDImplementation.class)).isEqualTo(FakeCase.class);
    }

    @Test
    public void extractsDate() {
        List<CaseFieldDefinition> fields = ReflectionUtils.getCaseListFields(HasDate.class);
        assertThat(fields.size()).isEqualTo(3);
        assertThat(fields.get(0).getFieldTypeDefinition().getType()).isEqualTo("Date");
        assertThat(fields.get(1).getFieldTypeDefinition().getType()).isEqualTo("Date");
        assertThat(fields.get(2).getFieldTypeDefinition().getType()).isEqualTo("Date");
    }

    @Test
    public void testRhubarbTabView() {
        List<ICaseView> views = Lists.newArrayList(new FakeView());
        CaseViewTab[] result = ReflectionUtils.generateCaseViewTabs(FakeCase.C, views);
        assertThat(result.length).isEqualTo(1);
        CaseViewTab addressTab = result[0];
        // TODO
//        assertThat(addressTab.getFields().length).isEqualTo(2);
//        CaseViewField vf = addressTab.getFields()[1];
//
//        assertThat(vf.getFieldTypeDefinition().getComplexFields().size()).isGreaterThanOrEqualTo(2);
    }

}
