package korzeniowski.mateusz.app.email;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EmailRecipientCacheService {
    private static final String SESSION_KEY = "emailRecipientCache";


    private Map<String, List<String>> getCache(HttpSession session) {
        Map<String, List<String>> cache = (Map<String, List<String>>) session.getAttribute(SESSION_KEY);
        if (cache == null) {
            cache = new HashMap<>();
            session.setAttribute(SESSION_KEY, cache);
        }
        return cache;
    }

    public String storeRecipients(HttpSession session, List<String> recipients) {
        String id = UUID.randomUUID().toString();
        Map<String, List<String>> cache = getCache(session);
        cache.put(id, recipients);
        return id;
    }

    public List<String> getRecipients(HttpSession session, String id) {
        Map<String, List<String>> cache = getCache(session);
        return cache.getOrDefault(id, Collections.emptyList());
    }
}
