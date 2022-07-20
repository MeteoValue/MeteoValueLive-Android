package de.jadehs.mvl.data.models.reporting;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.LinkedList;
import java.util.Random;

import de.jadehs.mvl.data.models.routing.CurrentRouteETA;
import de.jadehs.mvl.data.models.routing.CurrentRouteETATests;

@RunWith(AndroidJUnit4.class)
public class RouteETAArchiveTest {


    private LinkedList<CurrentRouteETA> routeETAList;
    private RouteETAArchive archive;

    @Before
    public void setup() {
        int count = 10;
        Random random = new Random(20000);
        long routeId = 100;
        this.routeETAList = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            routeETAList.add(CurrentRouteETATests.makeDummyData(random, routeId));
        }
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        this.archive = new RouteETAArchive(appContext.getCacheDir(), routeId);
    }


    @Test
    public void add() throws JSONException {
        JSONArray originalETAArray = new JSONArray();
        for (CurrentRouteETA eta : this.routeETAList) {
            this.archive.add(eta);
            originalETAArray.put(eta.toJson());
        }

    }

    @Test
    public void toArrayString() throws JSONException {

        JSONArray originalETAArray = new JSONArray();
        for (CurrentRouteETA eta : this.routeETAList) {
            this.archive.add(eta);
            originalETAArray.put(eta.toJson());
        }

        String readData = this.archive.toArrayString();


        JSONArray readDataArray = new JSONArray(readData);

        assertEquals(originalETAArray.toString(), readDataArray.toString());
    }
}