package org.example.mock.Service;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
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
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;

@Service
public class GoogleMeetService {

    private static final String APPLICATION_NAME = "Automatic Sent Mail and Google Meet Calendar";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "src/main/resources/credentials.json";
    private static final String REFRESH_TOKEN_FILE_PATH = "src/main/resources/refresh_token.txt";

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
                .setAccessType("offline") // Yêu cầu refresh_token
                .setApprovalPrompt("force") // Buộc hiển thị lại hộp thoại yêu cầu quyền
                .build();

        // Kiểm tra xem refresh token đã được lưu chưa
        String refreshToken = loadRefreshToken();
        if (refreshToken != null && !refreshToken.isEmpty()) {
            // Sử dụng refresh token để tạo Credential
            Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                    .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                    .setJsonFactory(JSON_FACTORY)
                    .setTokenServerEncodedUrl(clientSecrets.getDetails().getTokenUri())
                    .setClientAuthentication(new ClientParametersAuthentication(
                            clientSecrets.getDetails().getClientId(),
                            clientSecrets.getDetails().getClientSecret()))
                    .build();
            credential.setRefreshToken(refreshToken);
            if (credential.refreshToken()) {
                System.out.println("Access token refreshed successfully.");
            } else {
                throw new RuntimeException("Failed to refresh access token.");
            }
            return credential;
        } else {
            // Xác thực lần đầu và lưu refresh token
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8086).setCallbackPath("/oauth2callback").build();
            Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

            // Kiểm tra xem refresh token có được cấp không
            if (credential.getRefreshToken() != null) {
                System.out.println("Retrieved refresh token: " + credential.getRefreshToken());
                saveRefreshToken(credential.getRefreshToken());
            } else {
                throw new RuntimeException("Failed to retrieve refresh token. Ensure access_type=offline is set in the OAuth2 flow.");
            }
            return credential;
        }
    }




    private void saveRefreshToken(String refreshToken) throws IOException {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            Files.write(Paths.get(REFRESH_TOKEN_FILE_PATH), refreshToken.getBytes());
            System.out.println("Refresh token saved successfully.");
        } else {
            throw new RuntimeException("Failed to save refresh token: Token is null or empty.");
        }
    }

    private String loadRefreshToken() throws IOException {
        if (Files.exists(Paths.get(REFRESH_TOKEN_FILE_PATH))) {
            String token = new String(Files.readAllBytes(Paths.get(REFRESH_TOKEN_FILE_PATH)));
            System.out.println("Loaded refresh token: " + token);
            return token;
        }
        System.out.println("No refresh token found.");
        return null;
    }

}
