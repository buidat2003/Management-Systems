//package org.example.mock.Service;
//
//import com.google.api.client.auth.oauth2.Credential;
//import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
//import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
//import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
//import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.gson.GsonFactory;
//import com.google.api.services.calendar.Calendar;
//import com.google.api.services.calendar.model.ConferenceData;
//import com.google.api.services.calendar.model.ConferenceSolutionKey;
//import com.google.api.services.calendar.model.CreateConferenceRequest;
//import com.google.api.services.calendar.model.Event;
//import com.google.api.services.calendar.model.EventDateTime;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.FileInputStream;
//import java.io.InputStreamReader;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.Collections;
//import java.util.Date;
//
//@Service
//public class GoogleMeetService {
//
//    private static final String APPLICATION_NAME = "Automatic Sent Mail and Google Meet Calendar";
//    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
//    private static final String CREDENTIALS_FILE_PATH = "src/main/resources/credentials.json";
//
//    @Autowired
//    private UserService userService;
//
//    public String createGoogleMeetEvent(String summary, LocalDateTime startDateTime, LocalDateTime endDateTime) throws Exception {
//        Calendar service = getCalendarService();
//
//        Event event = new Event()
//                .setSummary(summary)
//                .setDescription("Cuộc phỏng vấn qua Google Meet")
//                .setConferenceData(new ConferenceData()
//                        .setCreateRequest(new CreateConferenceRequest()
//                                .setRequestId("uniqueRequestID")
//                                .setConferenceSolutionKey(new ConferenceSolutionKey().setType("hangoutsMeet"))));
//
//        EventDateTime start = new EventDateTime()
//                .setDateTime(new com.google.api.client.util.DateTime(Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant())))
//                .setTimeZone("Asia/Ho_Chi_Minh");
//        EventDateTime end = new EventDateTime()
//                .setDateTime(new com.google.api.client.util.DateTime(Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant())))
//                .setTimeZone("Asia/Ho_Chi_Minh");
//
//        event.setStart(start);
//        event.setEnd(end);
//
//        Event createdEvent = service.events().insert("primary", event)
//                .setConferenceDataVersion(1)
//                .execute();
//
//        return createdEvent.getHangoutLink(); // Trả về link Google Meet
//    }
//
//    private Calendar getCalendarService() throws Exception {
//        Credential credential = authorize();
//        return new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
//                .setApplicationName(APPLICATION_NAME)
//                .build();
//    }
//
//    private Credential authorize() throws Exception {
//        FileInputStream fileInputStream = new FileInputStream(CREDENTIALS_FILE_PATH);
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(fileInputStream));
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, Collections.singleton("https://www.googleapis.com/auth/calendar"))
//                .setAccessType("offline")
//                .build();
//
//        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8080).setCallbackPath("/oauth2callback").build();
//
//
//        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
//    }
//
//
//}

package org.example.mock.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.ConferenceData;
import com.google.api.services.calendar.model.ConferenceSolutionKey;
import com.google.api.services.calendar.model.CreateConferenceRequest;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;

@Service
public class GoogleMeetService {

    private static final String APPLICATION_NAME = "Automatic Sent Mail and Google Meet Calendar";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "src/main/resources/credentials.json";

    @Autowired
    private UserService userService;

    public String createGoogleMeetEvent(String summary, LocalDateTime startDateTime, LocalDateTime endDateTime) throws Exception {
        Calendar service = getCalendarService();

        Event event = new Event()
                .setSummary(summary)
                .setDescription("Cuộc phỏng vấn qua Google Meet")
                .setConferenceData(new ConferenceData()
                        .setCreateRequest(new CreateConferenceRequest()
                                .setRequestId("uniqueRequestID")
                                .setConferenceSolutionKey(new ConferenceSolutionKey().setType("hangoutsMeet"))));

        EventDateTime start = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant())))
                .setTimeZone("Asia/Ho_Chi_Minh");
        EventDateTime end = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant())))
                .setTimeZone("Asia/Ho_Chi_Minh");

        event.setStart(start);
        event.setEnd(end);

        Event createdEvent = service.events().insert("primary", event)
                .setConferenceDataVersion(1)
                .execute();

        return createdEvent.getHangoutLink(); // Trả về link Google Meet
    }

    private Calendar getCalendarService() throws Exception {
        Credential credential = authorize();
        return new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private Credential authorize() throws Exception {
        FileInputStream fileInputStream = new FileInputStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(fileInputStream));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, Collections.singleton("https://www.googleapis.com/auth/calendar"))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8086).setCallbackPath("/oauth2callback").build();

        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
}
