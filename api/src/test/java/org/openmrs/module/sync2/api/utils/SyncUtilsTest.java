package org.openmrs.module.sync2.api.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.sync2.api.model.configuration.ClassConfiguration;
import org.openmrs.module.sync2.api.model.configuration.GeneralConfiguration;
import org.openmrs.module.sync2.api.model.configuration.SyncConfiguration;
import org.openmrs.module.sync2.api.model.configuration.SyncMethodConfiguration;
import org.openmrs.module.sync2.api.model.enums.AtomfeedTagContent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SyncUtilsTest {
    private static final SyncConfiguration expectedConfiguration = new SyncConfiguration();

    @Before
    public void setUp() {
        GeneralConfiguration general = new GeneralConfiguration("", "defaultAddress", false, false);
        expectedConfiguration.setGeneral(general);

        ClassConfiguration locationClass = new ClassConfiguration("Location",
                "location", "org.openmrs.Location", true);
        ClassConfiguration observationClass = new ClassConfiguration("Observation",
                "observation", "org.openmrs.Obs", true);
        List<ClassConfiguration> classes = Arrays.asList(locationClass, observationClass);

        SyncMethodConfiguration push = new SyncMethodConfiguration(true, 12, classes);
        expectedConfiguration.setPush(push);

        SyncMethodConfiguration pull = new SyncMethodConfiguration(true, 12, classes);
        expectedConfiguration.setPull(pull);
    }

    @Test
    public void checkIfParamIsEventAction_shouldReturnTrueIfParamIsEventAction() {
        Assert.assertTrue(SyncUtils.checkIfParamIsEventAction("UPDATED"));
        Assert.assertTrue(SyncUtils.checkIfParamIsEventAction("CREATED"));
        Assert.assertTrue(SyncUtils.checkIfParamIsEventAction("RETIRED"));
        Assert.assertTrue(SyncUtils.checkIfParamIsEventAction("UNRETIRED"));
        Assert.assertTrue(SyncUtils.checkIfParamIsEventAction("UNVOIDED"));
        Assert.assertTrue(SyncUtils.checkIfParamIsEventAction("DELETED"));
    }
    
    @Test
    public void checkIfParamIsEventAction_shouldReturnFalseIfParamIsNotEventAction() {
        Assert.assertFalse(SyncUtils.checkIfParamIsEventAction("updated"));
        Assert.assertFalse(SyncUtils.checkIfParamIsEventAction("somethingElse"));
    }
    
    @Test
    public void getValueOfAtomfeedEventTag_shouldReturnCategoryDespiteItsOrderInList() {
        final String expected = "patient";
        String result1 = SyncUtils.getValueOfAtomfeedEventTag(prepareDummyAtomfeedTags1(), AtomfeedTagContent.CATEGORY);
        String result2 = SyncUtils.getValueOfAtomfeedEventTag(prepareDummyAtomfeedTags2(), AtomfeedTagContent.CATEGORY);
        Assert.assertEquals(expected, result1);
        Assert.assertEquals(expected, result2);
    }
    
    @Test
    public void getValueOfAtomfeedEventTag_shouldReturnEventActionDespiteItsOrderInList() {
        final String expected = "CREATED";
        String result1 = SyncUtils.getValueOfAtomfeedEventTag(prepareDummyAtomfeedTags1(),
                AtomfeedTagContent.EVENT_ACTION);
        String result2 = SyncUtils.getValueOfAtomfeedEventTag(prepareDummyAtomfeedTags2(),
                AtomfeedTagContent.EVENT_ACTION);
        Assert.assertEquals(expected, result1);
        Assert.assertEquals(expected, result2);
    }

    private List<Object> prepareDummyAtomfeedTags1() {
        List list = new ArrayList();
        list.add("Category.schemeResolved=null\nCategory.scheme=null\nCategory.term=CREATED\nCategory.label=null\n");
        list.add("Category.schemeResolved=null\nCategory.scheme=null\nCategory.term=patient\nCategory.label=null\n");
        return list;
    }
    
    private List<Object> prepareDummyAtomfeedTags2() {
        List list = new ArrayList();
        list.add("Category.schemeResolved=null\nCategory.scheme=null\nCategory.term=patient\nCategory.label=null\n");
        list.add("Category.schemeResolved=null\nCategory.scheme=null\nCategory.term=CREATED\nCategory.label=null\n");
        return list;
    }
}