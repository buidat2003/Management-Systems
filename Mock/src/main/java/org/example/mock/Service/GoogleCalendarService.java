package org.example.mock.Service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "Google Calendar API Java";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "src/main/resources/credentials_calendar.json";

    public Calendar getCalendarService() throws GeneralSecurityException, IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(CREDENTIALS_FILE_PATH))
                .createScoped(List.of("https://www.googleapis.com/auth/calendar"));
        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                new HttpCredentialsAdapter(credentials)
        ).setApplicationName(APPLICATION_NAME).build();
    }

    public String createEvent(String calendarId, String summary, String description, LocalDateTime startDateTime, LocalDateTime endDateTime) throws Exception {
        Calendar service = getCalendarService();

        // Tạo sự kiện
        Event event = new Event()
                .setSummary(summary)
                .setDescription(description);

        // Cài đặt thời gian bắt đầu và kết thúc
        EventDateTime start = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant())))
                .setTimeZone("Asia/Ho_Chi_Minh");
        EventDateTime end = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant())))
                .setTimeZone("Asia/Ho_Chi_Minh");

        event.setStart(start);
        event.setEnd(end);

        // Thêm sự kiện vào lịch
        Event createdEvent = service.events().insert(calendarId, event).execute();
        return createdEvent.getHtmlLink(); // Trả về link Google Calendar của sự kiện
    }


    public List<String> getCalendarsByEmail(String email) throws Exception {
        Calendar service = getCalendarService();

        // Lấy danh sách lịch
        CalendarList calendarList = service.calendarList().list().execute();

        System.out.println("Accessible calendars for service account:");
        for (CalendarListEntry entry : calendarList.getItems()) {
            System.out.println("- Calendar Summary: " + entry.getSummary() + ", ID: " + entry.getId());
        }

        List<String> calendarLinks = new ArrayList<>();
        for (CalendarListEntry entry : calendarList.getItems()) {
            if (entry.getSummary().equalsIgnoreCase(email)) {
                calendarLinks.add(entry.getId());
            }
        }

        return calendarLinks;
    }


}
