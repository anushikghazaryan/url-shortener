package org.example.untitled;

import io.javalin.Javalin;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

public class HttpServer {

    private static final Map<String, String> urls = new HashMap<>();

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(9000);
        app.get("/", context -> context
                .contentType("text/html")
                .result("<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "\t<title>URL Shortener</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<form method=\"post\">\n" +
                        "\t<input name=\"url\" type=\"text\" />\n" +
                        "\t<input type=\"submit\" />\n" +
                        "</form>\n" +
                        "</body>\n" +
                        "</html>"));

        app.post("/", context -> {
            String url = context.formParam("url");
            String shortUrl = ensureUniqueness(() -> generateRandomString(10));
            urls.put(shortUrl, url);

            String html = ("<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "\t<title>URL Shortener</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<form action=\"shortify\" method=\"get\">\n" +
                    "\t<input name=\"url\" type=\"text\" />\n" +
                    "\t<input type=\"submit\" />\n" +
                    "</form>\n" +
                    "<a href=\"PLACEHOLDER\">PLACEHOLDER</a>\n" +
                    "</body>\n" +
                    "</html>").replaceAll("PLACEHOLDER", "http://localhost:9000/" + shortUrl);

            context
                    .contentType("text/html")
                    .result(html);
        });

        app.get("/:shortUrl", context -> {
            String shortUrl = context.pathParam("shortUrl");
            String targetUrl = urls.get(shortUrl);

//            context.redirect(targetUrl);
            context.header("Location", targetUrl)
                    .status(302);
        });
    }

    private static String ensureUniqueness(Supplier<String> supplier) {
        boolean isUnique = false;
        String value = null;

        while (!isUnique) {
            value = supplier.get();
            if (!urls.containsKey(value)) {
                isUnique = true;
            }
        }

        return value;
    }

    private static String generateRandomString(@SuppressWarnings("SameParameterValue") int length) {
        char[] chars = new char[length];

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char currentChar = (char) ((random.nextInt(26) + 1) | 64);
            if (Math.random() > 0.5) {
                currentChar |= 32;
            }
            chars[i] = currentChar;
        }

        return new String(chars);
    }

}
