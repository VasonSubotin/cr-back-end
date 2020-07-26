package com.sm.client.services;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.sm.dao.LocationDao;
import com.sm.model.SmException;

import com.sm.model.SmLocation;
import com.sm.model.cache.Coordinates;
import com.sm.model.web.LocationScheduleItem;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;


public class LocationScheduleServiceImplTest {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


    @Test
    public void testCalculate() throws ParseException, SmException, IOException {

        LocationScheduleServiceImpl locationScheduleService = new LocationScheduleServiceImpl();

        GoogleLocationService googleLocationService = Mockito.mock(GoogleLocationService.class);
        locationScheduleService.setGoogleLocationService(googleLocationService);

        when(googleLocationService.getLatitudeAndLongitute("address1")).thenReturn(new Coordinates("address1", 32.891669, -97.501231));
        when(googleLocationService.getLatitudeAndLongitute("address2")).thenReturn(new Coordinates("address2", 32.886326, -97.491135));
        when(googleLocationService.getLatitudeAndLongitute("address3")).thenReturn(new Coordinates("address3", 32.872874, -97.487208));
        when(googleLocationService.getLatitudeAndLongitute("address4")).thenReturn(new Coordinates("address4", 32.880154, -97.465150));
        when(googleLocationService.getLatitudeAndLongitute("address5")).thenReturn(new Coordinates("address5", 32.890268, -97.451237));
        when(googleLocationService.getLatitudeAndLongitute("address6")).thenReturn(new Coordinates("address6", 32.891986, -97.470553));
        when(googleLocationService.getLatitudeAndLongitute("address7")).thenReturn(new Coordinates("address7", 32.911616, -97.483077));

        LocationDao locationDao = Mockito.mock(LocationDao.class);
        locationScheduleService.setLocationDao(locationDao);
        List<SmLocation> locations = makeLocations();
        when(locationDao.getLocationsInSmallRangeAndAccountId(eq(1L), anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(locations);

        Events events = generateEvenets();
        GoogleService googleService = Mockito.mock(GoogleService.class);
        when(googleService.getEvents(anyInt())).thenReturn(events);
        locationScheduleService.setGoogleService(googleService);

        List<LocationScheduleItem> actualList = locationScheduleService.calculate(1L, 1L, 300D, sdf.parse("2020-07-21T00:00:00"), sdf.parse("2020-07-22T00:00:00"));
        int index = 0;
        for (LocationScheduleItem locationScheduleItem : actualList) {
            if (index == 4) {
                index++;
            }
            Event event = events.getItems().get(index);
            Assert.assertEquals("Start date check failed " + index, locationScheduleItem.getStart().getTime(), event.getStart().getDateTime().getValue());
            Assert.assertEquals("Stop date check failed " + index, locationScheduleItem.getStop().getTime(), event.getEnd().getDateTime().getValue());


            if (index == 0) {
                Assert.assertEquals("Location Id check failed index="+index+" locIndex=0", locationScheduleItem.getLocationDistances().get(0).getLocationId(), locations.get(1).getIdLocation());
                Assert.assertEquals("Location Id check failed index="+index+" locIndex=1", locationScheduleItem.getLocationDistances().get(1).getLocationId(), locations.get(3).getIdLocation());
                Assert.assertEquals("Location Id check failed index="+index+" locIndex=2", locationScheduleItem.getLocationDistances().get(2).getLocationId(), locations.get(0).getIdLocation());
                Assert.assertEquals("Location Id check failed index="+index+" locIndex=3", locationScheduleItem.getLocationDistances().get(3).getLocationId(), locations.get(2).getIdLocation());
            }

            if (index == 1) {
                Assert.assertEquals("Location Id check failed index="+index+" locIndex=0", locationScheduleItem.getLocationDistances().get(0).getLocationId(), locations.get(5).getIdLocation());
                Assert.assertEquals("Location Id check failed index="+index+" locIndex=1", locationScheduleItem.getLocationDistances().get(1).getLocationId(), locations.get(7).getIdLocation());
                Assert.assertEquals("Location Id check failed index="+index+" locIndex=2", locationScheduleItem.getLocationDistances().get(2).getLocationId(), locations.get(6).getIdLocation());
                Assert.assertEquals("Location Id check failed index="+index+" locIndex=3", locationScheduleItem.getLocationDistances().get(3).getLocationId(), locations.get(8).getIdLocation());
                Assert.assertEquals("Location Id check failed index="+index+" locIndex=4", locationScheduleItem.getLocationDistances().get(4).getLocationId(), locations.get(4).getIdLocation());

            }
            if (index == 2) {
                Assert.assertEquals("Location Id check failed index="+index+" locIndex=0", locationScheduleItem.getLocationDistances().get(0).getLocationId(), locations.get(1).getIdLocation());
                Assert.assertEquals("Location Id check failed index="+index+" locIndex=1", locationScheduleItem.getLocationDistances().get(1).getLocationId(), locations.get(3).getIdLocation());
                Assert.assertEquals("Location Id check failed index="+index+" locIndex=2", locationScheduleItem.getLocationDistances().get(2).getLocationId(), locations.get(0).getIdLocation());
                Assert.assertEquals("Location Id check failed index="+index+" locIndex=3", locationScheduleItem.getLocationDistances().get(3).getLocationId(), locations.get(2).getIdLocation());
            }
//            if (index == 4) {
//                Assert.assertEquals("Location Id check failed index="+index+" locIndex=0", locationScheduleItem.getLocationDistances().get(0).getLocationId(), locations.get(1).getIdLocation());
//                Assert.assertEquals("Location Id check failed index="+index+" locIndex=1", locationScheduleItem.getLocationDistances().get(1).getLocationId(), locations.get(3).getIdLocation());
//                Assert.assertEquals("Location Id check failed index="+index+" locIndex=2", locationScheduleItem.getLocationDistances().get(2).getLocationId(), locations.get(0).getIdLocation());
//                Assert.assertEquals("Location Id check failed index="+index+" locIndex=3", locationScheduleItem.getLocationDistances().get(3).getLocationId(), locations.get(2).getIdLocation());
//            }
//            if (index == 5) {
//                Assert.assertEquals("Location Id check failed index="+index+" locIndex=0", locationScheduleItem.getLocationDistances().get(0).getLocationId(), locations.get(1).getIdLocation());
//                Assert.assertEquals("Location Id check failed index="+index+" locIndex=1", locationScheduleItem.getLocationDistances().get(1).getLocationId(), locations.get(3).getIdLocation());
//                Assert.assertEquals("Location Id check failed index="+index+" locIndex=2", locationScheduleItem.getLocationDistances().get(2).getLocationId(), locations.get(0).getIdLocation());
//                Assert.assertEquals("Location Id check failed index="+index+" locIndex=3", locationScheduleItem.getLocationDistances().get(3).getLocationId(), locations.get(2).getIdLocation());
//            }
            index++;
        }

    }

    private Events generateEvenets() throws ParseException {
        Events events = new Events();
        List<Event> eventList = new ArrayList<>();
        events.setItems(eventList);
        eventList.add(makeEvent("test1", "address1", "2020-07-21T01:00:00", "2020-07-21T02:00:00"));
        eventList.add(makeEvent("test2", "address2", "2020-07-21T03:00:00", "2020-07-21T04:00:00"));
        eventList.add(makeEvent("test3", "address3", "2020-07-21T06:00:00", "2020-07-21T06:00:00"));
        eventList.add(makeEvent("test4", "address4", "2020-07-21T08:00:00", "2020-07-21T10:00:00"));
        eventList.add(makeEvent("test5", "http://www.ru", "2020-07-21T12:00:00", "2020-07-21T13:00:00"));
        eventList.add(makeEvent("test6", "address6", "2020-07-21T14:00:00", "2020-07-21T15:00:00"));
        eventList.add(makeEvent("test7", "address7", "2020-07-21T17:00:00", "2020-07-21T18:00:00"));

        return events;
    }

    private Event makeEvent(String name, String address, String start, String stop) throws ParseException {
        Event event = new Event();
        event.setStart(makeEventDateTime(start));
        event.setEnd(makeEventDateTime(stop));
        event.setLocation(address);
        event.setDescription(name);
        return event;
    }

    private EventDateTime makeEventDateTime(String dtString) throws ParseException {
        EventDateTime eventDateTime = new EventDateTime();
        eventDateTime.setDateTime(new DateTime(sdf.parse(dtString)));
        return eventDateTime;
    }

    private List<SmLocation> makeLocations() {
        return Arrays.asList(
                //for address1  32.891669, -97.501231
                makeLocation(1L, "location1", 21.4, 32.890660, -97.499300),
                makeLocation(2L, "location2", 12.4, 32.891259, -97.502089),
                makeLocation(3L, "location3", 33.4, 32.891367, -97.501193),
                makeLocation(4L, "location4", 14.4, 32.891993, -97.501574),

                //for address2  32.886326, -97.491135
                makeLocation(5L, "location5", 65.4, 32.886948, -97.492648),
                makeLocation(6L, "location6", 16.4, 32.887209, -97.489869),
                makeLocation(7L, "location7", 27.4, 32.886533, -97.490604),
                makeLocation(8L, "location8", 18.4, 32.887069, -97.493072),
                makeLocation(9L, "location9", 39.4, 32.886987, -97.490092),

                //for address3  32.872874, -97.487208
                makeLocation(10L, "location10", 20.4, 32.873242, -97.486217),
                makeLocation(11L, "location11", 21.4, 32.872514, -97.488093),
                makeLocation(12L, "location12", 22.4, 32.872401, -97.487256),

                //for address4  32.880154, -97.465150
                makeLocation(13L, "location13", 23.4, 32.879334, -97.466588),
                makeLocation(14L, "location14", 14.4, 32.880127, -97.462350),
                makeLocation(15L, "location15", 25.4, 32.881290, -97.463981),
                makeLocation(16L, "location16", 36.4, 32.882119, -97.465375),

                //for address5   32.890268, -97.451237
                makeLocation(17L, "location17", 27.4, 32.889903, -97.451948),
                makeLocation(18L, "location18", 17.4, 32.890110, -97.450711),

                //for address6  32.891986, -97.470553
                makeLocation(19L, "location19", 67.4, 32.891331, -97.471535),
                makeLocation(20L, "location20", 58.4, 32.891434, -97.469899),
                makeLocation(21L, "location21", 49.4, 32.892056, -97.468783),
                makeLocation(22L, "location22", 30.4, 32.893511, -97.469335),
                makeLocation(23L, "location23", 21.4, 32.892772, -97.470802),

                //for address7   32.911616, -97.483077
                makeLocation(24L, "location24", 12.4, 32.910839, -97.484271),
                makeLocation(25L, "location25", 23.4, 32.911001, -97.481336),
                makeLocation(26L, "location26", 14.4, 32.912019, -97.482012),
                makeLocation(27L, "location27", 25.4, 32.912604, -97.484812)
        );
    }

    private SmLocation makeLocation(long id, String name, double price, double latitude, double longitude) {
        SmLocation smLocation = new SmLocation();
        smLocation.setIdLocation(id);
        smLocation.setPrice(price);
        smLocation.setName(name);
        smLocation.setLatitude(latitude);
        smLocation.setLongitude(longitude);
        return smLocation;
    }
}

